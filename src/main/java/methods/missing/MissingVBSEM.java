package methods.missing;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.models.BayesianNetwork;
import eu.amidst.core.models.DAG;
import eu.amidst.core.variables.Variables;
import eu.amidst.extension.learn.parameter.VBEM;
import eu.amidst.extension.learn.parameter.VBEMConfig;
import eu.amidst.extension.learn.structure.hillclimber.BayesianHcConfig;
import eu.amidst.extension.learn.structure.hillclimber.operator.BayesianHcAddArc;
import eu.amidst.extension.learn.structure.hillclimber.operator.BayesianHcOperator;
import eu.amidst.extension.learn.structure.hillclimber.operator.BayesianHcRemoveArc;
import eu.amidst.extension.learn.structure.hillclimber.operator.BayesianHcReverseArc;
import eu.amidst.extension.missing.VBSEM_Complete;
import eu.amidst.extension.missing.util.EvaluateMissingAmidst;
import eu.amidst.extension.missing.util.ImputeMissing;
import eu.amidst.extension.util.LogUtils;
import eu.amidst.extension.util.tuple.Tuple2;
import eu.amidst.extension.util.tuple.Tuple3;
import eu.amidst.extension.util.tuple.Tuple4;
import eu.amidst.extension.util.tuple.Tuple5;

import java.util.*;

public class MissingVBSEM implements MissingDiscreteMethod, MissingContinuousMethod, MissingMixedMethod {

    private VBEMConfig vbemConfig;

    private BayesianHcConfig bayesianHcConfig;

    private int maxSemIterations;

    private int maxParentsHc;

    public MissingVBSEM(VBEMConfig vbemConfig,
                        BayesianHcConfig bayesianHcConfig,
                        int maxSemIterations,
                        int maxParentsHc) {
        this.vbemConfig = vbemConfig;
        this.bayesianHcConfig = bayesianHcConfig;
        this.maxSemIterations = maxSemIterations;
        this.maxParentsHc = maxParentsHc;
    }

    @Override
    public void runMissingDiscrete(Tuple2<DataOnMemory<DataInstance>, List<DataOnMemory<DataInstance>>> runs,
                                   String dataName,
                                   double missingPercentage,
                                   boolean storeModels,
                                   LogUtils.LogLevel runLogLevel) throws Exception {

        System.out.println("\n==========================");
        System.out.println("VBSEM");
        System.out.println("==========================\n");

        VBSEM_Complete vbsem = new VBSEM_Complete(vbemConfig, bayesianHcConfig, maxSemIterations);
        VBEM vbem = new VBEM(vbemConfig);

        List<Tuple3<BayesianNetwork, Double, Long>> results = new ArrayList<>();

        /* Iterate through the runs and learn using VBSEM on each one */
        DataOnMemory<DataInstance> trueData = runs.getFirst();
        List<DataOnMemory<DataInstance>> missingDataSets = runs.getSecond();
        for(int i = 0; i < missingDataSets.size(); i++) {

            DataOnMemory<DataInstance> missingData = missingDataSets.get(i);

            /* Apply VBSEM to the initial BN composed of independent variables */
            long initTime = System.currentTimeMillis();
            Variables variables = new Variables(missingData.getAttributes());
            DAG initialDAG = new DAG(variables);
            double initialScore = vbem.learnModelWithPriorUpdate(missingData, initialDAG);
            BayesianNetwork initialBn = vbem.getLearntBayesianNetwork();

            BayesianHcAddArc bayesianHcAddArc = new BayesianHcAddArc(bayesianHcConfig, variables, maxParentsHc);
            BayesianHcRemoveArc bayesianHcRemoveArc = new BayesianHcRemoveArc(bayesianHcConfig, new HashMap<>());
            BayesianHcReverseArc bayesianHcReverseArc = new BayesianHcReverseArc(bayesianHcConfig, variables, maxParentsHc);
            Set<BayesianHcOperator> bayesianHcOperators = new LinkedHashSet<>(3);
            bayesianHcOperators.add(bayesianHcAddArc);
            bayesianHcOperators.add(bayesianHcRemoveArc);
            bayesianHcOperators.add(bayesianHcReverseArc);

            Tuple2<BayesianNetwork, Double> result = vbsem.learnModel(initialBn, initialScore, missingData, bayesianHcOperators, LogUtils.LogLevel.INFO);
            long endTime = System.currentTimeMillis();

            /* Store and log the run result */
            BayesianNetwork posteriorPredictive = result.getFirst();
            DataOnMemory<DataInstance> imputedData = ImputeMissing.imputeWithModel(missingData, posteriorPredictive);
            long learningTime = endTime - initTime;
            double accuracy = EvaluateMissingAmidst.accuracy(missingData, trueData, imputedData);
            results.add(new Tuple3<>(posteriorPredictive, accuracy, learningTime));

            LogUtils.info("----------------------------------------", runLogLevel);
            LogUtils.info("Run " + (i+1) , runLogLevel);
            LogUtils.info("Accuracy: " + accuracy, runLogLevel);
            LogUtils.info("Time: " + learningTime + " ms", runLogLevel);
        }

        List<Tuple2<Double, Long>> scoresAndTimes = new ArrayList<>(missingDataSets.size());
        List<BayesianNetwork> models = new ArrayList<>(missingDataSets.size());
        for(Tuple3<BayesianNetwork, Double, Long> result: results) {
            models.add(result.getFirst());
            scoresAndTimes.add(new Tuple2<>(result.getSecond(), result.getThird()));
        }

        /* Show average time and score */
        showAverageDiscreteResults(scoresAndTimes);

        String dataNameMissing = dataName + "_0" + (int)(missingPercentage * 10);

        /* Store models */
        if(storeModels)
            storeAmidstModels(models, "missing_results/discrete/"+ dataName + "/", dataNameMissing, "VBSEM");

        /* Store experiment results in a JSON file */
        storeDiscreteResults(scoresAndTimes, "missing_results/discrete/"+ dataName+"/",
                dataNameMissing + "_results_VBSEM.json");
    }

    @Override
    public void runMissingContinuous(Tuple2<DataOnMemory<DataInstance>, List<DataOnMemory<DataInstance>>> runs,
                                     String dataName,
                                     double missingPercentage,
                                     boolean storeModels,
                                     LogUtils.LogLevel runLogLevel) throws Exception {

        System.out.println("\n==========================");
        System.out.println("VBSEM");
        System.out.println("==========================\n");

        VBSEM_Complete vbsem = new VBSEM_Complete(vbemConfig, bayesianHcConfig, maxSemIterations);
        VBEM vbem = new VBEM(vbemConfig);

        List<Tuple3<BayesianNetwork, Double, Long>> results = new ArrayList<>();

        /* Iterate through the runs and learn using VBSEM on each one */
        DataOnMemory<DataInstance> trueData = runs.getFirst();
        List<DataOnMemory<DataInstance>> missingDataSets = runs.getSecond();
        for(int i = 0; i < missingDataSets.size(); i++) {

            DataOnMemory<DataInstance> missingData = missingDataSets.get(i);

            /* Apply VBSEM to the initial BN composed of independent variables */
            long initTime = System.currentTimeMillis();
            Variables variables = new Variables(missingData.getAttributes());
            DAG initialDAG = new DAG(variables);
            double initialScore = vbem.learnModelWithPriorUpdate(missingData, initialDAG);
            BayesianNetwork initialBn = vbem.getLearntBayesianNetwork();

            BayesianHcAddArc bayesianHcAddArc = new BayesianHcAddArc(bayesianHcConfig, variables, maxParentsHc);
            BayesianHcRemoveArc bayesianHcRemoveArc = new BayesianHcRemoveArc(bayesianHcConfig, new HashMap<>());
            BayesianHcReverseArc bayesianHcReverseArc = new BayesianHcReverseArc(bayesianHcConfig, variables, maxParentsHc);
            Set<BayesianHcOperator> bayesianHcOperators = new LinkedHashSet<>(3);
            bayesianHcOperators.add(bayesianHcAddArc);
            bayesianHcOperators.add(bayesianHcRemoveArc);
            bayesianHcOperators.add(bayesianHcReverseArc);

            Tuple2<BayesianNetwork, Double> result = vbsem.learnModel(initialBn, initialScore, missingData, bayesianHcOperators, LogUtils.LogLevel.INFO);
            long endTime = System.currentTimeMillis();

            /* Store and log the run result */
            BayesianNetwork posteriorPredictive = result.getFirst();
            DataOnMemory<DataInstance> imputedData = ImputeMissing.imputeWithModel(missingData, posteriorPredictive);
            long learningTime = endTime - initTime;
            double nrmse = EvaluateMissingAmidst.nrmse(missingData, trueData, imputedData);
            results.add(new Tuple3<>(posteriorPredictive, nrmse, learningTime));

            LogUtils.info("----------------------------------------", runLogLevel);
            LogUtils.info("Run " + (i+1) , runLogLevel);
            LogUtils.info("NRMSE: " + nrmse, runLogLevel);
            LogUtils.info("Time: " + learningTime + " ms", runLogLevel);
        }

        List<Tuple2<Double, Long>> scoresAndTimes = new ArrayList<>(missingDataSets.size());
        List<BayesianNetwork> models = new ArrayList<>(missingDataSets.size());
        for(Tuple3<BayesianNetwork, Double, Long> result: results) {
            models.add(result.getFirst());
            scoresAndTimes.add(new Tuple2<>(result.getSecond(), result.getThird()));
        }

        /* Show average time and score */
        showAverageContinuousResults(scoresAndTimes);

        String dataNameMissing = dataName + "_0" + (int)(missingPercentage * 10);

        /* Store models */
        if(storeModels)
            storeAmidstModels(models, "missing_results/continuous/"+ dataName + "/", dataNameMissing, "VBSEM");

        /* Store experiment results in a JSON file */
        storeContinuousResults(scoresAndTimes, "missing_results/continuous/"+ dataName+"/",
                dataNameMissing + "_results_VBSEM.json");
    }

    @Override
    public void runMissingMixed(Tuple2<DataOnMemory<DataInstance>, List<DataOnMemory<DataInstance>>> runs,
                                String dataName,
                                double missingPercentage,
                                boolean storeModels,
                                LogUtils.LogLevel runLogLevel) throws Exception {

        System.out.println("\n==========================");
        System.out.println("VBSEM");
        System.out.println("==========================\n");

        VBSEM_Complete vbsem = new VBSEM_Complete(vbemConfig, bayesianHcConfig, maxSemIterations);
        VBEM vbem = new VBEM(vbemConfig);

        List<Tuple5<BayesianNetwork, Double, Double, Double, Long>> results = new ArrayList<>();

        /* Iterate through the runs and learn using VBSEM on each one */
        DataOnMemory<DataInstance> trueData = runs.getFirst();
        List<DataOnMemory<DataInstance>> missingDataSets = runs.getSecond();
        for(int i = 0; i < missingDataSets.size(); i++) {

            DataOnMemory<DataInstance> missingData = missingDataSets.get(i);

            /* Apply VBSEM to the initial BN composed of independent variables */
            long initTime = System.currentTimeMillis();
            Variables variables = new Variables(missingData.getAttributes());
            DAG initialDAG = new DAG(variables);
            double initialScore = vbem.learnModelWithPriorUpdate(missingData, initialDAG);
            BayesianNetwork initialBn = vbem.getLearntBayesianNetwork();

            BayesianHcAddArc bayesianHcAddArc = new BayesianHcAddArc(bayesianHcConfig, variables, maxParentsHc);
            BayesianHcRemoveArc bayesianHcRemoveArc = new BayesianHcRemoveArc(bayesianHcConfig, new HashMap<>());
            BayesianHcReverseArc bayesianHcReverseArc = new BayesianHcReverseArc(bayesianHcConfig, variables, maxParentsHc);
            Set<BayesianHcOperator> bayesianHcOperators = new LinkedHashSet<>(3);
            bayesianHcOperators.add(bayesianHcAddArc);
            bayesianHcOperators.add(bayesianHcRemoveArc);
            bayesianHcOperators.add(bayesianHcReverseArc);

            Tuple2<BayesianNetwork, Double> result = vbsem.learnModel(initialBn, initialScore, missingData, bayesianHcOperators, LogUtils.LogLevel.INFO);
            long endTime = System.currentTimeMillis();

            /* Store and log the run result */
            BayesianNetwork posteriorPredictive = result.getFirst();
            DataOnMemory<DataInstance> imputedData = ImputeMissing.imputeWithModel(missingData, posteriorPredictive);
            long learningTime = endTime - initTime;
            double accuracy = EvaluateMissingAmidst.accuracy(missingData, trueData, imputedData);
            double nrmse = EvaluateMissingAmidst.nrmse(missingData, trueData, imputedData);
            double avgError = EvaluateMissingAmidst.avgError(missingData, trueData, imputedData);
            results.add(new Tuple5<>(posteriorPredictive, accuracy, nrmse, avgError, learningTime));

            LogUtils.info("----------------------------------------", runLogLevel);
            LogUtils.info("Run " + (i+1) , runLogLevel);
            LogUtils.info("AvgError: " + avgError, runLogLevel);
            LogUtils.info("Accuracy: " + accuracy, runLogLevel);
            LogUtils.info("NRMSE: " + nrmse, runLogLevel);
            LogUtils.info("Time: " + learningTime + " ms", runLogLevel);
        }

        List<Tuple4<Double, Double, Double, Long>> scoresAndTimes = new ArrayList<>(missingDataSets.size());
        List<BayesianNetwork> models = new ArrayList<>(missingDataSets.size());
        for(Tuple5<BayesianNetwork, Double, Double, Double, Long> result: results) {
            models.add(result.getFirst());
            scoresAndTimes.add(new Tuple4<>(result.getSecond(), result.getThird(), result.getFourth(), result.getFifth()));
        }

        /* Show average time and score */
        showAverageMixedResults(scoresAndTimes);

        String dataNameMissing = dataName + "_0" + (int)(missingPercentage * 10);

        /* Store models */
        if(storeModels)
            storeAmidstModels(models, "missing_results/mixed/"+ dataName + "/", dataNameMissing, "VBSEM");

        /* Store experiment results in a JSON file */
        storeMixedResults(scoresAndTimes, "missing_results/mixed/"+ dataName+"/",
                dataNameMissing + "_results_VBSEM.json");
    }
}
