# execute this script in a classpath to find all public types (class/interface/enum).
# fully-qualified type names are printed on stdout 1 per line.
# example output line:
# java.lang.String
#
# written by Josh Wolfe (thejoshwolfe at gmail)

import os
import re

def listRecursive(dirPath):
    for name in os.listdir(dirPath):
        fullPath = os.path.join(dirPath, name)
        if os.path.isfile(fullPath):
            yield fullPath
        else:
            for subItem in listRecursive(fullPath):
                yield subItem


headerPatterns = (
    "package.+?;",          # package statement
    "import.+?;",           # import statement
    r"//.*?\n",             # line comments
    "/\*.*?\*/",            # block comments
    "@[\w.]+(\s*\(.+?\))?", # annotations
)

headerPattern = r"^(\s|" + "|".join(["(" + pat + ")" for pat in headerPatterns]) + ")*"

headerRegex = re.compile(headerPattern, re.DOTALL)

typenameRegex = re.compile("class|interface|enum")

def main():
    for fileName in listRecursive("."):
        if not fileName.endswith(".java"): continue
        f = open(fileName, "r")
        s = f.read()
        m = headerRegex.match(s)
        afterHeader = s[m.end():]
        if len(afterHeader) == 0: continue
        m = typenameRegex.search(afterHeader)
        modifiers = afterHeader[:m.start()]
        if modifiers.endswith("@"): continue # no support for annotations
        if "public" in modifiers:
             print(fileName[2:-5].replace("/", "."))

if __name__ == "__main__":
    main()

