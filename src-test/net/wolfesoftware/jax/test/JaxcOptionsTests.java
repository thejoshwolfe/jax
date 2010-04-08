package net.wolfesoftware.jax.test;

import java.io.PrintStream;
import java.util.ArrayList;
import net.wolfesoftware.jax.JaxcOptions;
import net.wolfesoftware.jax.util.Util;

public class JaxcOptionsTests
{
    public static TestCase[] getTests()
    {
        ArrayList<TestCase> tests = new ArrayList<TestCase>();

        tests.add(new JaxcOptionsTestCase(new ArrayList<String>(), "", new ArrayList<String>()));

        // single cp
        tests.add(new JaxcOptionsTestCase(Util.arrayToList("-classPath=abc"), "-classPath=abc", new ArrayList<String>()));
        tests.add(new JaxcOptionsTestCase(Util.arrayToList("-classPath", "abc"), "-classPath=abc", new ArrayList<String>()));
        tests.add(new JaxcOptionsTestCase(Util.arrayToList("-cp=abc"), "-classPath=abc", new ArrayList<String>()));
        tests.add(new JaxcOptionsTestCase(Util.arrayToList("-cp", "abc"), "-classPath=abc", new ArrayList<String>()));

        // multiple cp
        tests.add(new JaxcOptionsTestCase(Util.arrayToList("-cp", "abc", "-cp", "efg"), "-classPath=abc -classPath=efg", new ArrayList<String>()));
        tests.add(new JaxcOptionsTestCase(Util.arrayToList("-cp", "efg", "-cp", "abc"), "-classPath=efg -classPath=abc", new ArrayList<String>()));

        // keep args
        tests.add(new JaxcOptionsTestCase(Util.arrayToList("abc"), "", Util.arrayToList("abc")));
        tests.add(new JaxcOptionsTestCase(Util.arrayToList("abc", "def"), "", Util.arrayToList("abc", "def")));

        // mixed
        tests.add(new JaxcOptionsTestCase(Util.arrayToList("-cp", "abc", "def"), "-classPath=abc", Util.arrayToList("def")));
        tests.add(new JaxcOptionsTestCase(Util.arrayToList("-cp", "abc", "def", "ghi"), "-classPath=abc", Util.arrayToList("def", "ghi")));
        tests.add(new JaxcOptionsTestCase(Util.arrayToList("def", "-cp", "abc", "ghi"), "-classPath=abc", Util.arrayToList("def", "ghi")));
        tests.add(new JaxcOptionsTestCase(Util.arrayToList("def", "ghi", "-cp", "abc"), "-classPath=abc", Util.arrayToList("def", "ghi")));
        tests.add(new JaxcOptionsTestCase(Util.arrayToList("-cp", "abc", "-cp", "def", "ghi"), "-classPath=abc -classPath=def", Util.arrayToList("ghi")));

        // java mode
        tests.add(new JaxcOptionsTestCase(Util.arrayToList("-javaMode"), "-javaCompatabilityMode", new ArrayList<String>()));

        return tests.toArray(new TestCase[tests.size()]);
    }

    private static class JaxcOptionsTestCase extends TestCase
    {
        public final ArrayList<String> args;
        public final String optionsToString;
        public final ArrayList<String> postArgs;
        public JaxcOptionsTestCase(ArrayList<String> args, String optionsToString, ArrayList<String> postArgs)
        {
            this.args = args;
            this.optionsToString = optionsToString;
            this.postArgs = postArgs;
        }

        @Override
        public void clean()
        {
        }
        @Override
        public String getName()
        {
            return "JaxcOptionsTest " + Util.join(args, " ");
        }
        @Override
        public boolean run(PrintStream verboseStream, PrintStream stderrStream)
        {
            ArrayList<String> localArgs = new ArrayList<String>();
            localArgs.addAll(args);
            JaxcOptions options;
            try {
                options = JaxcOptions.parse(localArgs);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return false;
            }
            if (!options.toString().equals(optionsToString))
                return false;
            if (!localArgs.equals(postArgs))
                return false;
            return true;
        }
    }
}
