package net.wolfesoftware.jax.test;

import java.io.*;
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
}
