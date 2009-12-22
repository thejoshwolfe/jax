package net.wolfesoftware.jax.test;

import java.io.*;
import java.util.ArrayList;

/**
 * Set CLEAN to false to see the intermediate .class files generated by tests. 
 * Turn RUN off and CLEAN on to just clean and not run the tests.
 */
public class TestMain
{
    private static final boolean RUN = true;
    private static final boolean CLEAN = true;
    private static final boolean VERBOSE = false;

    private static ArrayList<TestCase> getTests()
    {
        ArrayList<TestCase> tests = new ArrayList<TestCase>();
        for (TestCase test : CallTest.getTests())
            tests.add(test);
        return tests;
    }

    public static void main(String args[])
    {
        ArrayList<TestCase> tests = getTests();
        PrintStream verboseStream = VERBOSE ? System.out : new PrintStream(new ByteArrayOutputStream());
        PrintStream stderrStream = System.err;
        int failcount = 0;
        if (RUN) {
            for (TestCase test : tests) {
                String status;
                if (test.run(verboseStream, stderrStream)) {
                    status = "+++ PASS ";
                } else {
                    status = " *** FAIL ";
                    failcount++;
                }
                System.out.println(status + test.getName());
            }
        }
        if (CLEAN)
            for (TestCase test : tests)
                test.clean();

        if (!RUN)
            System.out.println("done");
        else if (failcount == 0)
            System.out.println("+++ ALL PASS");
        else
            System.out.println(" *** " + failcount + " failed out of " + tests.size());
    }
}
