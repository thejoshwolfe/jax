package net.wolfesoftware.java.jax;

import java.io.*;
import java.util.Scanner;
import net.wolfesoftware.java.common.Util;
import net.wolfesoftware.java.jax.codegen.CodeGenerator;
import net.wolfesoftware.java.jax.lexiconizer.*;
import net.wolfesoftware.java.jax.parser.*;
import net.wolfesoftware.java.jax.tokenizer.*;

public class Main
{
    public static void main(String[] args) throws FileNotFoundException
    {
        String infile = null;
        for (String arg : args)
        {
            if (arg.startsWith("-in="))
                infile = arg.substring("-in=".length()).replace('/', '\\');
        }
        if (infile == null)
            return;

        comprehend(infile);
    }

    public static void comprehend(String fileName) throws FileNotFoundException
    {
        boolean cleanup = false; // turn this on if you want

        Tokenization tokenization = Tokenizer.tokenize(Util.fileToString(fileName));
        Parsing parsing = Parser.parse(tokenization);
        Lexiconization lexiconization = Lexiconizer.lexiconize(parsing);
        String outFileName = fileName.substring(0, fileName.lastIndexOf('.')) + ".jasmin";
        CodeGenerator.generateCode(lexiconization, outFileName);

        // test (sorry UNIX, we're using backslashes)
        String outDir;
        if (outFileName.contains("\\"))
            outDir = outFileName.substring(0, outFileName.lastIndexOf('\\'));
        else
            outDir = ".";
        try {
            // assemble test.jasmin
            Runtime.getRuntime().exec("java -jar jars\\jasmin.jar -d " + outDir + " " + outFileName).waitFor();
            // compile Call.java
            Runtime.getRuntime().exec("javac -cp " + outDir + " test\\Call.java").waitFor();
            // run Call.main()
            Process p = Runtime.getRuntime().exec("java -cp " + outDir + " Call");
            Scanner output = new Scanner(p.getInputStream());
            while (output.hasNextLine())
                System.out.println(output.nextLine());
            // should print "+++ PASS" to System.out

            // clean up
            if (cleanup)
            {
                new File(outFileName).delete();
                new File(outFileName.substring(0, outFileName.lastIndexOf('.')) + ".class").delete();
                new File("test\\Call.class").delete();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
