from sys import argv, stdin, stdout, stderr, exit
from struct import unpack, calcsize

CONSTANT_Utf8 = 1
CONSTANT_Integer = 3
CONSTANT_Float = 4
CONSTANT_Long = 5
CONSTANT_Double = 6
CONSTANT_Class = 7
CONSTANT_String = 8
CONSTANT_Fieldref = 9
CONSTANT_Methodref = 10
CONSTANT_InterfaceMethodref = 11
CONSTANT_NameAndType = 12

def main(input, output):
    """
    input : str - such as open(filename, "r").read()
    output : file - such as stdout
    return : str - error message or None
    """
    
    # this is a bound variable :|
    index = [0]
    
    def str2hex(string):
        return "".join(hex(ord(c))[2:].zfill(2) + " " for c in string)
    def read(fmt):
        size = calcsize(fmt)
        valueStr = input[index[0]:index[0] + size]
        value = unpack(fmt, valueStr)[0]
        hexStr = str2hex(valueStr)
        index[0] += size
        return (hexStr, value)
    def readByte():
        return read(">b")
    def readShort():
        return read(">h")
    def readInt():
        return read(">i")
    def readUInt():
        return read(">I")
    def readFloat():
        return read(">f")
    def readLong():
        return read(">l")
    def readDouble():
        return read(">d")
    def readString(size):
        value = input[index[0]:index[0] + size]
        hexStr = str2hex(value)
        index[0] += size
        return (hexStr, value)
    
    # magic number
    (h, magic) = readUInt()
    if magic != 0xcafebabe:
        return "wrong magic number: " + str(magic)
    output.write(h + "\n\n")
    
    # version
    (h1, minor) = readShort()
    (h2, major) = readShort()
    # http://blogs.sun.com/sundararajan/entry/thou_shall_know_the_class
    versions = {48:"1.4.2", 49: "1.5", 50:"6"}
    if major < 48:
        version = "1.3.1"
    elif major > 50:
        version = "7"
    else:
        version = versions[major]
    output.write("; classfile version %i.%i - JRE version %s\n" % (major, minor, version))
    output.write("%s\n%s\n\n" % (h1, h2))

    # constant pool ( http://java.sun.com/docs/books/jvms/second_edition/html/ClassFile.doc.html#20080 )
    (h, constantPoolSize) = readShort()
    output.write("; constant pool; size=%i\n" % constantPoolSize)
    output.write(h + "\n")
    constantPool = [None] # 1 - based
    constantPoolHex = [None]
    i = 1
    while i < constantPoolSize:
        (typeHex, type) = readByte()
        if type == CONSTANT_Utf8:
            (sizeHex, size) = readShort()
            (valueHex, value) = readString(size)
            constantPool.append((type, value))
            constantPoolHex.append((typeHex, sizeHex, valueHex))
            i += 1
        elif type == CONSTANT_Integer:
            (h, value) = readInt()
            constantPool.append((type, value))
            constantPoolHex.append((typeHex, h))
            i += 1
        elif type == CONSTANT_Float:
            (h, value) = readFloat()
            constantPool.append((type, value))
            constantPoolHex.append((typeHex, h))
            i += 1
        elif type == CONSTANT_Long:
            (h, value) = readLong()
            constantPool.append((type, value))
            constantPoolHex.append((typeHex, h))
            constantPool.append(None)
            constantPoolHex.append(None)
            i += 2
        elif type == CONSTANT_Double:
            (h, value) = readDouble()
            constantPool.append((type, value))
            constantPoolHex.append((typeHex, h))
            constantPool.append(None)
            constantPoolHex.append(None)
            i += 2
        elif type == CONSTANT_Class:
            (h, nameIndex) = readShort()
            constantPool.append((type, nameIndex))
            constantPoolHex.append((typeHex, h))
            i += 1
        elif type == CONSTANT_String:
            (h, stringIndex) = readShort()
            constantPool.append((type, stringIndex))
            constantPoolHex.append((typeHex, h))
            i += 1
        elif type in (CONSTANT_Fieldref, CONSTANT_Methodref, CONSTANT_InterfaceMethodref):
            (h1, classIndex) = readShort()
            (h2, nameAndTypeIndex) = readShort()
            constantPool.append((type, classIndex, nameAndTypeIndex))
            constantPoolHex.append((typeHex, h1, h2))
            i += 1
        elif type == CONSTANT_NameAndType:
            (h1, nameIndex) = readShort()
            (h2, descriptorIndex) = readShort()
            constantPool.append((type, nameIndex, descriptorIndex))
            constantPoolHex.append((typeHex, h1, h2))
            i += 1
        else:
            return "error: constant code " + str(type) + " for index " + str(index[0])+":"+str(i) + " is not real"

    # verify and print constant pool
    i = 1
    while i < constantPoolSize:
        output.write("\t; %i - " % i)
        type = constantPool[i][0]
        hexStrs = constantPoolHex[i]
        if type == CONSTANT_Utf8:
            value = constantPool[i][1]
            output.write("Utf8 = \"%s\"\n" % value)
            i += 1
        elif type in (CONSTANT_Integer, CONSTANT_Long, CONSTANT_Float, CONSTANT_Double):
            (constantTypeName, size) = {
                CONSTANT_Integer:("Integer",1),
                CONSTANT_Long:("Long",2),
                CONSTANT_Float:("Float",1),
                CONSTANT_Double:("Double",2),
            }[type]
            value = constantPool[i][1]
            output.write("%s = %s\n" % (constantTypeName, str(value)))
            i += size
        elif type == CONSTANT_Class:
            nameIndex = constantPool[i][1]
            assert(constantPool[nameIndex][0] == CONSTANT_Utf8)
            className = constantPool[nameIndex][1]
            output.write("Class = %s\n" % className)
            i += 1
        elif type == CONSTANT_String:
            stringIndex = constantPool[i][1]
            assert(constantPool[stringIndex][0] == CONSTANT_Utf8)
            value = constantPool[stringIndex][1]
            output.write("String = %s\n" % value)
            i += 1
        elif type in (CONSTANT_Fieldref, CONSTANT_Methodref, CONSTANT_InterfaceMethodref):
            (classIndex, nameAndTypeIndex) = constantPool[i][1:]
            assert(constantPool[classIndex][0] == CONSTANT_Class)
            assert(constantPool[nameAndTypeIndex][0] == CONSTANT_NameAndType)
            className = constantPool[constantPool[classIndex][1]][1]
            nameAndTypeIndexes = constantPool[nameAndTypeIndex][1:]
            nameAndType = constantPool[nameAndTypeIndexes[0]][1] + constantPool[nameAndTypeIndexes[1]][1]
            constantTypeName = {
                CONSTANT_Fieldref:"Fieldref", 
                CONSTANT_Methodref:"Methodref", 
                CONSTANT_InterfaceMethodref:"InterfaceMethodref",
            }[type]
            output.write("Methodref = %s.%s\n" % (className, nameAndType))
            i += 1
        elif type == CONSTANT_NameAndType:
            (nameIndex, descriptorIndex) = constantPool[i][1:]
            assert(constantPool[nameIndex][0] == CONSTANT_Utf8)
            assert(constantPool[descriptorIndex][0] == CONSTANT_Utf8)
            name = constantPool[nameIndex][1]
            descriptor = constantPool[descriptorIndex][1]
            output.write("NameAndType = %s%s\n" % (name, descriptor))
            i += 1
        else:
            return "omgwtf!ddd!!"
        output.write("".join("\t%s\n" % hexStr for hexStr in hexStrs) + "\t\n")
    
    # class declaration
    

if __name__ == "__main__":
    if len(argv) == 1:
        input = stdin.read()
    else:
        input = open(argv[1], "r").read()
    
    errorMessage = main(input, stdout)
    returnCode = 0
    if errorMessage != None:
        stderr.write(errorMessage + "\n")
        returnCode = 1
    exit(returnCode)

