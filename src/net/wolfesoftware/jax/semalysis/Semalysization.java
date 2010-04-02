package net.wolfesoftware.jax.semalysis;

import java.util.ArrayList;
import net.wolfesoftware.jax.ast.Root;

public class Semalysization
{
    public final Root root;
    public final ArrayList<SemalyticalException> errors;
    public Semalysization(Root root, ArrayList<SemalyticalException> errors)
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
