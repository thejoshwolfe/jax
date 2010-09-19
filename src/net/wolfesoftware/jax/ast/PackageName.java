package net.wolfesoftware.jax.ast;

import java.util.HashMap;

public class PackageName extends ParseElement
{
    protected HashMap<String, PackageName> subPackages = new HashMap<String, PackageName>();
    private HashMap<String, String> types = new HashMap<String, String>();

    protected final String name;
    private PackageName(String name)
    {
        this.name = name;
    }

    /**
     * @return full name suitable for {@link Class#forName(String)} such as <code>"java.lang.String"</code>.
     */
    public String getTypeName(String name)
    {
        return types.get(name);
    }
    public PackageName getSubPackage(String name)
    {
        return subPackages.get(name);
    }
    public PackageName makeSubPackage(String name)
    {
        PackageName _package = new PackageName(this.name + "." + name);
        this.subPackages.put(name, _package);
        return _package;
    }
    public void putTypeName(String name)
    {
        types.put(name, this.name + "." + name);
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append(name);
    }

    public static final int TYPE = 0x44830743;
    public int getElementType()
    {
        return TYPE;
    }

    public static PackageName makeTopLevelPackage(String name)
    {
        return new PackageName(name);
    }
}
