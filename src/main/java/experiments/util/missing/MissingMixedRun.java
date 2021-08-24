package experiments.util.missing;

public class MissingMixedRun {

    private double accuracy;
    private double nrmse;
    private double average_error;
    private long learning_time;

    public MissingMixedRun(double accuracy, double nrmse, double average_error, long learning_time) {
        this.accuracy = accuracy;
        this.nrmse = nrmse;
        this.learning_time = learning_time;
        this.average_error = average_error;
    }
}
