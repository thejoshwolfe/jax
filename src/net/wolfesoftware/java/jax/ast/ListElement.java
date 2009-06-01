package net.wolfesoftware.java.jax.ast;

import java.util.List;

public abstract class ListElement<T extends ParseElement> extends ParseElement
{
    public List<T> elements;
    public ListElement(List<T> elements)
    {
        this.elements = elements;
    }

    @Override
    protected final void decompile(String indentation, StringBuilder out)
    {
        int size = elements.size();
        if (size == 0 || (size == 1 && elements.get(0) == null))
            return;
        String primer = getPrimer();
        out.append(primer);
        if (primer.endsWith("\n"))
            out.append(indentation);
        ParseElement element = elements.get(0);
        if (element != null)
            element.decompile(indentation, out);
        String delimiter = getDelimiter();
        boolean delimitNewLines = delimiter.endsWith("\n");
        for (int i = 1; i < size; i++)
        {
            out.append(delimiter);
            if (delimitNewLines)
                out.append(indentation);
            element = elements.get(i);
            if (element != null)
                element.decompile(indentation, out);
        }
    }

    protected abstract String getDelimiter();

    protected String getPrimer()
    {
        return "";
    }
}
