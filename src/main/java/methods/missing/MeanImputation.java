package methods.missing;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.extension.missing.util.EvaluateMissingAmidst;
import eu.amidst.extension.missing.util.ImputeMissing;
import eu.amidst.extension.util.LogUtils;
import eu.amidst.extension.util.tuple.Tuple2;
import eu.amidst.extension.util.tuple.Tuple4;

import java.util.ArrayList;
import java.util.List;

public class MeanImputation implements MissingDiscreteMethod, MissingContinuousMethod, MissingMixedMethod {

    @Override
    public void runMissingDiscrete(Tuple2<DataOnMemory<DataInstance>, List<DataOnMemory<DataInstance>>> runs,
                                   String dataName,
                                   double missingPercentage,
                                   boolean storeModels,
                                   LogUtils.LogLevel runLogLevel) throws Exception {

        System.out.println("\n==========================");
        System.out.println("MEAN IMPUTATION");
        System.out.println("==========================\n");

        DataOnMemory<DataInstance> trueData = runs.getFirst();
        List<DataOnMemory<DataInstance>> missingDataSets = runs.getSecond();
        List<Tuple2<Double, Long>> scoresAndTimes = new ArrayList<>(missingDataSets.size());
        for(int i = 0; i < missingDataSets.size(); i++) {

            DataOnMemory<DataInstance> missingData = missingDataSets.get(i);

            long initTime = System.currentTimeMillis();
            DataOnMemory<DataInstance> imputedData = ImputeMissing.imputeWithExpectedValue(missingData);
            long endTime = System.currentTimeMillis();

            long learningTime = endTime - initTime;
            double accuracy = EvaluateMissingAmidst.accuracy(missingData, trueData, imputedData);
            scoresAndTimes.add(new Tuple2<>(accuracy, learningTime));

            LogUtils.info("----------------------------------------", runLogLevel);
            LogUtils.info("Run " + (i+1) , runLogLevel);
            LogUtils.info("Accuracy: " + accuracy, runLogLevel);
            LogUtils.info("Time: " + learningTime + " ms", runLogLevel);
        }

        /* Show average time and score */
        showAverageDiscreteResults(scoresAndTimes);

        String dataNameMissing = dataName + "_0" + (int)(missingPercentage * 10);

        /* Store experiment results in a JSON file */
        storeDiscreteResults(scoresAndTimes, "missing_results/discrete/"+ dataName+"/",
                dataNameMissing + "_results_MEAN.json");
    }

    @Override
    public void runMissingContinuous(Tuple2<DataOnMemory<DataInstance>, List<DataOnMemory<DataInstance>>> runs,
                                     String dataName,
                                     double missingPercentage,
                                     boolean storeModels,
                                     LogUtils.LogLevel runLogLevel) throws Exception {

        System.out.println("\n==========================");
        System.out.println("MEAN IMPUTATION");
        System.out.println("==========================\n");

        DataOnMemory<DataInstance> trueData = runs.getFirst();
        List<DataOnMemory<DataInstance>> missingDataSets = runs.getSecond();
        List<Tuple2<Double, Long>> scoresAndTimes = new ArrayList<>(missingDataSets.size());
        for(int i = 0; i < missingDataSets.size(); i++) {

            DataOnMemory<DataInstance> missingData = missingDataSets.get(i);

            long initTime = System.currentTimeMillis();
            DataOnMemory<DataInstance> imputedData = ImputeMissing.imputeWithExpectedValue(missingData);
            long endTime = System.currentTimeMillis();

            long learningTime = endTime - initTime;
            double nrmse = EvaluateMissingAmidst.nrmse(missingData, trueData, imputedData);
            scoresAndTimes.add(new Tuple2<>(nrmse, learningTime));

            LogUtils.info("----------------------------------------", runLogLevel);
            LogUtils.info("Run " + (i+1) , runLogLevel);
            LogUtils.info("NRMSE: " + nrmse, runLogLevel);
            LogUtils.info("Time: " + learningTime + " ms", runLogLevel);
        }

        /* Show average time and score */
        showAverageContinuousResults(scoresAndTimes);

        String dataNameMissing = dataName + "_0" + (int)(missingPercentage * 10);

        /* Store experiment results in a JSON file */
        storeContinuousResults(scoresAndTimes, "missing_results/continuous/"+ dataName+"/",
                dataNameMissing + "_results_MEAN.json");
    }

    @Override
    public void runMissingMixed(Tuple2<DataOnMemory<DataInstance>, List<DataOnMemory<DataInstance>>> runs,
                                String dataName,
                                double missingPercentage,
                                boolean storeModels,
                                LogUtils.LogLevel runLogLevel) throws Exception {

        System.out.println("\n==========================");
        System.out.println("MEAN IMPUTATION");
        System.out.println("==========================\n");

        DataOnMemory<DataInstance> trueData = runs.getFirst();
        List<DataOnMemory<DataInstance>> missingDataSets = runs.getSecond();
        List<Tuple4<Double, Double, Double, Long>> scoresAndTimes = new ArrayList<>(missingDataSets.size());
        for(int i = 0; i < missingDataSets.size(); i++) {

            DataOnMemory<DataInstance> missingData = missingDataSets.get(i);

            long initTime = System.currentTimeMillis();
            DataOnMemory<DataInstance> imputedData = ImputeMissing.imputeWithExpectedValue(missingData);
            long endTime = System.currentTimeMillis();

            long learningTime = endTime - initTime;
            double accuracy = EvaluateMissingAmidst.accuracy(missingData, trueData, imputedData);
            double nrmse = EvaluateMissingAmidst.nrmse(missingData, trueData, imputedData);
            double averageError = EvaluateMissingAmidst.avgError(missingData, trueData, imputedData);
            scoresAndTimes.add(new Tuple4<>(accuracy, nrmse, averageError, learningTime));

            LogUtils.info("----------------------------------------", runLogLevel);
            LogUtils.info("Run " + (i+1) , runLogLevel);
            LogUtils.info("Accuracy: " + accuracy, runLogLevel);
            LogUtils.info("NRMSE: " + nrmse, runLogLevel);
            LogUtils.info("AvgError: " + averageError, runLogLevel);
            LogUtils.info("Time: " + learningTime + " ms", runLogLevel);
        }

        /* Show average time and score */
        showAverageMixedResults(scoresAndTimes);

        String dataNameMissing = dataName + "_0" + (int)(missingPercentage * 10);

        /* Store experiment results in a JSON file */
        storeMixedResults(scoresAndTimes, "missing_results/mixed/"+ dataName+"/",
                dataNameMissing + "_results_MEAN.json");

    }
}
