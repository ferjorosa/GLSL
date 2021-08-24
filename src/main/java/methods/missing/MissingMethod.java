package methods.missing;

import eu.amidst.core.models.BayesianNetwork;
import eu.amidst.extension.io.GenieWriter;

import java.io.File;
import java.util.List;

public interface MissingMethod {

    default void storeAmidstModels(List<BayesianNetwork> models,
                                   String directoryPath,
                                   String dataName,
                                   String methodName) throws Exception {

        new File(directoryPath).mkdirs();


        for(int i = 0; i < models.size(); i++) {
            String output = directoryPath + "/" + dataName  + "_f" + (i+1) + "_" + methodName + ".xdsl";
            GenieWriter genieWriter = new GenieWriter();
            genieWriter.write(models.get(i), new File(output));
        }
    }
}
