package net.wolfesoftware.jax.semalysizer;

public class LocalVariable
{
    public final String name;
    public final Type type;
    public int number;

    public LocalVariable(String name, Type type, int number)
    {
        this.name = name;
        this.type = type;
        this.number = number;
    }
}
