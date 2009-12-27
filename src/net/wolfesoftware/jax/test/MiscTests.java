package net.wolfesoftware.jax.test;

import java.io.*;
import java.util.ArrayList;
import net.wolfesoftware.jax.Jaxc;
import net.wolfesoftware.jax.util.Util;

public class MiscTests
{
    private static final String rootDir = "test/misc";
    public static TestCase[] getTests()
    {
        ArrayList<TestCase> tests = new ArrayList<TestCase>();
        tests.add(new FancyZipTestCase());
        tests.add(new ScannerTestCase());
        return tests.toArray(new TestCase[tests.size()]);
    }
    private static class FancyZipTestCase extends TestCase
    {
        private static final String className = "FancyZipThing";
        private static final String jaxFilePath = rootDir + "/" + className + ".jax";
        private static final String zipFilePath = rootDir + "/zipfile.zip";

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
            verboseStream.println("jaxc " + jaxFilePath);
            if (!Jaxc.compile(jaxFilePath))
                return false;
            ByteArrayOutputStream stdoutBuffer = new ByteArrayOutputStream();
            PrintStream stdoutStream = new PrintStream(stdoutBuffer);
            String[] cmd = { "java", "-cp", rootDir, className, zipFilePath };
            verboseStream.println(Util.join(cmd, " "));
            if (Util.exec(cmd, stdoutStream, stderrStream) != 0)
                return false;
            stdoutStream.flush();
            if (!stdoutBuffer.toString().contains("was deflated at"))
                return false;
            return true;
        }
    }
    static class ScannerTestCase extends TestCase
    {
        private static final String classpath = rootDir;
        private static final String className = "goal.testcases.Scanner";
        private static final String fileBase = classpath + "/" + className.replace('.', '/');
        static final String filepath = fileBase + ".jax";
        @Override
        public void clean()
        {
            deleteFile(fileBase + ".class");
        }
        @Override
        public String getName()
        {
            return fileBase;
        }
        @Override
        public boolean run(PrintStream verboseStream, PrintStream stderrStream)
        {
            if (!compileJax(filepath, verboseStream))
                return false;
            // TODO, do something with the class
            return true;
        }
    }
}
