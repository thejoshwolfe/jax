package net.wolfesoftware.java.jax.lexiconizer;

import java.util.ArrayList;
import net.wolfesoftware.java.jax.ast.Program;

public class Lexiconization
{
    public final Program root;
    public final ArrayList<LexicalException> errors;
    public Lexiconization(Program root, ArrayList<LexicalException> errors)
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
