package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.BranchDestination;

public abstract class LoopElement extends ParseElement
{
    public BranchDestination breakDestination = new BranchDestination();
    public BranchDestination continueDestination = new BranchDestination();
}
