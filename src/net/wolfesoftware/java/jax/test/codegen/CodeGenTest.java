package net.wolfesoftware.java.jax.test.codegen;

import java.io.*;
import java.util.ArrayList;
import net.wolfesoftware.java.common.*;
import net.wolfesoftware.java.jax.Jax;

/**
 * This class performs tests on files in {@link #dir} named in {@link #tests}.
 * <p/>
 * For each test name <i>Test</i>, there must be a "<i>Test</i>.jax" and a "<i>Test</i>Call.java" in {@link #dir}.
 * "<i>Test</i>Call.java" must call the code generated from "<i>Test</i>.jax" and print "<code>+++ PASS</code>" when successful.
 * @author Josh Wolfe
 */
public class CodeGenTest
{
    private static final boolean clean = true;
    private static final boolean verbose = true;
    private static final String dir = "test/codegen";
    private static final String[] tests = { 
        "ConstReturn", 
        "Arithmetic",
        };

    public static void main(String[] args)
    {
        ArrayList<String> tmpFiles = new ArrayList<String>();
        boolean failures = false;
        for (String test : tests)
        {
            failures |= codeGenTest(dir + "/" + test);
            tmpFiles.add(dir + "/" + test + ".jasmin");
            tmpFiles.add(dir + "/" + test + ".class");
            tmpFiles.add(dir + "/" + test + "Call.class");
        }

        if (!failures)
            System.out.println("+++ ALL PASS");

        if (clean)
            for (String file : tmpFiles)
                new File(Util.platformizeFilepath(file)).delete();
    }

    /**
     * @return true iff it fails.
     */
    private static boolean codeGenTest(String dirAndTest)
    {
        dirAndTest = Util.unixizeFilepath(dirAndTest);
        Jax.compile(dirAndTest + ".jax");
        TestUtils.compileJava(dirAndTest + "Call.java");
        InputStream stdout = TestUtils.runJavaMain(dirAndTest + "Call");
        String output = Util.readAll(stdout);
        if (output.trim().equals("+++ PASS"))
        {
            if (verbose)
                System.out.println("+++ PASS " + dirAndTest);
            return false;
        }
        else
        {
            System.out.println("*** FAIL " + dirAndTest);
            return true;
        }
    }
}
