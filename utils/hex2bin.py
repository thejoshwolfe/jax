from sys import argv, stdin, stdout, stderr, exit
import re

tokenPattern = re.compile(r"([0-9a-fA-F]{2})|(\s+)|(;.*$)", re.MULTILINE)
newlinePattern = re.compile(r"\r\n|[\n\r]")

def main(input, output):
    """
    input  : str - such as open("Example.class", "r").read()
    output : file - such as stdout or anything with a method write(str)
    return : str - error message or None
    """
    offset = 0
    lineNumber = 1 # for error reporting
    while offset < len(input):
        match = tokenPattern.match(input, offset)
        if match == None:
            return "You're doing it wrong in line %i" % lineNumber
        hexText = match.group(1)
        if hexText != None:
            output.write(chr(int(hexText, 16)))
        whitespace = match.group(2)
        if whitespace != None:
            lineNumber += len(newlinePattern.findall(whitespace))
        offset = match.end()

if __name__ == "__main__":
    if len(argv) == 1:
        input = stdin.read()
    elif len(argv) == 2:
        input = open(argv[1], "r").read()
    else:
        stderr.write("too many arguments\n")
        exit(1)
    
    errorMessage = main(input, stdout)
    returnCode = 0
    if errorMessage != None:
        stderr.write(errorMessage + "\n")
        returnCode = 1
    exit(returnCode)
