package net.wolfesoftware.java.jax;

import java.io.FileNotFoundException;
import net.wolfesoftware.java.common.Util;
import net.wolfesoftware.java.jax.lexiconizer.*;
import net.wolfesoftware.java.jax.parser.*;
import net.wolfesoftware.java.jax.tokenizer.*;

public class Main
{
    public static void main(String[] args) throws FileNotFoundException
    {
        String infile = null;
        for (String arg : args)
        {
            if (arg.startsWith("-in="))
                infile = arg.substring("-in=".length());
        }
        if (infile != null)
            comprehend(infile);
    }

    public static void comprehend(String fileName) throws FileNotFoundException
    {
        Tokenization tokenization = Tokenizer.tokenize(Util.fileToString(fileName));
        Parsing parsing = Parser.parse(tokenization);
        Lexiconization lexiconization = Lexiconizer.lexiconize(parsing);
        System.out.println(lexiconization);
    }
}
