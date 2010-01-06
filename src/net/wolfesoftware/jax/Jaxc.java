package net.wolfesoftware.jax;

import java.io.*;
import java.util.List;
import net.wolfesoftware.jax.codegen.CodeGenerator;
import net.wolfesoftware.jax.semalysizer.*;
import net.wolfesoftware.jax.staticalysizer.Staticalysizer;
import net.wolfesoftware.jax.parser.*;
import net.wolfesoftware.jax.tokenizer.*;
import net.wolfesoftware.jax.util.Util;

public class Jaxc
{
    public static void main(String[] args) throws FileNotFoundException, IOException
    {
        if (args.length == 0)
            throw new IllegalArgumentException();
        List<String> argsList = Util.arrayToList(args);
        JaxcOptions options = JaxcOptions.parse(argsList);
        String[] filenames = argsList.toArray(new String[argsList.size()]);
        int exitValue = compile(filenames, options) ? 0 : 1;
        System.exit(exitValue);
    }

    public static boolean compile(String[] jaxFilenames)
    {
        for (String jaxFilename : jaxFilenames)
            if (!compile(jaxFilename))
                return false;
        return true;
    }
    public static boolean compile(String[] jaxFilenames, JaxcOptions options)
    {
        for (String jaxFilename : jaxFilenames)
            if (!compile(jaxFilename, options))
                return false;
        return true;
    }
    public static boolean compile(String jaxFilename)
    {
        return compile(jaxFilename, null);
    }

    public static boolean compile(String jaxFilename, JaxcOptions options)
    {
        try {
            return comprehend(Util.unixizeFilepath(jaxFilename), options) == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static int comprehend(String fileName, JaxcOptions options) throws FileNotFoundException, IOException
    {
        Tokenization tokenization = Tokenizer.tokenize(Util.fileToString(fileName));
        if (printErrors(tokenization.errors))
            return 1;

        Parsing parsing = Parser.parse(tokenization);
        if (printErrors(parsing.errors))
            return 1;

        String classPath = fileName.substring(0, fileName.lastIndexOf('/') + 1);
        String relativePath = fileName.substring(classPath.length());
        Semalysization semalysization = Semalysizer.semalysize(parsing, relativePath);
        if (printErrors(semalysization.errors))
            return 1;

        Staticalysizer.staticalysize(semalysization.root);

        CodeGenerator.generate(semalysization, Util.platformizeFilepath(fileName), classPath);

        return 0;
    }
    private static boolean printErrors(List<? extends CompileError> errors)
    {
        for (CompileError error : errors)
            System.err.println(error.getMessage());
        return errors.size() != 0;
    }
}
