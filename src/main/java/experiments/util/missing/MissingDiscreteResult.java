package experiments.util.missing;

import java.util.Map;

public class MissingDiscreteResult {

    private double average_accuracy;
    private double average_learning_time;
    private Map<String, MissingDiscreteRun> runs;

    public MissingDiscreteResult(double average_accuracy,
                                 double average_learning_time,
                                 Map<String, MissingDiscreteRun> runs) {
        this.average_accuracy = average_accuracy;
        this.average_learning_time = average_learning_time;
        this.runs = runs;
    }
}
