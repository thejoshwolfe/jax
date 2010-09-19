package net.wolfesoftware.jax.semalysis;

import net.wolfesoftware.jax.ast.PackageName;

public class BuiltinPackageLister
{
    public static final PackageName[] topLevelPackages;
    static {
        PackageName java = PackageName.makeTopLevelPackage("java");
        PackageName java_lang = java.makeSubPackage("lang");
        java_lang.putTypeName("AbstractMethodError");
        java_lang.putTypeName("Appendable");
        topLevelPackages = new PackageName[] { java };
    }
}
