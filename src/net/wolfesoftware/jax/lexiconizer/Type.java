package net.wolfesoftware.jax.lexiconizer;

import java.util.*;
import net.wolfesoftware.jax.codegen.ClassFile;

public abstract class Type
{
    public final String fullName;
    public final String id;

    public Type(String fullName, String id)
    {
        this.fullName = fullName;
        this.id = id;
    }

    public final Method resolveMethod(String name, Type[] argumentSignature)
    {
        LinkedList<Method> overloads = new LinkedList<Method>();
        for (Method method : getMethods())
            if (method.id.equals(name))
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
            for (int i = 0; i < params1.length; i++)
            {
                if (params1[i] == params2[i])
                    continue;
                return params1[i].isInstanceOf(params2[i]) ? -1 : 1;
            }
            throw new RuntimeException("Duplicate method signatures: " + o1 + " : " + o2);
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
        return fullName.replace('.', '/');
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
        return fullName;
    }

    public boolean isPrimitive()
    {
        return false;
    }

    public int getSize()
    {
        return 1;
    }
}
