package net.wolfesoftware.jax.util;

import java.io.*;
import java.util.*;

public final class Util
{
    private Util()
    {
        // don't instantiate this class
        _assert(false);
    }

    public static String[] fileToLines(String fileName) throws FileNotFoundException
    {
        Scanner scanner = new Scanner(new File(fileName));
        ArrayList<String> lines = new ArrayList<String>();
        while (scanner.hasNextLine())
            lines.add(scanner.nextLine());
        return lines.toArray(new String[lines.size()]);
    }

    public static String readFile(String fileName) throws FileNotFoundException
    {
        return join(fileToLines(fileName), "\n");
    }

    public static <T> String join(Iterable<T> elements, String delimiter)
    {
        StringBuilder builder = new StringBuilder();
        boolean empty = true;
        for (T element : elements) {
            if (empty)
                empty = false;
            else
                builder.append(delimiter);
            builder.append(element);
        }
        return builder.toString();
    }
    public static <T> String join(T[] elements, String delimiter)
    {
        StringBuilder builder = new StringBuilder();
        if (0 < elements.length)
            builder.append(elements[0]);
        for (int i = 1; i < elements.length; i++)
            builder.append(delimiter).append(elements[i]);
        return builder.toString();
    }

    public static void removeAfter(ArrayList<?> list, int index)
    {
        for (int i = list.size() - 1; index <= i; i--)
            list.remove(i);
    }

    public static String unixizeFilepath(String file)
    {
        return file.replace("\\", "/");
    }

    private static final String fileSeparator = System.getProperty("file.separator");

    public static String platformizeFilepath(String file)
    {
        if (fileSeparator.equals("/"))
            return file;
        else
            return file.replace("/", fileSeparator);
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

    public static void _assert(boolean pass)
    {
        if (!pass)
            throw new AssertionError();
    }

    public static <T> ArrayList<T> arrayToList(T... elements)
    {
        ArrayList<T> list = new ArrayList<T>(elements.length);
        for (T element : elements)
            list.add(element);
        return list;
    }
}
