package net.wolfesoftware.jax.semalysis;

import java.util.ArrayList;

public class BranchDestination
{
    public Type type;
    public ArrayList<Integer> sources = new ArrayList<Integer>();
    public BranchDestination()
    {
    }
    public void addSource(int offset)
    {
        sources.add(offset);
    }
}
