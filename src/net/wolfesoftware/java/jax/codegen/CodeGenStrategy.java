package net.wolfesoftware.java.jax.codegen;

import java.io.FileNotFoundException;
import net.wolfesoftware.java.jax.ast.Program;

public abstract class CodeGenStrategy
{
    public abstract void generateCode(Program root, String outputFilename) throws FileNotFoundException;
}
