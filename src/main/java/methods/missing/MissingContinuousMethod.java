package methods.missing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.extension.util.LogUtils;
import eu.amidst.extension.util.MyMath;
import eu.amidst.extension.util.tuple.Tuple2;
import experiments.util.missing.MissingContinuousResult;
import experiments.util.missing.MissingContinuousRun;

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

public interface MissingContinuousMethod {

    void runMissingContinuous(Tuple2<DataOnMemory<DataInstance>, List<DataOnMemory<DataInstance>>> runs,
                              String dataName,
                              double missingPercentage,
                              boolean storeModels,
                              LogUtils.LogLevel runLogLevel) throws Exception;

    default void showAverageContinuousResults(List<Tuple2<Double, Long>> scoresAndTime) {

        List<Double> meanSquaredErrors = scoresAndTime.stream().map(x-> x.getFirst()).collect(Collectors.toList());
        List<Double> learningTimes = scoresAndTime.stream().map(x-> (double) x.getSecond()).collect(Collectors.toList());

        double averageMSE = MyMath.mean(meanSquaredErrors);
        double averageTime = MyMath.mean(learningTimes) / 1000;

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
        otherSymbols.setDecimalSeparator('.');
        DecimalFormat f = new DecimalFormat("0.00", otherSymbols);
        System.out.println("----------------------------------------");
        System.out.println("----------------------------------------");
        System.out.println("Average learning Time: " + f.format(averageTime) + "s");
        System.out.println("Average NRMSE: " + f.format(averageMSE));
    }

    default void storeContinuousResults(List<Tuple2<Double, Long>> scoresAndTime,
                                        String directoryPath,
                                        String fileName) throws IOException {

        new File(directoryPath).mkdirs();

        /* Create Result objects */
        Map<String, MissingContinuousRun> runs = new LinkedHashMap<>(scoresAndTime.size());
        int i = 1;
        for(Tuple2<Double, Long> tuple: scoresAndTime) {
            MissingContinuousRun runResult = new MissingContinuousRun(tuple.getFirst(), tuple.getSecond());
            runs.put("run_" + i, runResult);
            i++;
        }

        /* Estimate average scores and time */
        List<Double> errors = scoresAndTime.stream().map(x-> x.getFirst()).collect(Collectors.toList());
        List<Double> learningTimes = scoresAndTime.stream().map(x-> (double) x.getSecond()).collect(Collectors.toList());

        double averageMSE = MyMath.mean(errors);
        double averageTime = MyMath.mean(learningTimes) / 1000;

        /* Create the ExperimentResult object */
        MissingContinuousResult experimentResult = new MissingContinuousResult(averageMSE, averageTime, runs);

        /* Write the Json file */
        String output = directoryPath + "/" + fileName;
        try (Writer writer = new FileWriter(output)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(experimentResult, writer);
        }
    }
}
