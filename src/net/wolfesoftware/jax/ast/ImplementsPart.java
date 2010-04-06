package net.wolfesoftware.jax.ast;

public class ImplementsPart extends ParseElement
{
    public InterfaceList interfaceList;
    public ImplementsPart(InterfaceList interfaceList)
    {
        this.interfaceList = interfaceList;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append(" implements ");
        interfaceList.decompile(indentation, out);
    }

    public static final int TYPE = 0x2a1c05b6;
    public int getElementType()
    {
        return TYPE;
    }
}
