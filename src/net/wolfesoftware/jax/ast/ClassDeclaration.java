package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.lexiconizer.LocalType;

public class ClassDeclaration extends ParseElement
{
    public Id id;
    public ClassBody classBody;
    public LocalType localType;
    public ClassDeclaration(Id id, ClassBody classBody)
    {
        this.id = id;
        this.classBody = classBody;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append("class ");
        id.decompile(indentation, out);
        out.append("{");
        classBody.decompile(increaseIndentation(indentation), out);
        out.append("\n").append(indentation).append("}");
    }

    public static final int TYPE = 0x3465065d;
    public int getElementType()
    {
        return TYPE;
    }

}
