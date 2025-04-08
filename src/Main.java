import VRP.Solver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        File folder = new File("data");
        File[] listOfFiles = folder.listFiles();

        try (FileWriter writer = new FileWriter("result.txt")) {
            int scores = 0;
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    System.out.println(file.getName() + " : Started");
                    String[] parameters = {
                            "INPUT=data/" + file.getName(),
                            "CONSTRUCT=3",
                            "METHOD=4",
                            "ITERATIONS=10",
                            "OUTPUT=result/" + file.getName().replace(".txt", ".sol")
                    };
                    Solver solver = new Solver(parameters);

                    solver.solve();
                    int score = solver.getSolution().getScore();
                    scores += score;
                    writer.write(file.getName() + " : " + score + "\n");
                    System.out.println(file.getName() + " : Finished with score " + score);
                }
            }
            writer.write("all : " + scores + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
