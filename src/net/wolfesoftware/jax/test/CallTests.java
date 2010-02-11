package net.wolfesoftware.jax.test;

import java.io.*;
import java.util.ArrayList;
import net.wolfesoftware.jax.util.Util;

/**
 * This class performs tests on files in {@link #dir} named in {@link #tests}.
 * <p/>
 * For each test name <i>Test</i>, there must be a "<i>Test</i>.jax" and a "<i>Test</i>Call.java" in {@link #dir}.
 * "<i>Test</i>Call.java" must call the code generated from "<i>Test</i>.jax" and print "<code>+++ PASS</code>" when successful.
 * <p/>
 * Note: eclipse's default cwd for running and debugging projects is the project's directory. 
 * This means that {@link #dir} is relative to this project's directory.
 */
public class CallTests
{
    private static final String dir = "test/call";
    private static final String[] tests = {
//        "primitive/Arithmetic",
//        "primitive/BooleanType",
//        "primitive/FloatingTypes",
//        "primitive/ConstReturn",
//        "primitive/Empty",
//        "primitive/LocalVariables",
//        "primitive/Promotion",
//        "primitive/StringLiteral",
//        "primitive/TypeCasting",
//        "primitive/VoidFunction",
//        "controlStructures/IfThenElse",
//        "controlStructures/ForLoop",
//        "controlStructures/ShortCircuit",
//        "controlStructures/WhileLoop",
//        "oop/FunctionInvocation",
//        "oop/NewConstructor",
//        "baseLibraries/Import",
//        "baseLibraries/ImportStar",
//        "baseLibraries/RuntimeType",
//        "baseLibraries/StringConcatenation",
//        "arrays/ArrayType",
//        "arrays/ArrayDereference",
//        "exceptions/TryCatch",
        "exceptions/Operands",
//        "Fancy",
    };

    public static TestCase[] getTests()
    {
        ArrayList<TestCase> testCases = new ArrayList<TestCase>();
        for (String test : tests) {
            final String dirAndTest = Util.unixizeFilepath(dir + "/" + test);
            testCases.add(new TestCase() {
                @Override
                public void clean()
                {
                    deleteFile(dirAndTest + ".class");
                    deleteFile(dirAndTest + "Call.class");
                }
                @Override
                public boolean run(PrintStream verboseStream, PrintStream stderrStream)
                {
                    if (!compileJax(dirAndTest + ".jax", verboseStream, null))
                        return false;
                    if (!compileJava(dirAndTest + "Call.java", verboseStream, stderrStream))
                        return false;
                    String output = runJavaMain(dirAndTest + "Call", verboseStream, stderrStream);
                    if (!output.trim().equals("+++ PASS"))
                        return false;
                    return true;
                }
                @Override
                public String getName()
                {
                    return dirAndTest;
                }
            });
        }
        return testCases.toArray(new TestCase[testCases.size()]);
    }

    private static boolean compileJava(String filepath, PrintStream verboseStream, PrintStream stderrStream)
    {
        filepath = Util.unixizeFilepath(filepath);
        String[] dirAndFile = Util.splitDirAndFile(filepath);
        String[] cmd = { "javac", "-cp", Util.platformizeFilepath(dirAndFile[0]), Util.platformizeFilepath(filepath) };
        verboseStream.println(Util.join(cmd, " "));
        int exitValue = Util.exec(cmd, null, stderrStream);
        if (exitValue != 0)
            return false;
        return true;
    }

    private static String runJavaMain(String javaFilepath, PrintStream verboseStream, PrintStream stderrStream)
    {
        String[] dirAndFile = Util.splitDirAndFile(javaFilepath);
        String className = dirAndFile[1];
        String[] cmd = { "java", "-cp", Util.platformizeFilepath(dirAndFile[0]), className };
        ByteArrayOutputStream stdoutBuffer = new ByteArrayOutputStream();
        PrintStream stdoutStream = new PrintStream(stdoutBuffer);
        verboseStream.println(Util.join(cmd, " "));
        Util.exec(cmd, stdoutStream, stderrStream);
        stdoutStream.flush();
        return stdoutBuffer.toString();
    }
}
