package experiments.missing.discrete;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.io.DataStreamLoader;
import eu.amidst.extension.learn.parameter.InitializationTypeVBEM;
import eu.amidst.extension.learn.parameter.InitializationVBEM;
import eu.amidst.extension.learn.parameter.VBEMConfig;
import eu.amidst.extension.learn.parameter.penalizer.NoPenalizer;
import eu.amidst.extension.learn.structure.hillclimber.BayesianHcConfig;
import eu.amidst.extension.util.LogUtils;
import eu.amidst.extension.util.tuple.Tuple2;
import experiments.missing.MissingDiscreteExperiment;
import methods.missing.MissingDiscreteMethod;
import methods.missing.MissingVBSEM;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Exp_news_100 extends MissingDiscreteExperiment {

    public Exp_news_100(Set<MissingDiscreteMethod> methods) {
        super(methods);
    }

    public static void main(String[] args) throws Exception {
        long seed = 0;
        int nRuns = 5;

        boolean storeModels = false;

        LogUtils.LogLevel logLevel = LogUtils.LogLevel.INFO;
        int maxNumberOfParents = 3;
        Set<MissingDiscreteMethod> methods = new LinkedHashSet<>();

        /* Missing VBSEM configuration */
        InitializationVBEM initializationVBEM = new InitializationVBEM(InitializationTypeVBEM.PYRAMID, 64, 16, false);
        VBEMConfig vbemConfig = new VBEMConfig(seed, 0.01, 100, initializationVBEM, new NoPenalizer());
        BayesianHcConfig bayesianHcConfig = new BayesianHcConfig(seed, 0.01, 100);

        /* Methods */
        methods.add(new MissingVBSEM(vbemConfig, bayesianHcConfig, 100, maxNumberOfParents));

        /* Run experiment */
        Exp_news_100 exp = new Exp_news_100(methods);
        for(double missingPercentage = 0.1; missingPercentage < 0.6; missingPercentage += 0.1) {
            exp.runExperiment(missingPercentage, nRuns, storeModels, logLevel);
        }
    }

    @Override
    public void runExperiment(double missingPercentage,
                              int nRuns,
                              boolean storeModels,
                              LogUtils.LogLevel runLogLevel) throws Exception {

        System.out.println("------------------------------------------------------------------");
        System.out.println("------------------------------------------------------------------");
        System.out.println("---------------------------- NEWS_100 -----------------------------");
        System.out.println("------------------------------------------------------------------");
        System.out.println("------------------------------------------------------------------");

        String dataName = "news_100";
        String missingDataDirectory = "missing_data/discrete/" + dataName + "/0" + (int)(missingPercentage * 10) + "/";
        DataOnMemory<DataInstance> trueData = DataStreamLoader.loadDataOnMemoryFromFile("data/discrete/" + dataName + ".arff");

        List<DataOnMemory<DataInstance>> missingDataSets = new ArrayList<>(nRuns);
        for(int i = 1; i < nRuns + 1; i++){
            String missingDataName = dataName + "_0" + (int)(missingPercentage * 10) + "_" + i + ".arff";
            missingDataSets.add(DataStreamLoader.loadDataOnMemoryFromFile(missingDataDirectory + missingDataName));
        }

        /* Run methods */
        Tuple2<DataOnMemory<DataInstance>, List<DataOnMemory<DataInstance>>> data = new Tuple2<>(trueData, missingDataSets);
        for(MissingDiscreteMethod method: methods)
            method.runMissingDiscrete(data, dataName, missingPercentage, storeModels, runLogLevel);
    }
}
