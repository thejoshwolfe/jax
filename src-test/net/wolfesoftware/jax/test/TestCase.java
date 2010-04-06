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
        new File(Util.platformizeFilepath(filepath)).delete();
    }
    protected boolean compileJax(String filepath, PrintStream verboseStream, JaxcOptions options)
    {
        String switchesString = "";
        if (options != null) {
            switchesString = options.toString();
            if (!switchesString.equals(""))
                switchesString += " ";
        }
        verboseStream.println("jaxc " + switchesString + filepath);
        return Jaxc.compile(filepath, options);
    }
    public String toString()
    {
        return getName();
    }
}
