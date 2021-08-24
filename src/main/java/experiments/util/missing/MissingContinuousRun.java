package experiments.util.missing;

public class MissingContinuousRun {

    private double nrmse;
    private long learning_time;

    public MissingContinuousRun(double nrmse, long learning_time) {
        this.nrmse = nrmse;
        this.learning_time = learning_time;
    }
}
