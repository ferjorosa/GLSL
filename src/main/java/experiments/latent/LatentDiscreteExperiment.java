package experiments.latent;

import methods.latent.LatentDiscreteMethod;

import java.util.Set;

public abstract class LatentDiscreteExperiment {

    protected Set<LatentDiscreteMethod> methods;

    public LatentDiscreteExperiment(Set<LatentDiscreteMethod> methods) {
        this.methods = methods;
    }
}
