package net.wolfesoftware.jax.semalysis;

import java.util.*;
import net.wolfesoftware.jax.ast.*;
import net.wolfesoftware.jax.codegen.ClassFile;
import net.wolfesoftware.jax.util.Util;

public class LocalType extends Type
{
    public final LinkedList<Constructor> constructors = new LinkedList<Constructor>();
    private final LinkedList<Method> methods = new LinkedList<Method>();
    private final HashMap<String, Field> fields = new HashMap<String, Field>();
    public final ArrayList<Expression> staticInitializerExpressions = new ArrayList<Expression>();
    public final ArrayList<Expression> initializerExpressions = new ArrayList<Expression>();
    public LocalType(String qualifiedName, String id)
    {
        super(qualifiedName, id);
    }

    public void addConstructor(Constructor constructor)
    {
        constructors.add(constructor);
    }
    public void addMethod(Method method)
    {
        methods.add(method);
    }
    @Override
    protected LinkedList<Method> getMethods()
    {
        return methods;
    }
    @Override
    protected LinkedList<Constructor> getConstructors()
    {
        return constructors;
    }
    @Override
    public Field resolveField(String name)
    {
        return fields.get(name);
    }

    @Override
    public boolean isInstanceOf(Type type)
    {
        // TODO implement this
        return false;
    }

    public static final int TYPE = 0x1144038e;
    public int getType()
    {
        return TYPE;
    }

    public short getFlags()
    {
        return ClassFile.ACC_PUBLIC;
    }

    public Type getParent()
    {
        return RuntimeType.OBJECT;
    }

    public Type[] getInterfaces()
    {
        return new Type[0];
    }

    public ClassMember makeDefaultConstructor(ClassBody classBody)
    {
        MethodModifiers methodModifiers = new MethodModifiers(Util.arrayToList(MethodModifier.PUBLIC));
        TypeId typeId = TypeId.fromId(new Id(id));
        ArgumentDeclarations argumentDeclarations = new ArgumentDeclarations(new LinkedList<VariableDeclaration>());
        MaybeThrows maybeThrows = MaybeThrows.DOESNT;
        Expression expression = new Expression(Block.EMPTY);
        return new ClassMember(new ConstructorDeclaration(methodModifiers, typeId, argumentDeclarations, maybeThrows, expression));
    }
}
