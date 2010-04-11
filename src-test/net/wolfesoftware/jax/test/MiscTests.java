package net.wolfesoftware.jax.test;

import java.io.*;
import java.util.ArrayList;
import net.wolfesoftware.jax.*;
import net.wolfesoftware.jax.util.Util;

public class MiscTests
{
    private static final String rootDir = "test/misc";
    public static TestCase[] getTests()
    {
        ArrayList<TestCase> tests = new ArrayList<TestCase>();
        tests.add(new FancyZipTestCase());
//        tests.add(new ScannerTestCase());
        tests.add(new PackageTestCase());
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
            JaxcOptions options = new JaxcOptions();
            options.classPath = new String[] { rootDir };
            if (!compileJax(jaxFilePath, options, verboseStream, stderrStream))
                return false;
            ByteArrayOutputStream stdoutBuffer = new ByteArrayOutputStream();
            PrintStream stdoutStream = new PrintStream(stdoutBuffer);
            String[] cmd = { "java", "-cp", rootDir, className, zipFilePath };
            verboseStream.println(Util.join(cmd, " "));
            if (TestUtil.exec(cmd, stdoutStream, stderrStream) != 0)
                return false;
            stdoutStream.flush();
            if (!stdoutBuffer.toString().contains("was deflated at"))
                return false;
            return true;
        }
    }
    private static class ScannerTestCase extends TestCase
    {
        private static final String classPath = rootDir + "/goal";
        private static final String className = "scannerPackage.Scanner";
        private static final String fileBase = classPath + "/" + className.replace('.', '/');
        private static final String filepath = fileBase + ".jax";
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
            JaxcOptions options = new JaxcOptions();
            options.classPath = new String[] { classPath };
            options.javaCompatabilityMode = true;
            if (!compileJax(filepath, options, verboseStream, stderrStream))
                return false;
            // TODO, do something with the class file once it works
            return true;
        }
    }
    private static class PackageTestCase extends TestCase
    {
        private static final String classPath = rootDir + "/packageTest";
        private static final String mainClass = "mahPackage.Boring";
        private static final String mainFileBase = classPath + "/" + mainClass.replace('.', '/');
        private static final String mainCallClass = "mahPackage.BoringCall";
        private static final String mainCallFileBase = classPath + "/" + mainCallClass.replace('.', '/');
        private static final String otherCallClass = "otherPackage.DistantBoringCall";
        private static final String otherCallFileBase = classPath + "/" + otherCallClass.replace('.', '/');

        @Override
        public void clean()
        {
            deleteFile(mainFileBase + ".class");
            deleteFile(mainCallFileBase + ".class");
            deleteFile(otherCallFileBase + ".class");
        }
        @Override
        public String getName()
        {
            return classPath;
        }
        @Override
        public boolean run(PrintStream verboseStream, PrintStream stderrStream)
        {
            JaxcOptions options = new JaxcOptions();
            options.classPath = new String[] { classPath };
            if (!compileJax(mainFileBase + ".jax", options, verboseStream, stderrStream))
                return false;
            if (!compileJava(classPath, mainCallFileBase + ".java", verboseStream, stderrStream))
                return false;
            if (!execJava(classPath, mainCallClass, verboseStream, stderrStream, null))
                return false;
            if (!compileJava(classPath, otherCallFileBase + ".java", verboseStream, stderrStream))
                return false;
            if (!execJava(classPath, otherCallClass, verboseStream, stderrStream, null))
                return false;
            return true;
        }
    }
}
