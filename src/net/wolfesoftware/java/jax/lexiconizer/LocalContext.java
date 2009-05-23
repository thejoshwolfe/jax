package net.wolfesoftware.java.jax.lexiconizer;

import java.util.*;

public class LocalContext
{
    public final LocalContext parentContext;
    public final HashMap<String, LocalVariable> localVariables = new HashMap<String, LocalVariable>();

    public LocalContext(LocalContext parentContext)
    {
        this.parentContext = parentContext;
    }
    
}
