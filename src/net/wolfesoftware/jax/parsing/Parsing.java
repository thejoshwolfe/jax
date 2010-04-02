package net.wolfesoftware.jax.parsing;

import java.util.ArrayList;
import net.wolfesoftware.jax.ast.Root;

public class Parsing
{
    public final Root root;
    public final ArrayList<ParsingError> errors;
    public Parsing(Root root, ArrayList<ParsingError> errors)
    {
        this.root = root;
        this.errors = errors;
    }
    public String toString()
    {
        if (errors.size() != 0)
            return "errors: " + errors;
        return root.toString();
    }
}
