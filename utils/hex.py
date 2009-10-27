import sys

input = open(sys.argv[1], "r")
output = " ".join(hex(ord(c))[2:].zfill(2) for c in input.read())
input.close()

print(output)

