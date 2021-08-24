package experiments.util.missing;

import java.util.Map;

public class MissingContinuousResult {

    private double average_nrmse;
    private double average_learning_time;
    private Map<String, MissingContinuousRun> runs;

    public MissingContinuousResult(double average_nrmse,
                                   double average_learning_time,
                                   Map<String, MissingContinuousRun> runs) {
        this.average_nrmse = average_nrmse;
        this.average_learning_time = average_learning_time;
        this.runs = runs;
    }
}
