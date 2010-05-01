package net.wolfesoftware.jax.codegen;

import java.io.*;
import net.wolfesoftware.jax.ast.*;
import net.wolfesoftware.jax.semalysis.Semalysization;

public class CodeGenerator
{
    public static void generate(Semalysization semalysization, String sourceFile, String classPath) throws FileNotFoundException
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

    private void generateCode() throws FileNotFoundException
    {
        genCompilationUnit(root.content);
    }

    private void genCompilationUnit(CompilationUnit compilationUnit) throws FileNotFoundException
    {
        genClassDeclaration(compilationUnit.classDeclaration);
    }

    private void genClassDeclaration(ClassDeclaration classDeclaration) throws FileNotFoundException
    {
        ClassFile classFile = ClassFile.generate(sourceFile, classDeclaration);
        String outputFilename = classPath + '/' + classDeclaration.localType.getTypeName() + ".class";
        DataOutputStream out = new DataOutputStream(new FileOutputStream(outputFilename));
        classFile.write(out);
        try {
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
