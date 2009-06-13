package net.wolfesoftware.java.jax;

import java.io.*;
import java.util.*;
import net.wolfesoftware.java.common.Util;
import net.wolfesoftware.java.jax.codegen.CodeGenerator;
import net.wolfesoftware.java.jax.lexiconizer.*;
import net.wolfesoftware.java.jax.optimizer.Optimizer;
import net.wolfesoftware.java.jax.parser.*;
import net.wolfesoftware.java.jax.tokenizer.*;

public class Jax
{
    public static void main(String[] args) throws FileNotFoundException
    {
        if (args.length == 0)
            throw new IllegalArgumentException();
        comprehend(args[args.length - 1]);
    }

    private static void comprehend(String fileName) throws FileNotFoundException
    {
        Tokenization tokenization = Tokenizer.tokenize(Util.fileToString(fileName));
        if (printErrors(tokenization.errors))
            throw new RuntimeException();

        Parsing parsing = Parser.parse(tokenization);
        if (printErrors(parsing.errors))
            throw new RuntimeException();

        Lexiconization lexiconization = Lexiconizer.lexiconize(parsing);
        if (printErrors(lexiconization.errors))
            throw new RuntimeException();

        Optimizer.optimize(lexiconization.root, null);

        String outFileName = fileName.substring(0, fileName.lastIndexOf('.')) + ".jasmin";
        CodeGenerator.generateCode(lexiconization, outFileName);
    }
    private static boolean printErrors(List<? extends Exception> errors)
    {
        for (Exception e : errors)
            e.printStackTrace();
        return errors.size() != 0;
    }

    public static boolean compile(String jaxFilename)
    {
        String[] args = { Util.platformizeFilepath(jaxFilename) };
        try {
            main(args);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}