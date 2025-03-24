import VRP.Solver;

import java.io.File;

public class Main {

    public static void main(String[] args){
        //Lire tous les fichier d'un dossier
        File folder = new File("data");
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                System.out.println(file.getName() + " : Started");
                String[] parameters = {
                    "INPUT=data/"+file.getName(),
                    "CONSTRUCT=0",
                    "METHOD=0",
                    "OUTPUT=result/"+file.getName().replace(".txt", ".sol")
                };
                Solver solver = new Solver(parameters);
                solver.solve();
                System.out.println(file.getName() + " : Finished");
            }
        }
    }
}
