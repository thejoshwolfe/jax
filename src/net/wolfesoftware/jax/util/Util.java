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

    public static String[] readLines(String filePath) throws FileNotFoundException
    {
        Scanner scanner = new Scanner(new File(filePath));
        ArrayList<String> lines = new ArrayList<String>();
        while (scanner.hasNextLine())
            lines.add(scanner.nextLine());
        return lines.toArray(new String[lines.size()]);
    }

    /**
     * converts all newlines to <code>'\n'</code>.
     */
    public static String readTextFile(String filePath) throws FileNotFoundException
    {
        return join(readLines(filePath), "\n");
    }

    public static <T> String join(Iterable<T> elements, String delimiter)
    {
        Iterator<T> iterator = elements.iterator();
        if (!iterator.hasNext())
            return "";
        StringBuilder builder = new StringBuilder();
        builder.append(iterator.next());
        while (iterator.hasNext()) {
            builder.append(delimiter);
            builder.append(iterator.next());
        }
        return builder.toString();
    }
    public static <T> String join(T[] elements, String delimiter)
    {
        if (elements.length == 0)
            return "";
        StringBuilder builder = new StringBuilder();
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
