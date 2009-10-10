package net.wolfesoftware.jax.util;

import java.io.InputStream;

public class TestUtils
{
    private TestUtils()
    {
    }

    public static void compileJava(String filepath)
    {
        filepath = Util.unixizeFilepath(filepath);
        String[] dirAndFile = Util.splitDirAndFile(filepath);
        String[] cmd = { "javac", "-cp", Util.platformizeFilepath(dirAndFile[0]), Util.platformizeFilepath(filepath)};
        Util.exec(cmd);
    }

    public static void compileJasmin(String filepath)
    {
        filepath = Util.unixizeFilepath(filepath);
        String[] dirAndFile = Util.splitDirAndFile(filepath);
        String[] cmd = { "java", "-jar", Util.platformizeFilepath("jars/jasmin.jar"), "-d", Util.platformizeFilepath(dirAndFile[0]), Util.platformizeFilepath(filepath) };
        Util.exec(cmd);
    }

    public static InputStream runJavaMain(String javaFilepath)
    {
        String[] dirAndFile = Util.splitDirAndFile(javaFilepath);
        String className = dirAndFile[1];
        String[] cmd = { "java", "-cp", Util.platformizeFilepath(dirAndFile[0]), className };
        return Util.exec(cmd);
    }
}
