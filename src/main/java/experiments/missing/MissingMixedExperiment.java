package experiments.missing;

import methods.missing.MissingMixedMethod;

import java.util.Set;

public abstract class MissingMixedExperiment implements MissingExperiment {

    protected Set<MissingMixedMethod> methods;

    public MissingMixedExperiment(Set<MissingMixedMethod> methods) {
        this.methods = methods;
    }
}
