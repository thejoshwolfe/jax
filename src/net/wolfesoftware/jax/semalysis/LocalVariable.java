package net.wolfesoftware.jax.semalysis;

public class LocalVariable extends SecretLocalVariable
{
    public final String name;

    public LocalVariable(LocalContext context, String name, Type type)
    {
        super(context, type);
        this.name = name;
    }

    public String toString()
    {
        return type + " " + name;
    }
}
