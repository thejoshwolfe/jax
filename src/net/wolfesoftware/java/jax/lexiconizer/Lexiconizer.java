package net.wolfesoftware.java.jax.lexiconizer;

import java.util.HashMap;
import net.wolfesoftware.java.jax.parser.Parsing;

public class Lexiconizer
{
    public static Lexiconization lexiconize(Parsing parsing)
    {
        return new Lexiconizer(parsing).lixiconize();
    }
    
    private final HashMap<String, Type> importedTypes = new HashMap<String, Type>();
    {
        Type.initPrimitives(importedTypes);
    }
    
    private Lexiconizer(Parsing parsing)
    {
        // TODO: ensure types match up.
        // There is no type coercion or even implicit type casting yet, 
        // so exact matches is all that must be verified.
    }
    
    private Lexiconization lixiconize()
    {
        return null;
    }
}
