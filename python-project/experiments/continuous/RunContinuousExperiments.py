from experiments.continuous import Exp_real_state_valuation
from experiments.continuous import Exp_buddymove
from experiments.continuous import Exp_qsar_fish_toxicity
from experiments.continuous import Exp_qsar_aqua_toxicity
from experiments.continuous import Exp_ilpd
from experiments.continuous import Exp_alcohol
from experiments.continuous import Exp_travel_reviews
from experiments.continuous import Exp_wine_quality_white
from experiments.continuous import Exp_wine
from experiments.continuous import Exp_leaf
from experiments.continuous import Exp_nba
from experiments.continuous import Exp_wdbc
from experiments.continuous import Exp_waveform
from experiments.continuous import Exp_100_plants
from experiments.continuous import Exp_geo_music


def main():
    run = 1
    n_folds = 10
    fold_log = True
    kde_bw = "normal_reference"

    exp = Exp_real_state_valuation.Exp_real_state_valuation("real_state_valuation")
    exp.run(run, n_folds, fold_log, kde_bw)

    exp = Exp_buddymove.Exp_buddymove("buddymove")
    exp.run(run, n_folds, fold_log, kde_bw)

    exp = Exp_qsar_fish_toxicity.Exp_qsar_fish_toxicity("qsar_fish_toxicity")
    exp.run(run, n_folds, fold_log, kde_bw)

    exp = Exp_qsar_aqua_toxicity.Exp_qsar_aqua_toxicity("qsar_aqua_toxicity")
    exp.run(run, n_folds, fold_log, kde_bw)

    exp = Exp_ilpd.Exp_ilpd("ilpd")
    exp.run(run, n_folds, fold_log, kde_bw)

    exp = Exp_alcohol.Exp_alcohol("alcohol")
    exp.run(run, n_folds, fold_log, kde_bw)

    exp = Exp_travel_reviews.Exp_travel_reviews("travel_reviews")
    exp.run(run, n_folds, fold_log, kde_bw)

    exp = Exp_wine_quality_white.Exp_wine_quality_white("wine_quality_white")
    exp.run(run, n_folds, fold_log, kde_bw)

    exp = Exp_wine.Exp_wine("wine")
    exp.run(run, n_folds, fold_log, kde_bw)

    exp = Exp_leaf.Exp_leaf("leaf")
    exp.run(run, n_folds, fold_log, kde_bw)

    exp = Exp_nba.Exp_nba("nba")
    exp.run(run, n_folds, fold_log, kde_bw)

    exp = Exp_wdbc.Exp_wdbc("wdbc")
    exp.run(run, n_folds, fold_log, kde_bw)

    exp = Exp_waveform.Exp_waveform("waveform")
    exp.run(run, n_folds, fold_log, kde_bw)

    exp = Exp_100_plants.Exp_100_plants("100_plants")
    exp.run(run, n_folds, fold_log, kde_bw)

    exp = Exp_geo_music.Exp_geo_music("geo_music")
    exp.run(run, n_folds, fold_log, kde_bw)


if __name__ == "__main__":
    main()