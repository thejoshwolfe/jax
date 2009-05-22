package net.wolfesoftware.java.jax.parser;

import java.util.ArrayList;
import net.wolfesoftware.java.jax.parser.elements.Program;

public class Parsing
{
    public final Program root;
    public final ArrayList<ParsingException> errors;
    public Parsing(Program root, ArrayList<ParsingException> errors)
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
