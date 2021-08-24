from experiments.discrete import Exp_car_evaluation, Exp_hayes_roth, Exp_spect_heart, Exp_alarm, Exp_webkb_336, \
    Exp_news_100, Exp_coil_42, Exp_solar_flare, Exp_nursery, Exp_vote, Exp_web_phishing, Exp_breast_cancer, \
    Exp_hiv_test, Exp_zoo, Exp_balance_scale


def main():
    run = 1
    n_folds = 10
    fold_log = True
    kde_bw = "normal_reference"

    exp = Exp_hiv_test.Exp_hiv_test("hiv_test")
    exp.run(run, n_folds, fold_log, kde_bw)

    exp = Exp_hayes_roth.Exp_hayes_roth("hayes_roth")
    exp.run(run, n_folds, fold_log, kde_bw)

    exp = Exp_balance_scale.Exp_balance_scale("balance_scale")
    exp.run(run, n_folds, fold_log, kde_bw)

    exp = Exp_car_evaluation.Exp_car_evaluation("car_evaluation")
    exp.run(run, n_folds, fold_log, kde_bw)

    exp = Exp_nursery.Exp_nursery("nursery")
    exp.run(run, n_folds, fold_log, kde_bw)

    exp = Exp_breast_cancer.Exp_breast_cancer("breast_cancer")
    exp.run(run, n_folds, fold_log, kde_bw)

    exp = Exp_web_phishing.Exp_web_phishing("web_phishing")
    exp.run(run, n_folds, fold_log, kde_bw)

    exp = Exp_solar_flare.Exp_solar_flare("solar_flare")
    exp.run(run, n_folds, fold_log, kde_bw)

    exp = Exp_zoo.Exp_zoo("zoo")
    exp.run(run, n_folds, fold_log, kde_bw)

    exp = Exp_vote.Exp_vote("vote")
    exp.run(run, n_folds, fold_log, kde_bw)

    exp = Exp_spect_heart.Exp_spect_heart("spect_heart")
    exp.run(run, n_folds, fold_log, kde_bw)

    exp = Exp_alarm.Exp_alarm("alarm")
    exp.run(run, n_folds, fold_log, kde_bw)

    exp = Exp_coil_42.Exp_coil_42("coil_42")
    exp.run(run, n_folds, fold_log, kde_bw)

    exp = Exp_news_100.Exp_news_100("news_100")
    exp.run(run, n_folds, fold_log, kde_bw)

    exp = Exp_webkb_336.Exp_webkb_336("webkb_336")
    exp.run(run, n_folds, fold_log, kde_bw)


if __name__ == "__main__":
    main()