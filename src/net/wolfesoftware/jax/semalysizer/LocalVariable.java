package net.wolfesoftware.jax.semalysizer;

public class LocalVariable extends SecretLocalVariable
{
    public final String name;

    public LocalVariable(String name, Type type)
    {
        super(type);
        this.name = name;
    }
}
