package methods.missing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.extension.util.LogUtils;
import eu.amidst.extension.util.MyMath;
import eu.amidst.extension.util.tuple.Tuple2;
import experiments.util.missing.MissingDiscreteResult;
import experiments.util.missing.MissingDiscreteRun;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public interface MissingDiscreteMethod extends MissingMethod {

    void runMissingDiscrete(Tuple2<DataOnMemory<DataInstance>, List<DataOnMemory<DataInstance>>> runs,
                            String dataName,
                            double missingPercentage,
                            boolean storeModels,
                            LogUtils.LogLevel runLogLevel) throws Exception;

    default void showAverageDiscreteResults(List<Tuple2<Double, Long>> scoresAndTime) {

        List<Double> accuracies = scoresAndTime.stream().map(x-> x.getFirst()).collect(Collectors.toList());
        List<Double> learningTimes = scoresAndTime.stream().map(x-> (double) x.getSecond()).collect(Collectors.toList());

        double averageAccuracy = MyMath.mean(accuracies);
        double averageTime = MyMath.mean(learningTimes) / 1000;

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
        otherSymbols.setDecimalSeparator('.');
        DecimalFormat f = new DecimalFormat("0.00", otherSymbols);
        System.out.println("----------------------------------------");
        System.out.println("----------------------------------------");
        System.out.println("Average learning Time: " + f.format(averageTime) + "s");
        System.out.println("Average Accuracy: " + f.format(averageAccuracy));
    }

    default void storeDiscreteResults(List<Tuple2<Double, Long>> scoresAndTime,
                                      String directoryPath,
                                      String fileName) throws IOException {

        new File(directoryPath).mkdirs();

        /* Create Result objects */
        Map<String, MissingDiscreteRun> runs = new LinkedHashMap<>(scoresAndTime.size());
        int i = 1;
        for(Tuple2<Double, Long> tuple: scoresAndTime) {
            MissingDiscreteRun runResult = new MissingDiscreteRun(tuple.getFirst(), tuple.getSecond());
            runs.put("run_" + i, runResult);
            i++;
        }

        /* Estimate average scores and time */
        List<Double> accuracies = scoresAndTime.stream().map(x-> x.getFirst()).collect(Collectors.toList());
        List<Double> learningTimes = scoresAndTime.stream().map(x-> (double) x.getSecond()).collect(Collectors.toList());

        double averageAccuracy = MyMath.mean(accuracies);
        double averageTime = MyMath.mean(learningTimes) / 1000;

        /* Create the ExperimentResult object */
        MissingDiscreteResult experimentResult = new MissingDiscreteResult(averageAccuracy, averageTime, runs);

        /* Write the Json file */
        String output = directoryPath + "/" + fileName;
        try (Writer writer = new FileWriter(output)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(experimentResult, writer);
        }
    }
}
