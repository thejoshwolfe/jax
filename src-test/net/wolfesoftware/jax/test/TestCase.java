package net.wolfesoftware.jax.test;

import java.io.*;
import net.wolfesoftware.jax.*;
import net.wolfesoftware.jax.util.Util;

public abstract class TestCase
{
    public abstract String getName();
    public abstract void clean();
    public abstract boolean run(PrintStream verboseStream, PrintStream stderrStream);

    protected void deleteFile(String filepath)
    {
        new File(filepath).delete();
    }
    protected static boolean compileJax(String filepath, JaxcOptions options, PrintStream verboseStream, PrintStream stderrStream)
    {
        String switchesString = "";
        if (options != null) {
            switchesString = options.toString();
            if (!switchesString.equals(""))
                switchesString += " ";
        }
        verboseStream.println("jaxc " + switchesString + filepath);
        try {
            Jaxc.compile(filepath, options);
        } catch (JaxcCompileException e) {
            stderrStream.println(e.getMessage());
            return false;
        }
        return true;
    }
    protected static boolean compileJava(String classPath, String filepath, PrintStream verboseStream, PrintStream stderrStream)
    {
        return execSomething(new String[] { "javac", "-cp", classPath, filepath }, verboseStream, stderrStream, null);
    }
    protected static boolean execJava(String classPath, String mainClass, PrintStream verboseStream, PrintStream stderrStream, PrintStream stdoutStream)
    {
        return execSomething(new String[] { "java", "-cp", classPath, mainClass}, verboseStream, stderrStream, stdoutStream);
    }
    private static boolean execSomething(String[] cmd, PrintStream verboseStream, PrintStream stderrStream, PrintStream stdoutStream)
    {
        verboseStream.println(Util.join(cmd, " "));
        if (TestUtil.exec(cmd, stdoutStream, stderrStream) != 0)
            return false;
        return true;
    }
    public String toString()
    {
        return getName();
    }
}
