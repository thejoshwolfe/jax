package net.wolfesoftware.jax;

public abstract class CompileError
{
    private final String message;

    protected CompileError(String message)
    {
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }
    public String toString()
    {
        return getMessage();
    }
}
