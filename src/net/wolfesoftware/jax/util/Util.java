package net.wolfesoftware.jax.util;

import java.io.*;
import java.util.*;

public final class Util
{
    private Util()
    {
    }

    public static String[] fileToLines(String fileName) throws FileNotFoundException
    {
        Scanner scanner = new Scanner(new File(fileName));
        ArrayList<String> lines = new ArrayList<String>();
        while (scanner.hasNextLine())
            lines.add(scanner.nextLine());
        return lines.toArray(new String[lines.size()]);
    }

    public static String fileToString(String fileName) throws FileNotFoundException
    {
        return join(fileToLines(fileName), "\n");
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
        if (filepath.contains("/"))
        {
            int splitter = filepath.lastIndexOf('/');
            dirAndFile[0] = filepath.substring(0, splitter);
            dirAndFile[1] = filepath.substring(splitter + 1);
        }
        else
        {
            dirAndFile[0] = ".";
            dirAndFile[1] = filepath;
        }
        return dirAndFile;
    }

    public static int exec(String[] cmd, StringBuffer stdoutBuffer, StringBuffer stderrBuffer)
    {
        if (stdoutBuffer == null)
            stdoutBuffer = new StringBuffer();
        if (stderrBuffer == null)
            stderrBuffer = new StringBuffer();
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            InputStreamReader stdout = new InputStreamReader(p.getInputStream());
            InputStreamReader stderr = new InputStreamReader(p.getErrorStream());
            while (true) {
                Integer exitValue = null;
                try {
                    exitValue = p.exitValue();
                } catch (IllegalThreadStateException e) {
                }
                while (stdout.ready())
                    stdoutBuffer.append((char)stdout.read());
                while (stderr.ready())
                    stderrBuffer.append((char)stderr.read());
                if (exitValue != null)
                    return exitValue;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String readAll(InputStream input)
    {
        Scanner scanner = new Scanner(input);
        StringBuilder stringBuilder = new StringBuilder();
        while (scanner.hasNextLine())
            stringBuilder.append(scanner.nextLine()).append('\n');
        return stringBuilder.toString();
    }

    public static void _assert(boolean pass)
    {
        if (!pass)
            throw new AssertionError();
    }
}
