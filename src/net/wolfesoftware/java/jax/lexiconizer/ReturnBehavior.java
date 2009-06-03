package net.wolfesoftware.java.jax.lexiconizer;

public class ReturnBehavior
{
    public static final ReturnBehavior VOID = new ReturnBehavior(Type.KEYWORD_VOID, 0);

    public Type type;
    public int stackRequirement;

    public ReturnBehavior(Type type, int stackRequirement)
    {
        this.type = type;
        this.stackRequirement = stackRequirement;
    }
    
    public ReturnBehavior clone(int ensureStackRequirement) {
        return new ReturnBehavior(type, Math.max(stackRequirement, ensureStackRequirement));
    }
}
