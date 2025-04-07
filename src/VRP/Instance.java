package VRP;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class Instance {

    private static int nbDestination;

    private static int jours;

    private static int nbSiteTouristique;

    private static int nbHotelSupp;

    private static double[] distanceMaxJour;

    private static double[][] distances;

    private static int[] scores;

    private Instance(){}

    public static boolean readFile(String stringPath){
        File file = null;
        Scanner scanner = null;
        try{
            file = new File(stringPath);
            scanner = new Scanner(file);
        }catch(NullPointerException | FileNotFoundException e){
            System.err.println("Fichier non trouvé.");
            return false;
        }
        int nbLine = 0;
        int dataIndex = 0;
        double[][] coord = null;
        while (scanner.hasNextLine()){
            String line = scanner.nextLine();
            String[] lineSplit = line.split("\t");
            if(line.isEmpty() || line.startsWith("--")){
                continue;
            }else{
                switch (nbLine) {
                    case 0:
                        try {
                            nbHotelSupp = Integer.parseInt(lineSplit[1]);
                            nbDestination = Integer.parseInt(lineSplit[0]) + nbHotelSupp;
                            coord = new double[nbDestination][2];
                            scores = new int[nbDestination];
                            nbSiteTouristique = nbDestination - nbHotelSupp - 2;
                            jours = Integer.parseInt(lineSplit[2]);
                            distanceMaxJour = new double[jours];
                        }catch (NumberFormatException | IndexOutOfBoundsException e){
                            System.err.println("Ligne " + nbLine + " : Format invalide.");
                        }
                        break;
                    case 1:
                        try{
                            if(lineSplit.length != jours){
                                throw new Exception();
                            }
                            for(int i = 0; i<jours ; i++){
                                distanceMaxJour[i] = Double.parseDouble(lineSplit[i]);
                            }
                        }catch (NumberFormatException | IndexOutOfBoundsException e){
                            System.err.println("Ligne " + nbLine + " : Les distances ne sont pas dans le bon format.");
                            return false;
                        } catch (Exception e) {
                            System.err.println("Ligne " + nbLine + " : Nombre de jours ne coincide pas avec le tableau de distance max journalière." + lineSplit.length + " = " + jours);
                            return false;
                        }
                        break;
                    default:
                        try{
                            coord[dataIndex][0] = Double.parseDouble(lineSplit[0]);
                            coord[dataIndex][1] = Double.parseDouble(lineSplit[1]);
                            scores[dataIndex] = Integer.parseInt(lineSplit[2]);
                            dataIndex++;
                        }catch (NumberFormatException | ArrayIndexOutOfBoundsException e){
                            System.err.println("Ligne " + nbLine + " : Format coordonnée/scores non valide.");
                            return false;
                        }
                        break;
                }
            }
            nbLine++;
        }
        distances = new double[nbDestination][nbDestination];
        for(int i = 0; i < nbDestination ; i++){
            for(int j = i; j < nbDestination ; j++){
                if(i == j){
                    distances[i][j] = 0;
                }else{
                    double dist = Math.sqrt(Math.pow(coord[i][0] - coord[j][0], 2) + Math.pow(coord[i][1] - coord[j][1], 2));
                    distances[i][j] = dist;
                    distances[j][i] = dist;
                }
            }
        }
        return true;
    }

    public static void showInfo(){
        System.out.println("Nb Destination : " + nbDestination);
        System.out.println("Nb Site T : " + nbSiteTouristique);
        System.out.println("Nb Hotel " + 2);
        System.out.println("Nb Hotel Supp : " + nbHotelSupp);
        System.out.println("Jours : " + jours);
        System.out.println("Distance Max Jours : " + Arrays.toString(distanceMaxJour));
        System.out.println("Scores : " + Arrays.toString(scores));
        for(int i = 0; i < nbDestination ; i++){
            System.out.println("Distance " + i + " : " + Arrays.toString(distances[i]));
        }

    }

    public static int getNbDestination(){
        return nbDestination;
    }

    public static int getNbHotel(){
        return 2 + nbHotelSupp;
    }

    public static int getJours() {
        return jours;
    }

    public static int getNbSiteTouristique() {
        return nbSiteTouristique;
    }

    public static int getNbHotelSupp() {
        return nbHotelSupp;
    }

    public static double[] getDistanceMaxJour() {
        return distanceMaxJour;
    }

    public static double getDistanceMaxJour(int i){
        return distanceMaxJour[i];
    }

    public static double[][] getDistances() {
        return distances;
    }

    public static double getDistance(int i, int j) {
        return distances[i][j];
    }

    public static int[] getScores() {
        return scores;
    }

    public static int getScore(int i){
        return scores[i];
    }
}
