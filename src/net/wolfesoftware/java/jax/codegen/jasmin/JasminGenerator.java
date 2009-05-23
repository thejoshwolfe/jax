package net.wolfesoftware.java.jax.codegen.jasmin;

import java.io.*;
import net.wolfesoftware.java.jax.ast.Program;
import net.wolfesoftware.java.jax.codegen.*;

public class JasminGenerator extends CodeGenerator
{
    private final Program root;
    private final PrintWriter out;
    public JasminGenerator(Program root, String outputFilename) throws FileNotFoundException
    {
        this.root = root;
        out = new PrintWriter(outputFilename);
    }

    public static final CodeGenStrategy STRATEGY = new CodeGenStrategy() {
        public void generateCode(Program root, String outputFilename) throws FileNotFoundException
        {
            new JasminGenerator(root, outputFilename).generateCode();
        }
    };

    protected void generateCode()
    {
        genProgram(root);
    }
    
    protected void genProgram(Program program)
    {
        out.println(IJasminConstants.defualtConstructor);
        
        // TODO: comprehend program
    }
}
