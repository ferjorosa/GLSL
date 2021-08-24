package experiments.missing;

import eu.amidst.extension.util.LogUtils;

public interface MissingExperiment {

    void runExperiment(double missingPercentage, int nRuns, boolean storeModels, LogUtils.LogLevel runLogLevel) throws Exception;
}
