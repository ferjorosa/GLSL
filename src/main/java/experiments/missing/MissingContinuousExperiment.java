package experiments.missing;

import methods.missing.MissingContinuousMethod;

import java.util.Set;

public abstract class MissingContinuousExperiment implements MissingExperiment {

    protected Set<MissingContinuousMethod> methods;

    public MissingContinuousExperiment(Set<MissingContinuousMethod> methods) {
        this.methods = methods;
    }
}
