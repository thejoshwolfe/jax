package net.wolfesoftware.jax.semalysis;

import java.util.*;

public abstract class Type
{
    /** ex: "java.lang.String" */
    public final String qualifiedName;
    /** ex: "String" */
    public final String simpleName;

    public Type(String qualifiedName, String simpleName)
    {
        this.qualifiedName = qualifiedName;
        this.simpleName = simpleName;
    }

    public final Method resolveMethod(String name, Type[] argumentSignature)
    {
        LinkedList<Method> overloads = new LinkedList<Method>();
        for (Method method : getMethods())
            if (method.name.equals(name))
                overloads.add(method);
        return resolveOverloads(overloads, argumentSignature);
    }
    public final Constructor resolveConstructor(Type[] argumentSignature)
    {
        return resolveOverloads(getConstructors(), argumentSignature);
    }
    protected abstract LinkedList<Method> getMethods();
    protected abstract LinkedList<Constructor> getConstructors();
    private static final Comparator<TakesArguments> overloadSorter = new Comparator<TakesArguments>() {
        public int compare(TakesArguments o1, TakesArguments o2)
        {
            Type[] params1 = o1.argumentSignature;
            Type[] params2 = o2.argumentSignature;
            for (int i = 0; i < params1.length; i++) {
                if (params1[i] == params2[i])
                    continue;
                return params1[i].isInstanceOf(params2[i]) ? -1 : 1;
            }
            // declaringType is a lie, so we'll use the return type as a hack to resolve shadowing
            return o1.returnType.isInstanceOf(o2.returnType) ? -1 : 1;
        }
    };
    private <T extends TakesArguments> T resolveOverloads(LinkedList<T> overloads, Type[] argumentSignature)
    {
        ArrayList<T> candidates = new ArrayList<T>();
        findCandidates: for (T candidate : overloads) {
            Type[] candidateArgumentSignature = candidate.argumentSignature;
            if (candidateArgumentSignature.length != argumentSignature.length)
                continue; // wrong number of arugments
            for (int i = 0; i < candidateArgumentSignature.length; i++) {
                if (!argumentSignature[i].isInstanceOf(candidateArgumentSignature[i]))
                    continue findCandidates; // can't cast it
            }
            candidates.add(candidate);
        }
        if (candidates.size() == 0)
            return null;
        Collections.sort(candidates, overloadSorter);
        return candidates.get(0);
    }
    public abstract Field resolveField(String name);
    public abstract boolean isInstanceOf(Type type);
    /** example: java/lang/String */
    public String getTypeName()
    {
        return qualifiedName.replace('.', '/');
    }
    /** example: Ljava/lang/String; 
     * <p/>
     * http://java.sun.com/docs/books/jvms/second_edition/html/ClassFile.doc.html#84645
     */
    public String getTypeCode()
    {
        return "L" + getTypeName() + ";";
    }

    public abstract int getType();

    public String toString()
    {
        return qualifiedName;
    }

    public boolean isInterface()
    {
        return false;
    }
    public boolean isPrimitive()
    {
        return false;
    }
    public boolean isNumeric()
    {
        return false;
    }

    public int getSize()
    {
        return 1;
    }

    public abstract Type getParent();

    public boolean isVoidLike()
    {
        return false;
    }
}
