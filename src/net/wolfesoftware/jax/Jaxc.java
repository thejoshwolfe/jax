package net.wolfesoftware.jax;

import java.io.*;
import java.util.*;
import net.wolfesoftware.jax.codegen.CodeGenerator;
import net.wolfesoftware.jax.parsing.*;
import net.wolfesoftware.jax.semalysis.*;
import net.wolfesoftware.jax.tokenization.*;
import net.wolfesoftware.jax.util.Util;

public final class Jaxc
{
    public static void main(String[] args)
    {
        if (args.length == 0)
            throw new IllegalArgumentException();
        List<String> argsList = Util.arrayToList(args);
        JaxcOptions options = JaxcOptions.parse(argsList);
        String[] jaxFilePaths = argsList.toArray(new String[argsList.size()]);
        try {
            compile(jaxFilePaths, options);
        } catch (JaxcCompileException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void compile(String[] jaxFilePaths) throws JaxcCompileException
    {
        for (String jaxFilePath : jaxFilePaths)
            compile(jaxFilePath);
    }
    public static void compile(String[] jaxFilePaths, JaxcOptions options) throws JaxcCompileException
    {
        for (String jaxFilename : jaxFilePaths)
            compile(jaxFilename, options);
    }
    public static void compile(String jaxFilePath) throws JaxcCompileException
    {
        compile(jaxFilePath, null);
    }

    public static void compile(String jaxFilePath, JaxcOptions options) throws JaxcCompileException
    {
        ArrayList<CompileError> compileErrors = internalCompile(Util.unixizeFilepath(jaxFilePath), options);
        if (!compileErrors.isEmpty())
            throw new JaxcCompileException(compileErrors.toArray(new CompileError[compileErrors.size()]));
    }

    private static ArrayList<CompileError> internalCompile(String filePath, JaxcOptions options) throws JaxcCompileException
    {
        ArrayList<CompileError> errors = new ArrayList<CompileError>();
        if (options == null)
            options = new JaxcOptions();

        String filePathRelativeToClassPath = getFilePathRelativeToClassPath(filePath, options.classPath);
        if (filePathRelativeToClassPath == null) {
            errors.add(new CompileError("Source file \"" + filePath + "\"is not in classPath [" + Util.join(options.classPath, ", ") + "]."));
            // not quite terminal. we'll see how tokenization and parsing goes.
        }

        Tokenization tokenization;
        try {
            tokenization = Tokenizer.tokenize(Util.readFile(filePath));
        } catch (FileNotFoundException e) {
            errors.add(new CompileError(e.getMessage()));
            return errors;
        }
        errors.addAll(tokenization.errors);

        Parsing parsing = Parser.parse(tokenization, options);
        errors.addAll(parsing.errors);

        // don't go on if there are errors.
        if (!errors.isEmpty())
            return errors;

        Semalysization semalysization = Semalysizer.semalysize(parsing, filePathRelativeToClassPath, options);
        if (!errors.isEmpty())
            return errors;

//        Staticalysizer.staticalysize(semalysization.root);

        try {
            // TODO: -d outputdir
            CodeGenerator.generate(semalysization, Util.platformizeFilepath(filePath), options.classPath[0]);
        } catch (FileNotFoundException e) {
            errors.add(new CompileError(e.getMessage()));
        }
        return errors;
    }

    private static String getFilePathRelativeToClassPath(String filePath, String[] classPath)
    {
        String absFilePath = new File(filePath).getAbsolutePath();
        for (String classPathDir : classPath) {
            String absClassPathDir = new File(classPathDir).getAbsolutePath();
            if (absFilePath.startsWith(absClassPathDir))
                return Util.unixizeFilepath(absFilePath.substring(absClassPathDir.length() + 1));
        }
        return null;
    }
}
