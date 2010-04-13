#!/usr/bin/env python

import sys

input = open(sys.argv[1], "r")
outputContent = " ".join(hex(ord(c))[2:].zfill(2) for c in input.read())
input.close()

if len(sys.argv) == 2:
    print(outputContent)
else:
    output = open(sys.argv[2], "w")
    output.write(outputContent)
    output.close()

