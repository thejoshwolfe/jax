#!/usr/bin/env python

import sys

input = open(sys.argv[1], "rU")
content = input.read()
input.close()
output = open(sys.argv[1], "w")
output.write(content)
output.close()
