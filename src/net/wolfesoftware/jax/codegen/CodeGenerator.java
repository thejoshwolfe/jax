package net.wolfesoftware.jax.codegen;

import java.io.*;
import net.wolfesoftware.jax.ast.*;
import net.wolfesoftware.jax.semalysis.Semalysization;
import net.wolfesoftware.jax.util.Util;

public class CodeGenerator
{
    public static void generate(Semalysization semalysization, String sourceFile, String classPath) throws FileNotFoundException, IOException
    {
        new CodeGenerator(semalysization.root, sourceFile, classPath).generateCode();
    }

    private final Root root;
    private final String sourceFile;
    private final String classPath;
    public CodeGenerator(Root root, String sourceFile, String classPath)
    {
        this.root = root;
        this.sourceFile = sourceFile;
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
        ClassFile classFile = ClassFile.generate(sourceFile, classDeclaration);
        String outputFilename = Util.platformizeFilepath(classPath + File.separator + classDeclaration.localType.getTypeName() + ".class");
        DataOutputStream out = new DataOutputStream(new FileOutputStream(outputFilename));
        classFile.write(out);
        out.close();
    }

}
