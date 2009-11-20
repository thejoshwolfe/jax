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

ACC_PUBLIC = 0x0001
ACC_PRIVATE = 0x0002
ACC_PROTECTED = 0x0002
ACC_STATIC = 0x0008
ACC_FINAL = 0x0010
ACC_SUPER = ACC_SYNCHRONIZED = 0x0020
ACC_VOLATILE = 0x0040
ACC_TRANSIENT = 0x0080
ACC_NATIVE = 0x0100
ACC_INTERFACE = 0x0200
ACC_ABSTRACT = 0x0400
ACC_STRICT = 0x0800

def main(input, output):
    """
    input : str - such as open("Example.class", "r").read()
    output : file - such as stdout or anything with a method write(str)
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
            nameAndType = constantPool[nameAndTypeIndexes[0]][1] + ":" + constantPool[nameAndTypeIndexes[1]][1]
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
            output.write("NameAndType = %s:%s\n" % (name, descriptor))
            i += 1
        else:
            return "omgwtf!!!"
        output.write("".join("\t%s\n" % hexStr for hexStr in hexStrs) + "\t\n")
    
    # class declaration
    (h1, accessFlags) = readShort()
    output.write("; ")
    if accessFlags & ACC_PUBLIC:
        output.write("public ")
    if accessFlags & ACC_FINAL:
        output.write("final ")
    if accessFlags & ACC_INTERFACE:
        output.write("interface ")
    else:
        if accessFlags & ACC_ABSTRACT:
            output.write("abstract ")
        output.write("class ")
    (h2, classIndex) = readShort()
    output.write(constantPool[constantPool[classIndex][1]][1] + " ")
    (h3, superClassIndex) = readShort()
    output.write("extends " + constantPool[constantPool[superClassIndex][1]][1])
    # interfaces
    (h4, interfaceCount) = readShort()
    interfacesHexStr = ""
    if interfaceCount != 0:
        output.write(" implements ")
        interfaceNames = []
        for i in range(interfaceCount):
            (h, classIndex) = readShort()
            interfaceNames.append(constantPool[constantPool[classIndex][1]][1])
            interfacesHexStr += "\t" + h + "\n"
        output.write(", ".join(interfaceNames))
    output.write(("\n" + "%s\n" * 4 + "%s\n") % (h1, h2, h3, h4, interfacesHexStr))

    # function for reading attributes on fields, methods, and at the end of the class
    def readAttribute(indentation):
        (h1, nameIndex) = readShort()
        name = constantPool[nameIndex][1]
        (h2, length) = readInt()
        output.write("%s; attribute \"%s\"; size=%i\n" % (indentation, name, length))
        output.write(((indentation + "%s\n") * 2) % (h1, h2))
        if False:
            pass
        else:
            # unknown attribute name
            (h, _) = readString(length)
            output.write(indentation + "\t" + h)
        output.write(indentation + "\n")
    
    # fields
    (h, fieldCount) = readShort()
    output.write("; fields; size=%i\n" % fieldCount)
    output.write(h + "\n")
    for i in range(fieldCount):
        (h1, accessFlags) = readShort()
        (h2, nameIndex) = readShort()
        (h3, descriptorIndex) = readShort()
        output.write("\t; ")
        if accessFlags & ACC_PUBLIC:
            output.write("public ")
        elif accessFlags & ACC_PRIVATE:
            output.write("private ")
        elif accessFlags & ACC_PROTECTED:
            output.write("protected ")
        if accessFlags & ACC_STATIC:
            output.write("static ")
        if accessFlags & ACC_FINAL:
            output.write("final ")
        if accessFlags & ACC_VOLATILE:
            output.write("volitile ")
        if accessFlags & ACC_TRANSIENT:
            output.write("transient ")
        output.write(constantPool[nameIndex][1] + ":" + constantPool[descriptorIndex][1] + "\n")
        output.write(("\t%s\n" * 3) % (h1, h2, h3))
        (h, attributeCount) = readShort()
        output.write("\t; attributes; size=%i\n" % attributeCount)
        output.write("\t" + h + "\n")
        for i in range(attributeCount):
            readAttribute("\t\t")
        output.write("\t\n")
    

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

