package net.wolfesoftware.jax;

import net.wolfesoftware.jax.util.Util;

public final class JaxcCompileException extends Exception
{
    private final CompileError[] compileErrors;
    public JaxcCompileException(CompileError[] compileErrors)
    {
        this.compileErrors = compileErrors;
    }

    public CompileError[] getCompileErrors()
    {
        return compileErrors;
    }
    @Override
    public String getMessage()
    {
        return Util.join(compileErrors, "\n\n");
    }
}
