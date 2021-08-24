package experiments.latent.discrete;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.io.DataStreamLoader;
import eu.amidst.extension.learn.structure.BLFM_BinA;
import eu.amidst.extension.learn.structure.typelocalvbem.SimpleLocalVBEM;
import eu.amidst.extension.util.LogUtils;
import eu.amidst.extension.util.PriorsFromData;
import eu.amidst.extension.util.tuple.Tuple2;
import experiments.latent.LatentCrossValidationExperiment;
import experiments.latent.LatentDiscreteExperiment;
import experiments.latent.LatentLearnExperiment;
import experiments.util.GenerateLatentData;
import methods.BayesianMethod;
import methods.latent.*;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Exp_webkb_336 extends LatentDiscreteExperiment implements LatentCrossValidationExperiment, LatentLearnExperiment {

    public Exp_webkb_336(Set<LatentDiscreteMethod> methods) {
        super(methods);
    }

    public static void main(String[] args) throws Exception {

        long seed = 0;
        int kFolds = 10;
        int run = 1;
        LogUtils.LogLevel logLevel = LogUtils.LogLevel.INFO;

        Set<LatentDiscreteMethod> methods = new LinkedHashSet<>();
        methods.add(new BinA(seed, BLFM_BinA.LinkageType.AVERAGE));
        methods.add(new LCM(seed));
        methods.add(new ConstrainedIncrementalLearner(seed, 10, false, true, true, 3, false, false, new SimpleLocalVBEM()));
        methods.add(new ConstrainedIncrementalLearner(seed, 1, false, true, true, 3, false, false, new SimpleLocalVBEM()));
        methods.add(new IncrementalLearner(seed, false, true, true, new SimpleLocalVBEM()));

        Exp_webkb_336 webkb_336 = new Exp_webkb_336(methods);
        webkb_336.runCrossValExperiment(seed, kFolds, run, logLevel);
    }

    @Override
    public void runCrossValExperiment(long seed, int kFolds, int run, LogUtils.LogLevel foldLogLevel) throws Exception {

        System.out.println("------------------------------------------------------------------");
        System.out.println("------------------------------------------------------------------");
        System.out.println("---------------------------- WEBKB 336 ---------------------------");
        System.out.println("------------------------------------------------------------------");
        System.out.println("------------------------------------------------------------------");

        /* Generate data folds */
        String filename = "data/discrete/webkb_336.arff";
        String dataName = "webkb_336";

        DataOnMemory<DataInstance> data = DataStreamLoader.open(filename).toDataOnMemory();
        List<Tuple2<DataOnMemory<DataInstance>, DataOnMemory<DataInstance>>> folds = GenerateLatentData.generate(data, kFolds);
        System.out.println(kFolds + " folds have been generated");

        /* Filter Bayesian methods and assign them the empirical Bayes priors */
        final Map<String, double[]> priors = PriorsFromData.generate(data, 1);
        methods.stream()
                .filter(x -> x instanceof BayesianMethod)
                .forEach(x-> ((BayesianMethod) x).setPriors(priors));

        /* Run methods */
        for(LatentDiscreteMethod method: methods)
            method.runLatentDiscrete(folds, "webkb_336", run, LogUtils.LogLevel.INFO);
    }

    @Override
    public void runLearnExperiment(long seed, LogUtils.LogLevel logLevel) throws Exception {

        System.out.println("------------------------------------------------------------------");
        System.out.println("------------------------------------------------------------------");
        System.out.println("------------------------ 4 UNIVERSITIES --------------------------");
        System.out.println("------------------------------------------------------------------");
        System.out.println("------------------------------------------------------------------");

        String filename = "data/discrete/webkb_336/webkb_336.arff";
        String dataName = "webkb_336";

        DataOnMemory<DataInstance> data = DataStreamLoader.open(filename).toDataOnMemory();

        /* Filter Bayesian methods and assign them the empirical Bayes priors */
        final Map<String, double[]> priors = PriorsFromData.generate(data, 1);
        methods.stream()
                .filter(x -> x instanceof BayesianMethod)
                .forEach(x-> ((BayesianMethod) x).setPriors(priors));

    }
}
