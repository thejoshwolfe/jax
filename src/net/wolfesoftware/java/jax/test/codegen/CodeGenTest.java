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
 * <p/>
 * Turn "1*CLEAN" to "0*CLEAN" to see the intermediate .jasmin and .class files generated by tests. 
 * Turn RUN off and CLEAN on to just clean and not run the tests.
 * @author Josh Wolfe
 */
public class CodeGenTest
{
    private static final int RUN = 1, CLEAN = 2, VERBOSE = 4;
    private static final int mode = 1*RUN | 1*CLEAN | 1*VERBOSE;
    private static final String dir = "test/codegen";
    private static final String[] tests = {
        "primitive/ConstReturn",
        "primitive/Arithmetic",
        "primitive/VoidFunction",
        "primitive/LocalVariables",
        "primitive/BooleanType",
        "primitive/IfThenElse",
        "reference/StringLiteral",
        "reference/FunctionInvocation",
    };

    public static void main(String[] args)
    {
        ArrayList<String> tmpFiles = new ArrayList<String>();
        boolean allPass = true;
        try
        {
            for (String test : tests)
            {
                if ((mode & RUN) != 0)
                    allPass &= codeGenTest(dir + "/" + test);
                tmpFiles.add(dir + "/" + test + ".jasmin");
                tmpFiles.add(dir + "/" + test + ".class");
                tmpFiles.add(dir + "/" + test + "Call.class");
            }
        }
        finally
        {
            if ((mode & CLEAN) != 0)
                for (String file : tmpFiles)
                    new File(Util.platformizeFilepath(file)).delete();

            if ((mode & RUN) == 0)
                System.out.println("done");
            else if (allPass)
                System.out.println("+++ ALL PASS");
        }
    }

    /**
     * @return true iff it passes.
     */
    private static boolean codeGenTest(String dirAndTest)
    {
        dirAndTest = Util.unixizeFilepath(dirAndTest);
        if (Jax.compile(dirAndTest + ".jax"))
        {
            TestUtils.compileJava(dirAndTest + "Call.java");
            InputStream stdout = TestUtils.runJavaMain(dirAndTest + "Call");
            if (stdout != null)
            {
                String output = Util.readAll(stdout);
                if (output.trim().equals("+++ PASS"))
                {
                    if ((mode & VERBOSE) != 0)
                        System.out.println("+++ PASS " + dirAndTest);
                    return true;
                }
            }
        }
        System.out.println("*** FAIL " + dirAndTest);
        return false;
    }
}