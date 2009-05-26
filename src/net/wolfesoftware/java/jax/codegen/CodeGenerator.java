package net.wolfesoftware.java.jax.codegen;

import java.io.FileNotFoundException;
import net.wolfesoftware.java.jax.ast.Program;
import net.wolfesoftware.java.jax.codegen.jasmin.JasminGenerator;
import net.wolfesoftware.java.jax.lexiconizer.Lexiconization;

public abstract class CodeGenerator
{
    public static void generateCode(Lexiconization lexiconization, String outputFilename) throws FileNotFoundException
    {
        generateCode(lexiconization.root, outputFilename, JasminGenerator.STRATEGY);
    }
    public static void generateCode(Program root, String outputFilename, CodeGenStrategy strategy) throws FileNotFoundException 
    {
        strategy.generateCode(root, outputFilename);
    }
    
}
