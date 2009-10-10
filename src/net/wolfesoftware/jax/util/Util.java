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

    public static InputStream exec(String[] cmd)
    {
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            return p.waitFor() == 0 ? p.getInputStream() : null;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
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
