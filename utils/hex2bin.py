import sys
import re

input = open(sys.argv[1], "r")
inputContent = input.read()
input.close()

tokenPattern = r"(\s+)|(;.*$)|([0-9a-fA-F]{2})"
matches = re.findall(tokenPattern, inputContent, re.MULTILINE)

outputContent = ""
for match in matches:
    digits = match[2]
    if digits == "":
        continue
    outputContent += chr(int(digits, 16))

output = open(sys.argv[2], "w")
output.write(outputContent)
output.close()

