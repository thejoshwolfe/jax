package net.wolfesoftware.java.jax.util;

import java.io.*;
import java.util.*;
import java.util.regex.*;

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

    public static String decodeLiteral(String literal)
    {
        if (literal.startsWith("\"") && literal.endsWith("\""))
            return literal.substring(1, literal.length() - 1);
        return null;
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

    public static boolean nonNull(Object...objects)
    {
        for (Object o : objects)
            if (o == null)
                return false;
        return true;
    }

    /** 
     * copied and modified from Java's source code for {@link Pattern#split(CharSequence, int)} 
     */
    public static String[] regexSplit(Pattern pattern, String input, int limit, int group)
    {
        int index = 0;
        boolean matchLimited = limit > 0;
        ArrayList<String> matchList = new ArrayList<String>();
        Matcher m = pattern.matcher(input);

        // Add segments before each match found
        while (m.find())
        {
            if (!matchLimited || matchList.size() < limit - 1)
            {
                String match = input.subSequence(index, m.start(group)).toString();
                matchList.add(match);
                index = m.end(group);
            }
            else if (matchList.size() == limit - 1)
            { // last one
                String match = input.subSequence(index, input.length()).toString();
                matchList.add(match);
                index = m.end(group);
            }
        }

        // If no match was found, return this
        if (index == 0)
            return new String[] { input };

        // Add remaining segment
        if (!matchLimited || matchList.size() < limit)
            matchList.add(input.subSequence(index, input.length()).toString());

        // Construct result
        int resultSize = matchList.size();
        if (limit == 0)
            while (resultSize > 0 && matchList.get(resultSize - 1).equals(""))
                resultSize--;
        String[] result = new String[resultSize];
        return matchList.subList(0, resultSize).toArray(result);
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

    public static String escapeJavaStringLiteral(String value)
    {
        StringBuilder stringBuilder = new StringBuilder("\"");
        for (char c : value.toCharArray())
        {
            switch (c)
            {
                case '\b':
                    stringBuilder.append("\\b");
                    break;
                case '\t':
                    stringBuilder.append("\\t");
                    break;
                case '\n':
                    stringBuilder.append("\\n");
                    break;
                case '\f':
                    stringBuilder.append("\\f");
                    break;
                case '\r':
                    stringBuilder.append("\\r");
                    break;
                case '"':
                    stringBuilder.append("\\\"");
                    break;
                case '\'':
                    stringBuilder.append("\\'");
                    break;
                case '\\':
                    stringBuilder.append("\\\\");
                    break;
                default:
                    stringBuilder.append(c);
                    break;
            }
        }
        stringBuilder.append('"');
        return stringBuilder.toString();
    }
}
