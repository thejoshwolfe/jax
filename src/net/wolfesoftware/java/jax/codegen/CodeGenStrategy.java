package net.wolfesoftware.java.jax.codegen;

import java.io.FileNotFoundException;
import net.wolfesoftware.java.jax.ast.Root;

public abstract class CodeGenStrategy
{
    public abstract void generateCode(Root root, String outputFilename) throws FileNotFoundException;
}
