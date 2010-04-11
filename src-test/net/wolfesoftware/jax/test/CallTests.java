package net.wolfesoftware.jax.test;

import java.io.*;
import java.util.ArrayList;
import net.wolfesoftware.jax.JaxcOptions;

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
        "oop/Fields",
//        "oop/NewConstructor",
//        "baseLibraries/Import",
//        "baseLibraries/ImportStar",
//        "baseLibraries/RuntimeType",
//        "baseLibraries/StringConcatenation",
//        "arrays/ArrayType",
//        "arrays/ArrayDereference",
//        "exceptions/TryCatch",
//        "exceptions/Operands",
//        "Fancy",
    };

    public static TestCase[] getTests()
    {
        ArrayList<TestCase> testCases = new ArrayList<TestCase>();
        for (String test : tests) {
            final String dirAndTest = dir + "/" + test;
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
                    String[] dirAndFile = TestUtil.splitDirAndFile(dirAndTest);
                    String classPath = dirAndFile[0];
                    String fileName = dirAndFile[1];
                    JaxcOptions options = new JaxcOptions();
                    options.classPath = new String[] { classPath };
                    if (!compileJax(dirAndTest + ".jax", options, verboseStream, stderrStream))
                        return false;
                    if (!compileJava(classPath, dirAndTest + "Call.java", verboseStream, stderrStream))
                        return false;
                    ByteArrayOutputStream stdoutBuffer = new ByteArrayOutputStream();
                    PrintStream stdoutStream = new PrintStream(stdoutBuffer);
                    if (!execJava(classPath, fileName + "Call", verboseStream, stderrStream, stdoutStream))
                        return false;
                    if (!stdoutBuffer.toString().trim().equals("+++ PASS"))
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
}
