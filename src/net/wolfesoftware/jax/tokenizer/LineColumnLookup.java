package net.wolfesoftware.jax.tokenizer;

import java.util.*;
import java.util.regex.*;

public class LineColumnLookup
{
    private final String source;
    private int[] lineIndeces = null;

    public LineColumnLookup(String source)
    {
        this.source = source;
    }

    public void getLineAndColumn(int offset, LineAndColumn out_lineAndColumn)
    {
        ensureCached();
        int line = Math.abs(Arrays.binarySearch(lineIndeces, offset) + 1) - 1;
        int column = offset - lineIndeces[line];
        out_lineAndColumn.line = line;
        out_lineAndColumn.column = column;
    }

    private static final Pattern newlinePatter = Pattern.compile("\r\n?|\n");
    private void ensureCached()
    {
        if (lineIndeces != null)
            return;
        ArrayList<Integer> lineIndecesList = new ArrayList<Integer>();
        lineIndecesList.add(0);
        Matcher newlineMatcher = newlinePatter.matcher(source);
        while (newlineMatcher.find())
            lineIndecesList.add(newlineMatcher.end());
        lineIndeces = new int[lineIndecesList.size()];
        int i = 0;
        for (Integer index : lineIndecesList)
            lineIndeces[i++] = index;
    }
    public static class LineAndColumn
    {
        public int line, column;
    }
}
