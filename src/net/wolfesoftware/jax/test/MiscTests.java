package net.wolfesoftware.jax.test;

import java.io.PrintStream;
import java.util.ArrayList;
import net.wolfesoftware.jax.Jaxc;

public class MiscTests
{
    private static final String rootDir = "test/misc";
    public static TestCase[] getTests()
    {
        ArrayList<TestCase> tests = new ArrayList<TestCase>();
        tests.add(new FancyZipTestCase());
        return tests.toArray(new TestCase[tests.size()]);
    }
    private static class FancyZipTestCase extends TestCase
    {
        private static final String className = "FancyZipThing";
        private static final String jaxFilePath = rootDir + "/" + className + ".jax";
        
        @Override
        public void clean()
        {
            deleteFile(rootDir + "/" + className + ".class");
        }
        @Override
        public String getName()
        {
            return rootDir + "/" + className;
        }
        @Override
        public boolean run(PrintStream verboseStream, PrintStream stderrStream)
        {
            if (!Jaxc.compile(jaxFilePath))
                return false;
            
            return true;
        }
    }
}
