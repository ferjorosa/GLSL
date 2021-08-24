package experiments.latent.mixed;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.io.DataStreamLoader;
import eu.amidst.extension.data.DataUtils;
import eu.amidst.extension.learn.structure.typelocalvbem.SimpleLocalVBEM;
import eu.amidst.extension.util.LogUtils;
import eu.amidst.extension.util.PriorsFromData;
import eu.amidst.extension.util.tuple.Tuple2;
import experiments.latent.LatentCrossValidationExperiment;
import experiments.latent.LatentMixedExperiment;
import experiments.util.GenerateLatentData;
import methods.BayesianMethod;
import methods.latent.ConstrainedIncrementalLearner;
import methods.latent.HC;
import methods.latent.LatentMixedMethod;
import methods.latent.VariationalLCM;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Exp_census_india extends LatentMixedExperiment implements LatentCrossValidationExperiment {

    public Exp_census_india(Set<LatentMixedMethod> methods) { super(methods); }

    public static void main(String[] args) throws Exception {
        long seed = 0;
        int kFolds = 10;
        int run = 1;
        LogUtils.LogLevel logLevel = LogUtils.LogLevel.INFO;

        Set<LatentMixedMethod> methods = new LinkedHashSet<>();
        methods.add(new HC());
        methods.add(new VariationalLCM(seed));
        methods.add(new ConstrainedIncrementalLearner(seed, 1, true, true, true, 3, false, false, new SimpleLocalVBEM()));
        methods.add(new ConstrainedIncrementalLearner(seed, 10, true, true, true, 3, false, false, new SimpleLocalVBEM()));
        //methods.add(new IncrementalLearner(seed, false, true, true, new SimpleLocalVBEM()));


        Exp_census_india experiment = new Exp_census_india(methods);
        experiment.runCrossValExperiment(seed, kFolds, run, logLevel);
    }

    @Override
    public void runCrossValExperiment(long seed, int kFolds, int run, LogUtils.LogLevel foldLogLevel) throws Exception {
        System.out.println("------------------------------------------------------------------");
        System.out.println("------------------------------------------------------------------");
        System.out.println("-------------------------- CENSUS_INDIA --------------------------");
        System.out.println("------------------------------------------------------------------");
        System.out.println("------------------------------------------------------------------");

        String dataName = "census_india";
        String filename = "data/mixed/"+dataName+".arff";
        DataOnMemory<DataInstance> data = DataStreamLoader.open(filename).toDataOnMemory();
        DataUtils.defineAttributesMaxMinValues(data);

        /* Generate data folds */
        List<Tuple2<DataOnMemory<DataInstance>, DataOnMemory<DataInstance>>> folds = GenerateLatentData.generate(data, kFolds);
        folds = GenerateLatentData.filterZeroVarianceColumns(folds);
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
