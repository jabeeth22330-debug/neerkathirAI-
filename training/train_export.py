"""Train a compact tomato classifier and export it to ExecuTorch (.pte) for Arm.
Data: data/{train,val,test}/<class>/*.jpg  (classes: healthy, early_blight, late_blight, septoria, leaf_curl, other_unknown)
IMPORTANT: split by plant/field/session, NOT randomly per photo, or accuracy will be inflated.
pip install torch torchvision executorch
"""
import torch, torch.nn as nn
from torchvision import datasets, transforms, models
def T(train):
    a=[transforms.RandomResizedCrop(224,(0.7,1)),transforms.RandomHorizontalFlip(),transforms.ColorJitter(.3,.3,.3)] if train else [transforms.Resize((224,224))]
    return transforms.Compose(a+[transforms.ToTensor(),transforms.Normalize([.485,.456,.406],[.229,.224,.225])])
tr=datasets.ImageFolder("data/train",T(True)); va=datasets.ImageFolder("data/val",T(False)); te=datasets.ImageFolder("data/test",T(False))
L=lambda d,s: torch.utils.data.DataLoader(d,batch_size=32,shuffle=s)
m=models.mobilenet_v3_small(weights="DEFAULT"); m.classifier[3]=nn.Linear(m.classifier[3].in_features,len(tr.classes))
opt=torch.optim.AdamW(m.parameters(),1e-3); lf=nn.CrossEntropyLoss()
def acc(d):
    m.eval(); c=n=0
    with torch.no_grad():
        for x,y in L(d,False): c+=(m(x).argmax(1)==y).sum().item(); n+=len(y)
    return c/n
for e in range(15):
    m.train()
    for x,y in L(tr,True): opt.zero_grad(); lf(m(x),y).backward(); opt.step()
    print(e,"val acc",acc(va))
print("TEST acc (report this):",acc(te)); open("classes.txt","w").write("\n".join(tr.classes))
from executorch.exir import to_edge_transform_and_lower
from executorch.backends.xnnpack.partition.xnnpack_partitioner import XnnpackPartitioner
m.eval(); ex=(torch.randn(1,3,224,224),)
prog=to_edge_transform_and_lower(torch.export.export(m,ex),partitioner=[XnnpackPartitioner()]).to_executorch()
open("tomato_mobilenet.pte","wb").write(prog.buffer); print("saved tomato_mobilenet.pte")
# Next: calibrate confidence (temperature scaling on val set) and set the 'unknown' threshold.
