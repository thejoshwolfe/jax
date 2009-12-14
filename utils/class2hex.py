#!/usr/bin/env python

from sys import argv, stdin, stdout, stderr, exit
from struct import unpack, calcsize
import os.path

usage = """\
usage: %s [inFile [outFile]]
        if input/output unspecified stdin/stdout are used
examples:
        %s MyClass.class MyClass.hex
        %s MyClass.class
"""
usage %= ((os.path.split(__file__)[1],) * (len(usage.split("%s")) - 1))


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
ACC_PROTECTED = 0x0004
ACC_STATIC = 0x0008
ACC_FINAL = 0x0010
ACC_SUPER = ACC_SYNCHRONIZED = 0x0020
ACC_VOLATILE = 0x0040
ACC_TRANSIENT = 0x0080
ACC_NATIVE = 0x0100
ACC_INTERFACE = 0x0200
ACC_ABSTRACT = 0x0400
ACC_STRICT = 0x0800



ARG_NOT_SUPPORTED = "ARG_NOT_SUPPORTED"
ARG_NONE = "ARG_NONE"
ARG_BYTE_IMMEDIATE = "ARG_BYTE_IMMEDIATE"
ARG_SHORT_IMMEDIATE = "ARG_SHORT_IMMEDIATE"
ARG_VARIENT_CONSTANT = "ARG_VARIENT_CONSTANT"
ARG_VARIENT_CONSTANT_W = "ARG_VARIENT_CONSTANT_W"
ARG_LOCAL_VARIABLE_INDEX = "ARG_LOCAL_VARIABLE_INDEX"
ARG_BRANCH_OFFSET = "ARG_BRANCH_OFFSET"
ARG_BRANCH_OFFSET_W = "ARG_BRANCH_OFFSET_W"
ARG_TABLESWITCH = "ARG_TABLESWITCH"
ARG_LOOKUPSWITCH = "ARG_LOOKUPSWITCH"
ARG_FIELDREF_INDEX = "ARG_FIELDREF_INDEX"
ARG_METHODREF_INDEX = "ARG_METHODREF_INDEX"
ARG_INTERFACE_METHODREF = "ARG_INTERFACE_METHODREF"
ARG_CLASS_INDEX = "ARG_CLASS_INDEX"
ARG_ATYPE_CODE = "ARG_ATYPE_CODE"
ARG_WIDE_ARGUMENTS = "ARG_WIDE_ARGUMENTS"
ARG_MULTIANEWARRAY = "ARG_MULTIANEWARRAY"

# index is opcode
instructions = [
    ("nop", ARG_NONE),
    ("aconst_null", ARG_NONE),
    ("iconst_m1", ARG_NONE),
    ("iconst_0", ARG_NONE),
    ("iconst_1", ARG_NONE),
    ("iconst_2", ARG_NONE),
    ("iconst_3", ARG_NONE),
    ("iconst_4", ARG_NONE),
    ("iconst_5", ARG_NONE),
    ("lconst_0", ARG_NONE),
    ("lconst_1", ARG_NONE),
    ("fconst_0", ARG_NONE),
    ("fconst_1", ARG_NONE),
    ("fconst_2", ARG_NONE),
    ("dconst_0", ARG_NONE),
    ("dconst_1", ARG_NONE),
    ("bipush", ARG_BYTE_IMMEDIATE),
    ("sipush", ARG_SHORT_IMMEDIATE),
    ("ldc", ARG_VARIENT_CONSTANT),
    ("ldc_w", ARG_VARIENT_CONSTANT_W),
    ("ldc2_w", ARG_VARIENT_CONSTANT_W),
    ("iload", ARG_LOCAL_VARIABLE_INDEX),
    ("lload", ARG_LOCAL_VARIABLE_INDEX),
    ("fload", ARG_LOCAL_VARIABLE_INDEX),
    ("dload", ARG_LOCAL_VARIABLE_INDEX),
    ("aload", ARG_LOCAL_VARIABLE_INDEX),
    ("iload_0", ARG_NONE),
    ("iload_1", ARG_NONE),
    ("iload_2", ARG_NONE),
    ("iload_3", ARG_NONE),
    ("lload_0", ARG_NONE),
    ("lload_1", ARG_NONE),
    ("lload_2", ARG_NONE),
    ("lload_3", ARG_NONE),
    ("fload_0", ARG_NONE),
    ("fload_1", ARG_NONE),
    ("fload_2", ARG_NONE),
    ("fload_3", ARG_NONE),
    ("dload_0", ARG_NONE),
    ("dload_1", ARG_NONE),
    ("dload_2", ARG_NONE),
    ("dload_3", ARG_NONE),
    ("aload_0", ARG_NONE),
    ("aload_1", ARG_NONE),
    ("aload_2", ARG_NONE),
    ("aload_3", ARG_NONE),
    ("iaload", ARG_NONE),
    ("laload", ARG_NONE),
    ("faload", ARG_NONE),
    ("daload", ARG_NONE),
    ("aaload", ARG_NONE),
    ("baload", ARG_NONE),
    ("caload", ARG_NONE),
    ("saload", ARG_NONE),
    ("istore", ARG_LOCAL_VARIABLE_INDEX),
    ("lstore", ARG_LOCAL_VARIABLE_INDEX),
    ("fstore", ARG_LOCAL_VARIABLE_INDEX),
    ("dstore", ARG_LOCAL_VARIABLE_INDEX),
    ("astore", ARG_LOCAL_VARIABLE_INDEX),
    ("istore_0", ARG_NONE),
    ("istore_1", ARG_NONE),
    ("istore_2", ARG_NONE),
    ("istore_3", ARG_NONE),
    ("lstore_0", ARG_NONE),
    ("lstore_1", ARG_NONE),
    ("lstore_2", ARG_NONE),
    ("lstore_3", ARG_NONE),
    ("fstore_0", ARG_NONE),
    ("fstore_1", ARG_NONE),
    ("fstore_2", ARG_NONE),
    ("fstore_3", ARG_NONE),
    ("dstore_0", ARG_NONE),
    ("dstore_1", ARG_NONE),
    ("dstore_2", ARG_NONE),
    ("dstore_3", ARG_NONE),
    ("astore_0", ARG_NONE),
    ("astore_1", ARG_NONE),
    ("astore_2", ARG_NONE),
    ("astore_3", ARG_NONE),
    ("iastore", ARG_NONE),
    ("lastore", ARG_NONE),
    ("fastore", ARG_NONE),
    ("dastore", ARG_NONE),
    ("aastore", ARG_NONE),
    ("bastore", ARG_NONE),
    ("castore", ARG_NONE),
    ("sastore", ARG_NONE),
    ("pop", ARG_NONE),
    ("pop2", ARG_NONE),
    ("dup", ARG_NONE),
    ("dup_x1", ARG_NONE),
    ("dup_x2", ARG_NONE),
    ("dup2", ARG_NONE),
    ("dup2_x1", ARG_NONE),
    ("dup2_x2", ARG_NONE),
    ("swap", ARG_NONE),
    ("iadd", ARG_NONE),
    ("ladd", ARG_NONE),
    ("fadd", ARG_NONE),
    ("dadd", ARG_NONE),
    ("isub", ARG_NONE),
    ("lsub", ARG_NONE),
    ("fsub", ARG_NONE),
    ("dsub", ARG_NONE),
    ("imul", ARG_NONE),
    ("lmul", ARG_NONE),
    ("fmul", ARG_NONE),
    ("dmul", ARG_NONE),
    ("idiv", ARG_NONE),
    ("ldiv", ARG_NONE),
    ("fdiv", ARG_NONE),
    ("ddiv", ARG_NONE),
    ("irem", ARG_NONE),
    ("lrem", ARG_NONE),
    ("frem", ARG_NONE),
    ("drem", ARG_NONE),
    ("ineg", ARG_NONE),
    ("lneg", ARG_NONE),
    ("fneg", ARG_NONE),
    ("dneg", ARG_NONE),
    ("ishl", ARG_NONE),
    ("lshl", ARG_NONE),
    ("ishr", ARG_NONE),
    ("lshr", ARG_NONE),
    ("iushr", ARG_NONE),
    ("lushr", ARG_NONE),
    ("iand", ARG_NONE),
    ("land", ARG_NONE),
    ("ior", ARG_NONE),
    ("lor", ARG_NONE),
    ("ixor", ARG_NONE),
    ("lxor", ARG_NONE),
    ("iinc", ARG_NONE),
    ("i2l", ARG_NONE),
    ("i2f", ARG_NONE),
    ("i2d", ARG_NONE),
    ("l2i", ARG_NONE),
    ("l2f", ARG_NONE),
    ("l2d", ARG_NONE),
    ("f2i", ARG_NONE),
    ("f2l", ARG_NONE),
    ("f2d", ARG_NONE),
    ("d2i", ARG_NONE),
    ("d2l", ARG_NONE),
    ("d2f", ARG_NONE),
    ("i2b", ARG_NONE),
    ("i2c", ARG_NONE),
    ("i2s", ARG_NONE),
    ("lcmp", ARG_NONE),
    ("fcmpl", ARG_NONE),
    ("fcmpg", ARG_NONE),
    ("dcmpl", ARG_NONE),
    ("dcmpg", ARG_NONE),
    ("ifeq", ARG_BRANCH_OFFSET),
    ("ifne", ARG_BRANCH_OFFSET),
    ("iflt", ARG_BRANCH_OFFSET),
    ("ifge", ARG_BRANCH_OFFSET),
    ("ifgt", ARG_BRANCH_OFFSET),
    ("ifle", ARG_BRANCH_OFFSET),
    ("if_icmpeq", ARG_BRANCH_OFFSET),
    ("if_icmpne", ARG_BRANCH_OFFSET),
    ("if_icmplt", ARG_BRANCH_OFFSET),
    ("if_icmpge", ARG_BRANCH_OFFSET),
    ("if_icmpgt", ARG_BRANCH_OFFSET),
    ("if_icmple", ARG_BRANCH_OFFSET),
    ("if_acmpeq", ARG_BRANCH_OFFSET),
    ("if_acmpne", ARG_BRANCH_OFFSET),
    ("goto", ARG_BRANCH_OFFSET),
    ("jsr", ARG_BRANCH_OFFSET),
    ("ret", ARG_LOCAL_VARIABLE_INDEX),
    ("tableswitch", ARG_TABLESWITCH),
    ("lookupswitch", ARG_LOOKUPSWITCH),
    ("ireturn", ARG_NONE),
    ("lreturn", ARG_NONE),
    ("freturn", ARG_NONE),
    ("dreturn", ARG_NONE),
    ("areturn", ARG_NONE),
    ("return", ARG_NONE),
    ("getstatic", ARG_FIELDREF_INDEX),
    ("putstatic", ARG_FIELDREF_INDEX),
    ("getfield", ARG_FIELDREF_INDEX),
    ("putfield", ARG_FIELDREF_INDEX),
    ("invokevirtual", ARG_METHODREF_INDEX),
    ("invokespecial", ARG_METHODREF_INDEX),
    ("invokestatic", ARG_METHODREF_INDEX),
    ("invokeinterface", ARG_INTERFACE_METHODREF),
    ("invokedynamic", ARG_NOT_SUPPORTED),
    ("new", ARG_CLASS_INDEX),
    ("newarray", ARG_ATYPE_CODE),
    ("anewarray", ARG_CLASS_INDEX),
    ("arraylength", ARG_NONE),
    ("athrow", ARG_NONE),
    ("checkcast", ARG_CLASS_INDEX),
    ("instanceof", ARG_CLASS_INDEX),
    ("monitorenter", ARG_NONE),
    ("monitorexit", ARG_NONE),
    ("wide", ARG_WIDE_ARGUMENTS),
    ("multianewarray", ARG_MULTIANEWARRAY),
    ("ifnull", ARG_BRANCH_OFFSET),
    ("ifnonnull", ARG_BRANCH_OFFSET),
    ("goto_w", ARG_BRANCH_OFFSET_W),
    ("jsr_w", ARG_BRANCH_OFFSET_W),
    ("breakpoint", ARG_NONE),
]


def main(input, output):
    """
    input  : str - such as open("Example.class", "r").read()
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
    def readUByte():
        return read(">B")
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
            offset = 0
            while offset < codeLength:
                (opcodeHex, opcode) = readUByte()
                (instructionName, argumentType) = instructions[opcode]
                instructionHex = None
                argsStr = None
                if argumentType == ARG_NOT_SUPPORTED:
                    return "Instruction %s is not supported" % instructionName
                elif argumentType == ARG_NONE:
                    instructionHex = opcodeHex
                    argsStr = ""
                elif argumentType == ARG_BYTE_IMMEDIATE:
                    (h, value) = readByte()
                    instructionHex = opcodeHex + h
                    argsStr = " = " + str(value)
                elif argumentType == ARG_SHORT_IMMEDIATE:
                    (h, value) = readShort()
                    instructionHex = opcodeHex + h
                    argsStr = " = " + str(value)
                elif argumentType == ARG_VARIENT_CONSTANT:
                    (h, index) = readUByte()
                    instructionHex = opcodeHex + h
                    typeStr = {
                        CONSTANT_Integer:"Integer",
                        CONSTANT_Float:"Float",
                        CONSTANT_String:"String",
                    }[constantPool[index][0]]
                    value = constantPool[index][1]
                    if typeStr == "String":
                        value = constantPool[value][1]
                    argsStr = " %s = %s" % (typeStr, str(value))
                elif argumentType == ARG_VARIENT_CONSTANT_W:
                    (h, index) = readShort()
                    instructionHex = opcodeHex + h
                    typeStr = {
                        CONSTANT_Integer:"Integer",
                        CONSTANT_Long:"Long",
                        CONSTANT_Float:"Float",
                        CONSTANT_Double:"Double",
                        CONSTANT_String:"String",
                    }[constantPool[index][0]]
                    value = constantPool[index][1]
                    if typeStr == "String":
                        value = constantPool[value][1]
                    argsStr = " %s = %s" % (typeStr, str(value))
                elif argumentType == ARG_LOCAL_VARIABLE_INDEX:
                    (h, value) = readUByte()
                    instructionHex = opcodeHex + h
                    argsStr = " = " + str(value)
                elif argumentType == ARG_BRANCH_OFFSET:
                    (h, value) = readShort()
                    instructionHex = opcodeHex + h
                    argsStr = " " + str(value + offset)
                elif argumentType == ARG_BRANCH_OFFSET_W:
                    (h, value) = readInt()
                    instructionHex = opcodeHex + h
                    argsStr = " " + str(value + offset)
                elif argumentType == ARG_TABLESWITCH:
                    return "argument type " + argumentType + " is not implemented yet"
                elif argumentType == ARG_LOOKUPSWITCH:
                    return "argument type " + argumentType + " is not implemented yet"
                elif argumentType == ARG_FIELDREF_INDEX:
                    (h, fieldrefIndex) = readShort()
                    className = constantPool[constantPool[constantPool[fieldrefIndex][1]][1]][1]
                    nameAndTypeIndex = constantPool[fieldrefIndex][2]
                    signature = constantPool[constantPool[nameAndTypeIndex][1]][1] + ":" + constantPool[constantPool[nameAndTypeIndex][2]][1]
                    instructionHex = opcodeHex + h
                    argsStr = " %s.%s" % (className, signature)
                elif argumentType == ARG_METHODREF_INDEX:
                    (h, methodrefIndex) = readShort()
                    className = constantPool[constantPool[constantPool[methodrefIndex][1]][1]][1]
                    nameAndTypeIndex = constantPool[methodrefIndex][2]
                    signature = constantPool[constantPool[nameAndTypeIndex][1]][1] + ":" + constantPool[constantPool[nameAndTypeIndex][2]][1]
                    instructionHex = opcodeHex + h
                    argsStr = " %s.%s" % (className, signature)
                elif argumentType == ARG_INTERFACE_METHODREF:
                    (h1, interfaceMethodrefIndex) = readShort()
                    (h2, _) = readShort()
                    className = constantPool[constantPool[constantPool[interfaceMethodrefIndex][1]][1]][1]
                    nameAndTypeIndex = constantPool[interfaceMethodrefIndex][2]
                    signature = constantPool[constantPool[nameAndTypeIndex][1]][1] + ":" + constantPool[constantPool[nameAndTypeIndex][2]][1]
                    instructionHex = opcodeHex + h1 + h2
                    argsStr = " %s.%s" % (className, signature)
                elif argumentType == ARG_CLASS_INDEX:
                    (h, classIndex) = readShort()
                    className = constantPool[constantPool[classIndex][1]][1]
                    instructionHex = opcodeHex + h
                    argsStr = " " + className
                elif argumentType == ARG_ATYPE_CODE:
                    return "argument type " + argumentType + " is not implemented yet"
                elif argumentType == ARG_WIDE_ARGUMENTS:
                    return "argument type " + argumentType + " is not implemented yet"
                elif argumentType == ARG_MULTIANEWARRAY:
                    return "argument type " + argumentType + " is not implemented yet"
                else:
                    return "omgwtf!!"
                output.write("%s\t%s; %i:  %s%s\n" % (indentation, instructionHex.ljust(9), offset, instructionName, argsStr))
                offset += len(instructionHex) / 3
                
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
    if len(argv) >= 2 and argv[1] in ("-h", "-help", "--help"):
        stdout.write(usage)
        exit(1)
    if len(argv) == 1:
        input = stdin.read()
        output = stdout
    elif len(argv) == 2:
        input = open(argv[1], "rb").read()
        output = stdout
    elif len(argv) == 3:
        input = open(argv[1], "rb").read()
        output = open(argv[2], "w")
    else:
        stderr.write("too many arguments\n")
        stdout.write(usage)
        exit(1)
    errorMessage = main(input, output)
    returnCode = 0
    if errorMessage != None:
        stderr.write(errorMessage + "\n")
        returnCode = 1
    if output != stdout:
        output.close()
    exit(returnCode)

