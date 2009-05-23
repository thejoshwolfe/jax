package net.wolfesoftware.java.jax.codegen;

import net.wolfesoftware.java.jax.ast.Program;
import net.wolfesoftware.java.jax.codegen.jasmin.JasminGenerator;

public abstract class CodeGenerator
{
    public static void generateCode(Program root, String outputFilename)
    {
        generateCode(root, outputFilename, JasminGenerator.STRATEGY);
    }
    public static void generateCode(Program root, String outputFilename, CodeGenStrategy strategy)
    {
        
    }
    
}
