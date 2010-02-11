package net.wolfesoftware.jax.semalysizer;

public class LocalVariable extends SecretLocalVariable
{
    public final String name;

    public LocalVariable(String name, Type type, int number)
    {
        super(type, number);
        this.name = name;
    }
}
