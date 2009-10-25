package net.wolfesoftware.jax.codegen;

import java.io.*;
import net.wolfesoftware.jax.ast.*;
import net.wolfesoftware.jax.lexiconizer.Lexiconization;
import net.wolfesoftware.jax.util.Util;

public class CodeGenerator
{
    public static void generate(Lexiconization lexiconization, String outputFilename) throws FileNotFoundException, IOException
    {
        new CodeGenerator(lexiconization.root, outputFilename).generateCode();
    }

    private final Root root;
    private final String classPath;
    private CodeGenerator(Root root, String classPath)
    {
        this.root = root;
        this.classPath = classPath;
    }

    protected void generateCode() throws FileNotFoundException, IOException
    {
        genCompilationUnit(root.content);
    }

    private void genCompilationUnit(CompilationUnit compilationUnit) throws FileNotFoundException, IOException
    {
        genClassDeclaration(compilationUnit.classDeclaration);
    }

    private void genClassDeclaration(ClassDeclaration classDeclaration) throws FileNotFoundException, IOException
    {
        ClassFile classFile = ClassFile.generate(classDeclaration);
        String outputFilename = Util.platformizeFilepath(classPath + File.separator + classDeclaration.localType.getTypeName() + ".class");
        DataOutputStream out = new DataOutputStream(new FileOutputStream(outputFilename));
        classFile.write(out);
        out.close();
    }

}
