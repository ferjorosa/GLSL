package experiments.util;

import eu.amidst.core.datastream.DataInstance;
import eu.amidst.core.datastream.DataOnMemory;
import eu.amidst.core.datastream.filereaders.arffFileReader.ARFFDataWriter;
import eu.amidst.core.io.DataStreamLoader;
import eu.amidst.extension.missing.util.GenerateMissing;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GenerateDataMissing {

    public static void main(String[] args) throws Exception {

        double maxMissingPercentage = 0.5;
        int k = 5; // number of missing files for each percentage value

        String[] dataTypes = {"mixed"};
        String inputBasePath = "data";
        String outputBasePath = "missing_data";

        for(String dataType: dataTypes) {

            String inputDirectory = inputBasePath + "/" + dataType;
            String outputDirectory = outputBasePath + "/" + dataType;

            File f_directory = new File(inputDirectory);
            String[] fileNames = f_directory.list(new FilenameFilter() {
                @Override
                public boolean accept(File f, String name) {
                    return name.endsWith(".arff");
                }
            });

            for (String fileName : fileNames) {
                String dataName = fileName.split("\\.")[0];
                System.out.println("\n" + dataName);
                String filePath = inputDirectory + "/" + fileName;

                for (double missingPercentage = 0.1; missingPercentage <= maxMissingPercentage; missingPercentage += 0.1) {
                    String missingPercentageString = "0" + (int) (missingPercentage * 10);
                    long seed = 0;
                    String missingFilePath = outputDirectory + "/" + dataName + "/" + missingPercentageString + "/";
                    DataOnMemory<DataInstance> data = DataStreamLoader.open(filePath).toDataOnMemory();
                    generateAndExportWithMissing(data, k, missingPercentage, seed, dataName, missingFilePath);
                }
            }
        }
    }

    public static List<DataOnMemory<DataInstance>> generateAndExportWithMissing(DataOnMemory<DataInstance> data,
                                                                                int k,
                                                                                double missingPercentage,
                                                                                long seed,
                                                                                String dataName,
                                                                                String path) throws IOException {

        /* Generate a percentage of missing values in the train folds */
        Random random = new Random(seed);
        List<DataOnMemory<DataInstance>> datasetsWithMissing = new ArrayList<>();
        for(int i = 0; i < k ; i++) {
            long seed_for_missing = random.nextInt(Integer.MAX_VALUE);
            DataOnMemory<DataInstance> dataWithMissing = GenerateMissing.randomlyHideValues(data, missingPercentage, seed_for_missing);
            datasetsWithMissing.add(dataWithMissing);
        }

        exportWithMissing(datasetsWithMissing, path, dataName, missingPercentage);

        return datasetsWithMissing;
    }

    private static void exportWithMissing(List<DataOnMemory<DataInstance>> datasets,
                                          String path,
                                          String dataName,
                                          double missingPercentage) throws IOException {

        /* Create directory if it doesnt exist */
        new File(path).mkdirs();

        String dataNameMissing = dataName + "_0" + (int)(missingPercentage * 10);

        for(int i = 0; i < datasets.size(); i++)
            ARFFDataWriter.writeToARFFFile(datasets.get(i), path + dataNameMissing + "_" + (i+1) + ".arff");
    }
}
