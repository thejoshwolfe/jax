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



notSupported = "notSupported"
noArguments = "noArguments"
byteImmediate = "byteImmediate"
shortImmediate = "shortImmediate"
varientConstant = "varientConstant"
varientConstant_w = "varientConstant_w"
localVariableIndex = "localVariableIndex"
branchOffset = "branchOffset"
branchOffset_w = "branchOffset_w"
tableswitch = "tableswitch"
lookupswitch = "lookupswitch"
fieldrefIndex = "fieldrefIndex"
methodrefIndex = "methodrefIndex"
interfaceMethodref = "interfaceMethodrefIndex"
classIndex = "classIndex"
atypeCode = "atypeCode"
wideArguments = "wideArguments"
multianewarray = "multianewarray"

# index is opcode
instructions = [
    ("nop", noArguments),
    ("aconst_null", noArguments),
    ("iconst_m1", noArguments),
    ("iconst_0", noArguments),
    ("iconst_1", noArguments),
    ("iconst_2", noArguments),
    ("iconst_3", noArguments),
    ("iconst_4", noArguments),
    ("iconst_5", noArguments),
    ("lconst_0", noArguments),
    ("lconst_1", noArguments),
    ("fconst_0", noArguments),
    ("fconst_1", noArguments),
    ("fconst_2", noArguments),
    ("dconst_0", noArguments),
    ("dconst_1", noArguments),
    ("bipush", byteImmediate),
    ("sipush", shortImmediate),
    ("ldc", varientConstant),
    ("ldc_w", varientConstant_w),
    ("ldc2_w", varientConstant_w),
    ("iload", localVariableIndex),
    ("lload", localVariableIndex),
    ("fload", localVariableIndex),
    ("dload", localVariableIndex),
    ("aload", localVariableIndex),
    ("iload_0", noArguments),
    ("iload_1", noArguments),
    ("iload_2", noArguments),
    ("iload_3", noArguments),
    ("lload_0", noArguments),
    ("lload_1", noArguments),
    ("lload_2", noArguments),
    ("lload_3", noArguments),
    ("fload_0", noArguments),
    ("fload_1", noArguments),
    ("fload_2", noArguments),
    ("fload_3", noArguments),
    ("dload_0", noArguments),
    ("dload_1", noArguments),
    ("dload_2", noArguments),
    ("dload_3", noArguments),
    ("aload_0", noArguments),
    ("aload_1", noArguments),
    ("aload_2", noArguments),
    ("aload_3", noArguments),
    ("iaload", noArguments),
    ("laload", noArguments),
    ("faload", noArguments),
    ("daload", noArguments),
    ("aaload", noArguments),
    ("baload", noArguments),
    ("caload", noArguments),
    ("saload", noArguments),
    ("istore", localVariableIndex),
    ("lstore", localVariableIndex),
    ("fstore", localVariableIndex),
    ("dstore", localVariableIndex),
    ("astore", localVariableIndex),
    ("istore_0", noArguments),
    ("istore_1", noArguments),
    ("istore_2", noArguments),
    ("istore_3", noArguments),
    ("lstore_0", noArguments),
    ("lstore_1", noArguments),
    ("lstore_2", noArguments),
    ("lstore_3", noArguments),
    ("fstore_0", noArguments),
    ("fstore_1", noArguments),
    ("fstore_2", noArguments),
    ("fstore_3", noArguments),
    ("dstore_0", noArguments),
    ("dstore_1", noArguments),
    ("dstore_2", noArguments),
    ("dstore_3", noArguments),
    ("astore_0", noArguments),
    ("astore_1", noArguments),
    ("astore_2", noArguments),
    ("astore_3", noArguments),
    ("iastore", noArguments),
    ("lastore", noArguments),
    ("fastore", noArguments),
    ("dastore", noArguments),
    ("aastore", noArguments),
    ("bastore", noArguments),
    ("castore", noArguments),
    ("sastore", noArguments),
    ("pop", noArguments),
    ("pop2", noArguments),
    ("dup", noArguments),
    ("dup_x1", noArguments),
    ("dup_x2", noArguments),
    ("dup2", noArguments),
    ("dup2_x1", noArguments),
    ("dup2_x2", noArguments),
    ("swap", noArguments),
    ("iadd", noArguments),
    ("ladd", noArguments),
    ("fadd", noArguments),
    ("dadd", noArguments),
    ("isub", noArguments),
    ("lsub", noArguments),
    ("fsub", noArguments),
    ("dsub", noArguments),
    ("imul", noArguments),
    ("lmul", noArguments),
    ("fmul", noArguments),
    ("dmul", noArguments),
    ("idiv", noArguments),
    ("ldiv", noArguments),
    ("fdiv", noArguments),
    ("ddiv", noArguments),
    ("irem", noArguments),
    ("lrem", noArguments),
    ("frem", noArguments),
    ("drem", noArguments),
    ("ineg", noArguments),
    ("lneg", noArguments),
    ("fneg", noArguments),
    ("dneg", noArguments),
    ("ishl", noArguments),
    ("lshl", noArguments),
    ("ishr", noArguments),
    ("lshr", noArguments),
    ("iushr", noArguments),
    ("lushr", noArguments),
    ("iand", noArguments),
    ("land", noArguments),
    ("ior", noArguments),
    ("lor", noArguments),
    ("ixor", noArguments),
    ("lxor", noArguments),
    ("iinc", noArguments),
    ("i2l", noArguments),
    ("i2f", noArguments),
    ("i2d", noArguments),
    ("l2i", noArguments),
    ("l2f", noArguments),
    ("l2d", noArguments),
    ("f2i", noArguments),
    ("f2l", noArguments),
    ("f2d", noArguments),
    ("d2i", noArguments),
    ("d2l", noArguments),
    ("d2f", noArguments),
    ("i2b", noArguments),
    ("i2c", noArguments),
    ("i2s", noArguments),
    ("lcmp", noArguments),
    ("fcmpl", noArguments),
    ("fcmpg", noArguments),
    ("dcmpl", noArguments),
    ("dcmpg", noArguments),
    ("ifeq", branchOffset),
    ("ifne", branchOffset),
    ("iflt", branchOffset),
    ("ifge", branchOffset),
    ("ifgt", branchOffset),
    ("ifle", branchOffset),
    ("if_icmpeq", branchOffset),
    ("if_icmpne", branchOffset),
    ("if_icmplt", branchOffset),
    ("if_icmpge", branchOffset),
    ("if_icmpgt", branchOffset),
    ("if_icmple", branchOffset),
    ("if_acmpeq", branchOffset),
    ("if_acmpne", branchOffset),
    ("goto", branchOffset),
    ("jsr", branchOffset),
    ("ret", localVariableIndex),
    ("tableswitch", tableswitch),
    ("lookupswitch", lookupswitch),
    ("ireturn", noArguments),
    ("lreturn", noArguments),
    ("freturn", noArguments),
    ("dreturn", noArguments),
    ("areturn", noArguments),
    ("return", noArguments),
    ("getstatic", fieldrefIndex),
    ("putstatic", fieldrefIndex),
    ("getfield", fieldrefIndex),
    ("putfield", fieldrefIndex),
    ("invokevirtual", methodrefIndex),
    ("invokespecial", methodrefIndex),
    ("invokestatic", methodrefIndex),
    ("invokeinterface", interfaceMethodref),
    ("invokedynamic", notSupported),
    ("new", classIndex),
    ("newarray", atypeCode),
    ("anewarray", classIndex),
    ("arraylength", noArguments),
    ("athrow", noArguments),
    ("checkcast", classIndex),
    ("instanceof", classIndex),
    ("monitorenter", noArguments),
    ("monitorexit", noArguments),
    ("wide", wideArguments),
    ("multianewarray", multianewarray),
    ("ifnull", branchOffset),
    ("ifnonnull", branchOffset),
    ("goto_w", branchOffset_w),
    ("jsr_w", branchOffset_w),
    ("breakpoint", noArguments),
]


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
            output.write("String = \"%s\"\n" % value)
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

    # function for calling readAttribute()
    def readAttributes(attributeCount, indentation):
        for i in range(attributeCount):
            errorMessage = readAttribute(indentation)
            if errorMessage != None:
                return errorMessage
            if i < attributeCount:
                output.write(indentation + "\n")
    
    # function for reading attributes on fields, methods, and at the end of the class
    def readAttribute(indentation):
        (h1, nameIndex) = readShort()
        name = constantPool[nameIndex][1]
        (h2, attributeLength) = readInt()
        output.write("%s; attribute \"%s\"; size=%i\n" % (indentation, name, attributeLength))
        output.write(((indentation + "%s\n") * 2) % (h1, h2))
        if name == "ConstantValue":
            assert(attributeLength == 2)
            (h, valueIndex) = readShort()
            output.write("%s; value = %s\n" % (indentation, str(constantPool[valueIndex][1])))
            output.write(indentation + h + "\n")
        elif name == "SourceFile":
            assert(attributeLength == 2)
            (h, valueIndex) = readShort()
            output.write("%s; value = \"%s\"\n" % (indentation, constantPool[valueIndex][1]))
            output.write(indentation + h + "\n")
        elif name == "Code":
            assert(attributeLength >= 13)
            (h1, maxStack) = readShort()
            (h2, maxLocals) = readShort()
            output.write("%s; max_stack=%i, max_locals=%i\n" % (indentation, maxStack, maxLocals))
            output.write((indentation + "%s\n") * 2 % (h1, h2))
            (h, codeLength) = readInt()
            output.write("%s; code_length=%i\n" % (indentation, codeLength))
            output.write(indentation + h + "\n")
            (h, _) = readString(codeLength)
            output.write("%s\t%s\n" % (indentation, h))
            #offset = 0
            #while offset < attributeLength:
            #    (h1, instruction) = readByte()
            #    (instructionName, argumentType) = instructions[instruction]
            #    if argumentType == noArguments:
            #        
            #        offset += 1
            (h, exceptionTableLength) = readShort()
            output.write("%s; exception table; size=%i\n" % (indentation, exceptionTableLength))
            output.write(indentation + h + "\n")
            for i in range(exceptionTableLength):
                (h1, startPc) = readShort()
                (h2, endPc) = readShort()
                (h3, handlerPc) = readShort()
                (h4, catchTypeIndex) = readShort()
                exceptionName = constantPool[constantPool[catchTypeIndex][1]][1]
                output.write("%s\t; from %i to %i catch %s at %i\n" % (indentation, startPc, endPc, exceptionName, handlerPc))
                output.write((indentation + "\t%s\n") * 4 % (h1, h2, h3, h4))
            (h, attributeCount) = readShort()
            output.write("%s; attributes; size=%i\n" % (indentation, attributeCount))
            output.write(indentation + h + "\n")
            errorMessage = readAttributes(attributeCount, indentation + "\t")
            if errorMessage != None:
                return errorMessage
            
        else:
            # unknown attribute name
            (h, _) = readString(attributeLength)
            output.write(indentation + "; unknown attribute name\n")
            output.write(indentation + h + "\n")
    
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
        errorMessage = readAttributes(attributeCount, "\t\t")
        if errorMessage != None:
            return errorMessage
        output.write("\t\n")
    
    # methods
    (h, methodCount) = readShort()
    output.write("; methods; size=%i\n" % methodCount)
    output.write(h + "\n")
    for i in range(methodCount):
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
        if accessFlags & ACC_SYNCHRONIZED:
            output.write("synchronized ")
        if accessFlags & ACC_NATIVE:
            output.write("native ")
        if accessFlags & ACC_ABSTRACT:
            output.write("abstract ")
        if accessFlags & ACC_STRICT:
            output.write("strictfp ")
        output.write(constantPool[nameIndex][1] + ":" + constantPool[descriptorIndex][1] + "\n")
        output.write(("\t%s\n" * 3) % (h1, h2, h3))
        (h, attributeCount) = readShort()
        output.write("\t; attributes; size=%i\n" % attributeCount)
        output.write("\t" + h + "\n")
        errorMessage = readAttributes(attributeCount, "\t\t")
        if errorMessage != None:
            return errorMessage
        output.write("\t\n")
    
    # class attributes
    (h, attributeCount) = readShort()
    output.write("; attributes; size=%i\n" % attributeCount)
    output.write(h + "\n")
    errorMessage = readAttributes(attributeCount, "\t")
    if errorMessage != None:
        return errorMessage
    output.write("\n")
    
    # EOF
    if index[0] != len(input):
        return "Expected EOF at offset " + str(index[0])

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

