#!/usr/bin/env python

import os, sys
import re

pattern = re.compile("public static final int TYPE = 0x(.*?);")

def flattenFiles(rootPath):
    for (root, dirs, files) in os.walk(rootSearchPath):
        for file in files:
            yield os.path.join(root, file)

def readFile(path):
    file = open(path, "r")
    content = file.read()
    file.close()
    return content

def main(rootSearchPath):
    found = set()
    status = 0
    for filePath in flattenFiles(rootSearchPath):
        matches = pattern.findall(readFile(filePath))
        for match in matches:
            match = match.lower()
            if match in found:
                sys.stderr.write(match)
                sys.stderr.write("\n")
                status = 1
            else:
                found.add(match)
    return status

if __name__ == "__main__":
    [rootSearchPath] = sys.argv[1:]
    sys.exit(main(rootSearchPath))
