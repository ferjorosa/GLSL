package experiments.util.missing;

import java.util.Map;

public class MissingMixedResult {

    private double average_accuracy;
    private double average_nrmse;
    private double average_error;
    private double average_learning_time;
    private Map<String, MissingMixedRun> runs;

    public MissingMixedResult(double average_accuracy,
                              double average_nrmse,
                              double average_error,
                              double average_learning_time,
                              Map<String, MissingMixedRun> runs) {
        this.average_accuracy = average_accuracy;
        this.average_nrmse = average_nrmse;
        this.average_error = average_error;
        this.average_learning_time = average_learning_time;
        this.runs = runs;
    }
}
