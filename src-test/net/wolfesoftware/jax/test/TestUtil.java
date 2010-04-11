package net.wolfesoftware.jax.test;

import java.io.*;
import net.wolfesoftware.jax.util.Util;

public class TestUtil
{
    private TestUtil()
    {
        // don't instantiate this class
        Util._assert(false);
    }

    /**
     * Executes a command and waits for the process to exit.
     * The process is queried every 100 milliseconds for completion.
     * stdoutBuffer and stderrBuffer can be the same object.
     * 
     * @param cmd the command to be passed to {@link Runtime#exec(String[])}.
     * @param stdoutStream stream to which the process's stdout will be written. can be null.
     * @param stderrStream stream to which the process's stderr will be written. can be null.
     * @return the exit value of the process
     * @throws RuntimeException IOException and InterruptedException are wrapped in RuntimeException and thrown
     */
    public static int exec(String[] cmd, PrintStream stdoutStream, PrintStream stderrStream)
    {
        if (stdoutStream == null)
            stdoutStream = new PrintStream(new ByteArrayOutputStream());
        if (stderrStream == null)
            stderrStream = new PrintStream(new ByteArrayOutputStream());
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            InputStreamReader stdout = new InputStreamReader(p.getInputStream());
            InputStreamReader stderr = new InputStreamReader(p.getErrorStream());
            while (true) {
                int exitValue = -1;
                boolean done;
                try {
                    exitValue = p.exitValue();
                    done = true;
                } catch (IllegalThreadStateException e) {
                    done = false;
                }
                while (stdout.ready())
                    stdoutStream.print((char)stdout.read());
                while (stderr.ready())
                    stderrStream.print((char)stderr.read());
                if (done)
                    return exitValue;
                Thread.sleep(100);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static String[] splitDirAndFile(String filepath)
    {
        String[] dirAndFile = new String[2];
        if (filepath.contains("/")) {
            int splitter = filepath.lastIndexOf('/');
            dirAndFile[0] = filepath.substring(0, splitter);
            dirAndFile[1] = filepath.substring(splitter + 1);
        } else {
            dirAndFile[0] = ".";
            dirAndFile[1] = filepath;
        }
        return dirAndFile;
    }

    
}
