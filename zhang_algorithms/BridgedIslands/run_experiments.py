import subprocess
from timeit import default_timer as timer
import os
import shutil
import json

# This script executes the original BI code for a number of datasets. It has been designed to do cross-validation,
# because it wasnt specifically designed to do so in the original.
#
# This script has been specifically designed for the article. Changes in the datasets or in the number of folds could be
# made by simply changing a couple of lines.
#
# IMPORTANT: If you want to execute the experiments multiple times, assign the "run" variable a value that doesn't
# overwrite current runs. Be careful to assign the same number to all the methods or you will have multiple results
# on different "run_X" directories
run = 1

if not os.path.exists("temp"):
    os.makedirs("temp")

# Datasets that are going to be used in the comparative experiment
# datasets = [ "alarm", "asia", "balance_scale", "breast_cancer", "breast_w", "cancer", "car_evaluation",
#                 "child", "coil_42", "coleman", "earthquake", "hannover", "hayes_roth", "hiv_test", "house_building", "insurance",
#                 "mildew", "monks_1", "monks_2", "monks_3", "mushroom", "nursery", "pascal_voc_2007", "sachs", "solar_flare",
#                 "somerville", "spect_heart", "survey", "vote", "water", "web_phishing", "zoo"]
datasets = ["hannover", "solar_flare", "somerville", "web_phishing", "zoo"]

# Number of folds for the cross-validation
# (this will NOT generate the folds, that has been done with experiments.util.KFold.java, this will only used the
# appropriate files in case you have tried multiple number of folds)
numberOfFolds = 10

results = {}
for data in datasets:
    print("\n--------------------------------------------")
    print(data)
    print("--------------------------------------------")

    if not os.path.exists('../../latent_results/' + 'run_' + str(run) + '/discrete/' + data + '/' + str(numberOfFolds)
                          + '_folds/BI'):
        os.makedirs('../../latent_results/' + 'run_' + str(run) + '/discrete/' + data + '/' + str(numberOfFolds)
                    + '_folds/BI')

    data_results = {}
    folds = {}
    avg_learning_time = 0
    avg_test_ll = 0
    avg_train_bic = 0

    for i in range(1, 11):
        command = ['java', '-Xmx1024M', '-cp', 'BI.jar', 'clustering/LearnAndTest',
                   '../../latent_data/discrete/'+data+'/'+str(numberOfFolds)+'_folds/'+data+'_'+str(i)+'_train.arff',
                   '../../latent_data/discrete/'+data+'/'+str(numberOfFolds)+'_folds/'+data+'_'+str(i)+'_test.arff',
                   './temp',
                   "32", "32", "32", "32", "64", "100", "0.01", "3"]

        print("\nFold " + str(i) + ":")

        # Learn the model
        start = timer()
        p = subprocess.Popen(command, stdout=subprocess.PIPE)
        result = p.communicate()
        p.wait()
        end = timer()
        learning_time = (end - start) * 1000

        # Rename the learned file to BIF format for evaluation
        if os.path.isfile('temp/'+data+'_'+str(i)+'.bif'):
            os.remove('temp/'+data+'_'+str(i)+'.bif')
            os.rename("temp/FinalBIModel.bif", 'temp/'+data+'_'+str(i)+'.bif')
        else:
            os.rename("temp/FinalBIModel.bif", 'temp/'+data+'_'+str(i)+'.bif')

        # Results have been posted by BI.jar in the 'Result_LearnAndTest.txt' file,
        # we now load them and post them in the results.json file
        test_ll = 0
        train_bic = 0
        with open("temp/Result_LearnAndTest.txt", "r") as file:
            for line in file:
                if line.startswith("Loglikelihood (base e) on the testing data:"):
                    test_ll = float(line.replace("Loglikelihood (base e) on the testing data:", ""))
                elif line.startswith("BIC Score on the training data:"):
                    train_bic = float(line.replace("BIC Score on the training data:", ""))

        fold_results = {}
        print("Learning time (ms) = " + str(learning_time))
        fold_results["learning_time"] = learning_time
        print("Test LL = " + str(test_ll))
        fold_results["test_LL"] = test_ll
        print("Train BIC = " + str(train_bic))
        fold_results["train_BIC"] = train_bic

        #  Move the result model
        if os.path.isfile('../../latent_results/' + 'run_' + str(run) + '/discrete/' + data + '/' + str(numberOfFolds)
                          + '_folds/BI/' + data + '_' + str(i) + '_BI.bif'):
            os.remove('../../latent_results/' + 'run_' + str(run) + '/discrete/' + data + '/' + str(numberOfFolds)
                      + '_folds/BI/' + data + '_' + str(i) + '_BI.bif')
            shutil.move('temp/' + data + '_' + str(i) + '.bif', '../../latent_results/' + 'run_' + str(run) + '/discrete/' + data + '/'
                        + str(numberOfFolds) + '_folds/BI/' + data + '_' + str(i) + '_BI.bif')
        else:
            shutil.move('temp/' + data + '_' + str(i) + '.bif', '../../latent_results/' + 'run_' + str(run) + '/discrete/' + data + '/'
                        + str(numberOfFolds) + '_folds/BI/' + data + '_' + str(i) + '_BI.bif')

        folds["fold_" + str(i)] = fold_results
        avg_learning_time = avg_learning_time + learning_time
        avg_test_ll = avg_test_ll + test_ll
        avg_train_bic = avg_train_bic + train_bic

    # Generate the average results and store them in the dictionary
    avg_train_bic = avg_train_bic / numberOfFolds
    avg_test_ll = avg_test_ll / numberOfFolds
    avg_learning_time = avg_learning_time / numberOfFolds / 1000  # in seconds
    data_results["average_learning_time"] = avg_learning_time
    data_results["average_test_LL"] = avg_test_ll
    data_results["average_train_BIC"] = avg_train_bic
    data_results["folds"] = folds

    # Print the average results
    print("\nAverage learning time (s): " + str(avg_learning_time))
    print("Average test LL: " + str(avg_test_ll))
    print("Average train BIC: " + str(avg_train_bic))

    # Export the results for this dataset in a JSON file
    if os.path.isfile('../../latent_results/' + 'run_' + str(run) + '/discrete/' + data + '/' + str(numberOfFolds)
                      + '_folds/BI/' + data + '_results_BI.json'):
        os.remove('../../latent_results/' + 'run_' + str(run) + '/discrete/' + data + '/' + str(numberOfFolds)
                  + '_folds/BI/' + data + '_results_BI.json')
        with open('../../latent_results/' + 'run_' + str(run) + '/discrete/' + data + '/' + str(numberOfFolds)
                  + '_folds/BI/' + data + '_results_BI.json', 'w') as fp:
            json.dump(data_results, fp, sort_keys=True, indent=4)
    else:
        with open('../../latent_results/' + 'run_' + str(run) + '/discrete/' + data + '/' + str(numberOfFolds)
                  + '_folds/BI/' + data + '_results_BI.json', 'w') as fp:
            json.dump(data_results, fp, sort_keys=True, indent=4)

    # Store in a Dictionary the results from this dataset (a dictionary of dictionaries)
    results[data] = data_results

# if os.path.isfile('../../latent_results/' + 'run_' + str(run) + '/results_BI.json'):
#     os.remove('../../latent_results/' + 'run_' + str(run) + '/results_BI.json')
#     with open('../../latent_results/' + 'run_' + str(run) + '/results_BI.json', 'w') as fp:
#         json.dump(latent_results, fp, sort_keys=True, indent=4)
# else:
#     with open('../../latent_results/' + 'run_' + str(run) + '/results_BI.json', 'w') as fp:
#         json.dump(latent_results, fp, sort_keys=True, indent=4)

input("All the experiments have finished. Models resulting of each fold can be found in the 'latent_results' folder. "
      "The score and time results can be found in 'results_BI.json' and in their respective JSON results "
      "files (i.e., 'asia_results_BI.json')")
