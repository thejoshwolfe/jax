package net.wolfesoftware.java.jax.test.parser;

import net.wolfesoftware.java.common.Util;
import net.wolfesoftware.java.jax.parser.*;
import net.wolfesoftware.java.jax.tokenizer.*;

public class ParserTest
{
    private static final boolean verbose = true;
    private static final String dir = "test/parser";
    private static final String[] tests = { "TopLevelItems", "Test" };

    public static void main(String[] args)
    {
        boolean fail = false;
        for (String test : tests)
            fail |= runTest(dir + "/" + test + ".jax");
        if (!fail)
            System.out.println("+++ ALL PASS");
    }

    private static boolean runTest(String file)
    {
        boolean fail = testParser(file);
        if (fail)
            System.out.println("*** FAIL " + file);
        else if (verbose)
            System.out.println("+++ PASS " + file);
        return fail;
    }

    private static boolean testParser(String file)
    {
        try {
            Tokenization tokenization = Tokenizer.tokenize(Util.fileToString(Util.platformizeFilepath(file)));
            if (tokenization.errors.size() != 0)
                return true;
            Parsing parsing = Parser.parse(tokenization);
            if (parsing.errors.size() != 0)
                return true;
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }
}
