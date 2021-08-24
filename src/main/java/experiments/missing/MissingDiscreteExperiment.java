package experiments.missing;

import methods.missing.MissingDiscreteMethod;

import java.util.Set;

public abstract class MissingDiscreteExperiment implements MissingExperiment {

    protected Set<MissingDiscreteMethod> methods;

    public MissingDiscreteExperiment(Set<MissingDiscreteMethod> methods) {
        this.methods = methods;
    }
}
