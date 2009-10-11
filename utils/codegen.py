from sys import stdin

packages = {}

for line in stdin:
    line = line[:-1]
    index = line.rfind(".")
    packageName = line[:index]
    className = line[index+1:]
    if packageName in packages:
         classList = packages[packageName]
    else:
         classList = []
         packages[packageName] = classList
    classList.append(className)

keys = packages.keys()
keys.sort()
for packageName in keys:
    print("        packages.put(\"" + packageName + "\", new String[] { " + ", ".join(['"' + c + '"' for c in packages[packageName]]) +" });")




