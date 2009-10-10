package net.wolfesoftware.jax.ast;

public class CompilationUnit extends ParseElement
{
    public Imports imports;
    public ClassDeclaration classDeclaration;
    public CompilationUnit(Imports imports, ClassDeclaration classDeclaration)
    {
        this.imports = imports;
        this.classDeclaration = classDeclaration;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        imports.decompile(indentation, out);
        classDeclaration.decompile(indentation, out);
    }

    public static final int TYPE = 0x301e0620;
    public int getElementType()
    {
        return TYPE;
    }
}
