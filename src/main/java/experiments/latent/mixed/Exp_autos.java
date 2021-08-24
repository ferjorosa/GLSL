package experiments.latent.mixed;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.io.DataStreamLoader;
import eu.amidst.extension.data.DataUtils;
import eu.amidst.extension.util.LogUtils;
import eu.amidst.extension.util.PriorsFromData;
import eu.amidst.extension.util.tuple.Tuple2;
import experiments.latent.LatentCrossValidationExperiment;
import experiments.latent.LatentMixedExperiment;
import experiments.util.GenerateLatentData;
import methods.BayesianMethod;
import methods.latent.*;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Exp_autos extends LatentMixedExperiment implements LatentCrossValidationExperiment {

    public Exp_autos(Set<LatentMixedMethod> methods) { super(methods); }

    public static void main(String[] args) throws Exception {
        long seed = 0;
        int kFolds = 10;
        int run = 1;
        LogUtils.LogLevel logLevel = LogUtils.LogLevel.INFO;

        Set<LatentMixedMethod> methods = new LinkedHashSet<>();
//        methods.add(new VariationalLCM(seed));
//        methods.add(new ConstrainedIncrementalLearner(seed, 1, true, true, true, 3, false, false, new SimpleLocalVBEM()));
//        methods.add(new ConstrainedIncrementalLearner(seed, 10, true, true, true, 3, false, false, new SimpleLocalVBEM()));
//        methods.add(new IncrementalLearner(seed, false, true, true, new SimpleLocalVBEM()));
        methods.add(new HC());

        Exp_autos experiment = new Exp_autos(methods);
        experiment.runCrossValExperiment(seed, kFolds, run, logLevel);
    }

    @Override
    public void runCrossValExperiment(long seed, int kFolds, int run, LogUtils.LogLevel foldLogLevel) throws Exception {
        System.out.println("------------------------------------------------------------------");
        System.out.println("------------------------------------------------------------------");
        System.out.println("------------------------------ AUTOS -----------------------------");
        System.out.println("------------------------------------------------------------------");
        System.out.println("------------------------------------------------------------------");

        String dataName = "autos";
        String filename = "data/mixed/"+dataName+".arff";
        DataOnMemory<DataInstance> data = DataStreamLoader.open(filename).toDataOnMemory();
        DataUtils.defineAttributesMaxMinValues(data);

        /* Generate data folds */
        List<Tuple2<DataOnMemory<DataInstance>, DataOnMemory<DataInstance>>> folds = GenerateLatentData.generate(data, kFolds);
        System.out.println(kFolds + " folds have been generated");

        /* Filter Bayesian methods and assign them the empirical Bayes priors */
        final Map<String, double[]> priors = PriorsFromData.generate(data, 1);
        this.methods.stream()
                .filter(x -> x instanceof BayesianMethod)
                .forEach(x -> ((BayesianMethod) x).setPriors(priors));

        /* Run methods */
        for (LatentMixedMethod method : methods)
            method.runLatentMixed(folds, dataName, run, LogUtils.LogLevel.INFO);
    }
}
