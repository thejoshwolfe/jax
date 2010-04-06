package net.wolfesoftware.jax.ast;

public class CompilationUnit extends ParseElement
{
    public PackageStatements packageStatements;
    public Imports imports;
    public ClassDeclaration classDeclaration;
    public CompilationUnit(PackageStatements packageStatements, Imports imports, ClassDeclaration classDeclaration)
    {
        this.packageStatements = packageStatements;
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
