
from sys import argv

input = open(argv[1], "r")
s = input.read()
input.close()

def pad(thing):
    if len(thing) == 1: return "0" + thing
    return thing

output = " ".join(pad(hex(ord(c))[2:]) for c in s)

print(output)

