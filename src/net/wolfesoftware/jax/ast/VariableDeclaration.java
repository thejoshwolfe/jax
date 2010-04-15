package net.wolfesoftware.jax.ast;

public class VariableDeclaration extends ParseElement
{
    public TypeId typeId;

    public String variableName;

    public VariableDeclaration(TypeId type, String variableName)
    {
        this.typeId = type;
        this.variableName = variableName;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        typeId.decompile(indentation, out);
        out.append(' ').append(variableName);
    }

    public static final int TYPE = 0x49f4078d;
    public int getElementType()
    {
        return TYPE;
    }
}
