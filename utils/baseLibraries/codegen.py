import sys

def main(arg):
    topLevelPackages = {}
    for line in open(arg, "rU").read().split("\n"):
        if line == "":
            continue
        qualName = line.split(".")
        currentPackage = topLevelPackages
        className = line[index+1:]
        if packageName in packages:
             classList = packages[packageName]
        else:
             classList = []
             packages[packageName] = classList
        classList.append(className)
    for topLevelPackage in sorted(topLevelPackages):
        print("PackageName %(name) = PackageName.makeTopLevelPackage(\"%(name)\");" % {"name": topLevelPackage})
        for shortName in sorted(topLevelPackages[topLevelPackage]):
            fullItem = fullParent + "." + item
            if type(item) == dict:
                print("PackageName %s = %s.makeSubPackage(\"%s\");" % (dot2under(item), )
                for asf in asfsdf:
                    recusion
            else:
                print("%(super).putTypeName(\"%(name)\");" % templateArgs)

def dot2under(name):
    return name.replace(".", "_")

if __name__ == "__main__":
    main(*sys.argv[1:])
