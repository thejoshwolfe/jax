package net.wolfesoftware.java.jax.lexiconizer;

public class ReturnBehavior
{
    public Type type;
    public int stackRequirement;

    public ReturnBehavior(Type type, int stackRequirement)
    {
        this.type = type;
        this.stackRequirement = stackRequirement;
    }
}
