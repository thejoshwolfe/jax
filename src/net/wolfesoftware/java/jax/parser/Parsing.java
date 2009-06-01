package net.wolfesoftware.java.jax.parser;

import java.util.ArrayList;
import net.wolfesoftware.java.jax.ast.Root;

public class Parsing
{
    public final Root root;
    public final ArrayList<ParsingException> errors;
    public Parsing(Root root, ArrayList<ParsingException> errors)
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
