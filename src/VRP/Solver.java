package VRP;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.io.FileWriter;
import java.io.IOException;


public class Solver {

    private Map<String, String> parameters;

    public Solver(String[] args) {
        parameters = new HashMap<>();
        for (String arg : args) {
            String[] param = arg.split("=");
            if (param.length == 2) {
                parameters.put(param[0], param[1]);
            }
        }
    }

    public boolean solveConstruct(Solution s){
        String construct = parameters.get("CONSTRUCT");
        if ("0".equals(construct)) {
            return true;
        }
        else if ("1".equals(construct)) {
            return true;
        } else {
            System.err.println("Parametre de construction invalide(CONSTRUCT): " + construct);
            return false;
        }
    }

    public void output(Solution s, double temps) {
        String output = parameters.get("OUTPUT");
        if (output != null) {
            File dir = new File(output).getParentFile();
            if(dir != null && !dir.exists()){
                dir.mkdir();
            }
            try (FileWriter writer = new FileWriter(output, false)) {
                writer.append("Input File: ").append(parameters.get("INPUT")).append("\n");
                for (Map.Entry<String, String> entry : parameters.entrySet()) {
                    if (!"input".equals(entry.getKey())) {
                        writer.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
                    }
                }
                writer.append(s+"\n");
                writer.append("Time: ").append(String.valueOf(temps)).append("\n");
            } catch (IOException e) {
                System.err.println("Erreur lors de l'écriture du fichier de sortie.");
            }
        } else {
            System.err.println("Sotie non spécifiée(OUTPUT).");
        }
    }

    public void solve(){
        if (parameters.containsKey("INPUT") && parameters.containsKey("METHOD")) {
            if (Instance.readFile(parameters.get("INPUT"))) {
                Solution s = new Solution();
                long start = System.currentTimeMillis();
                if (solveConstruct(s)) {
                    long end = System.currentTimeMillis();
                    output(s, (end - start) / 1000.0);
                }
            } else {
                System.err.println("Erreur lors de la lecture du fichier d'entrée(INPUT).");
            }
        } else {
            System.err.println("Paramètres d'entrée invalides(INPUT/METHOD).");

        }
    }
}