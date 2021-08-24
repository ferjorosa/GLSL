package experiments.latent;


import methods.latent.LatentContinuousMethod;

import java.util.Set;

public abstract class LatentContinuousExperiment {

    protected Set<LatentContinuousMethod> methods;

    public LatentContinuousExperiment(Set<LatentContinuousMethod> methods) {
        this.methods = methods;
    }
}
