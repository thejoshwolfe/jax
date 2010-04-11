package net.wolfesoftware.jax;

public class CompileError
{
    private final String message;

    public CompileError(String message)
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
