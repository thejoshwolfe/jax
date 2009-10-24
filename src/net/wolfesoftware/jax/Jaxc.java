package net.wolfesoftware.jax;

import java.io.*;
import java.util.List;
import net.wolfesoftware.jax.codegen.CodeGenerator;
import net.wolfesoftware.jax.lexiconizer.*;
import net.wolfesoftware.jax.optimizer.Optimizer;
import net.wolfesoftware.jax.parser.*;
import net.wolfesoftware.jax.tokenizer.*;
import net.wolfesoftware.jax.util.Util;

public class Jaxc
{
    public static void main(String[] args) throws FileNotFoundException, IOException
    {
        if (args.length == 0)
            throw new IllegalArgumentException();
        System.exit(comprehend(args[args.length - 1]));
    }

    public static boolean compile(String jaxFilename)
    {
        try {
            return comprehend(Util.platformizeFilepath(jaxFilename)) == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static int comprehend(String fileName) throws FileNotFoundException, IOException
    {
        Tokenization tokenization = Tokenizer.tokenize(Util.fileToString(fileName));
        if (printErrors(tokenization.errors))
            return 1;

        Parsing parsing = Parser.parse(tokenization);
        if (printErrors(parsing.errors))
            return 1;

        String classPath = fileName.substring(0, fileName.lastIndexOf('\\') + 1);
        String relativePath = fileName.substring(classPath.length());
        Lexiconization lexiconization = Lexiconizer.lexiconize(parsing, relativePath);
        if (printErrors(lexiconization.errors))
            return 1;

        Optimizer.optimize(lexiconization.root);

        String className = lexiconization.root.content.classDeclaration.id.name;
        String outFileName = classPath + className + ".jasmin";
        CodeGenerator.generate(lexiconization, outFileName);

        return 0;
    }
    private static boolean printErrors(List<? extends CompileError> errors)
    {
        for (CompileError error : errors)
            System.err.println(error.getMessage());
        return errors.size() != 0;
    }
}
