package net.wolfesoftware.jax.semalysis;

public class SecretLocalVariable
{
    public final Type type;
    private final LocalContext context;
    private int number = -1;

    public SecretLocalVariable(LocalContext context, Type type)
    {
        this.context = context;
        this.type = type;
    }

    public int getNumber()
    {
        if (number == -1)
            number = context.getNextLocalVariableNumber(type.getSize());
        return number;
    }
}
