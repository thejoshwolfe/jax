package net.wolfesoftware.jax.test;

import java.io.*;
import java.util.ArrayList;
import net.wolfesoftware.jax.Jaxc;
import net.wolfesoftware.jax.util.*;

/**
 * This class performs tests on files in {@link #dir} named in {@link #tests}.
 * <p/>
 * For each test name <i>Test</i>, there must be a "<i>Test</i>.jax" and a "<i>Test</i>Call.java" in {@link #dir}.
 * "<i>Test</i>Call.java" must call the code generated from "<i>Test</i>.jax" and print "<code>+++ PASS</code>" when successful.
 * <p/>
 * Note: eclipse's default cwd for running and debugging projects is the project's directory. 
 * This means that {@link #dir} is relative to this project's directory.
 * <p/>
 * Set CLEAN to false to see the intermediate .class files generated by tests. 
 * Turn RUN off and CLEAN on to just clean and not run the tests.
 * VERBOSE is whether success is reported to stdout; failure is always reported.
 */
public class CodeGenTest
{
    private static final boolean RUN = true;
    private static final boolean CLEAN = true;
    private static final boolean VERBOSE = true;
    private static final String dir = "test";
    private static final String[] tests = {
        "primitive/Arithmetic",
        "primitive/BooleanType",
        "primitive/FloatingTypes",
        "primitive/ConstReturn",
        "primitive/Empty",
        "primitive/LocalVariables",
        "primitive/Promotion",
        "primitive/StringLiteral",
        "primitive/TypeCasting",
        "primitive/VoidFunction",
        "controlStructures/IfThenElse",
        "controlStructures/ForLoop",
        "controlStructures/ShortCircuit",
        "controlStructures/WhileLoop",
        "oop/FunctionInvocation",
        "oop/NewConstructor",
        "baseLibraries/Import",
        "baseLibraries/ImportStar",
        "baseLibraries/RuntimeType",
        "baseLibraries/StringConcatenation",
        "arrays/ArrayType",
        "arrays/ArrayDereference",
        "exceptions/TryCatch",
        "Fancy",
//        "Goal",
    };

    public static void main(String[] args)
    {
        ArrayList<String> tmpFiles = new ArrayList<String>();
        int failcount = 0;
        try {
            for (String test : tests) {
                if (RUN)
                    if (!codeGenTest(dir + "/" + test))
                        failcount++;
                tmpFiles.add(dir + "/" + test + ".class");
                tmpFiles.add(dir + "/" + test + "Call.class");
            }
        } finally {
            if (CLEAN)
                for (String file : tmpFiles)
                    new File(Util.platformizeFilepath(file)).delete();

            if (VERBOSE) {
                if (!RUN)
                    System.out.println("done");
                else if (failcount == 0)
                    System.out.println("+++ ALL PASS");
                else
                    System.out.println("**** " + failcount + " failed out of " + tests.length);
            }
        }
    }

    /**
     * @return true iff it passes.
     */
    private static boolean codeGenTest(String dirAndTest)
    {
        dirAndTest = Util.unixizeFilepath(dirAndTest);
        if (Jaxc.compile(dirAndTest + ".jax")) {
            compileJava(dirAndTest + "Call.java");
            String output = runJavaMain(dirAndTest + "Call");
            if (output.trim().equals("+++ PASS")) {
                if (VERBOSE)
                    System.out.println("+++ PASS " + dirAndTest);
                return true;
            }
        }
        System.out.println("**** FAIL " + dirAndTest);
        return false;
    }

    public static void compileJava(String filepath)
    {
        filepath = Util.unixizeFilepath(filepath);
        String[] dirAndFile = Util.splitDirAndFile(filepath);
        String[] cmd = { "javac", "-cp", Util.platformizeFilepath(dirAndFile[0]), Util.platformizeFilepath(filepath)};
        StringBuffer stderrBuffer = new StringBuffer();
        Util.exec(cmd, null, stderrBuffer);
        System.err.print(stderrBuffer.toString());
    }

    public static String runJavaMain(String javaFilepath)
    {
        String[] dirAndFile = Util.splitDirAndFile(javaFilepath);
        String className = dirAndFile[1];
        String[] cmd = { "java", "-cp", Util.platformizeFilepath(dirAndFile[0]), className };
        StringBuffer stdoutBuffer = new StringBuffer(), stderrBuffer = new StringBuffer();
        Util.exec(cmd, stdoutBuffer, stderrBuffer);
        System.err.print(stderrBuffer.toString());
        return stdoutBuffer.toString();
    }
}
