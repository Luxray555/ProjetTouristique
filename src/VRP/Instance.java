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

    private static int nbHotel = 2;

    private static double[] distanceMaxJour;

    private static double[][] distances;

    private static int[] scores;

    private Instance(){}

    public static void readFile(String stringPath){
        File file = null;
        Scanner scanner = null;
        try{
            file = new File(stringPath);
            scanner = new Scanner(file);
        }catch(NullPointerException | FileNotFoundException e){
            System.err.println("Fichier non trouvé.");
        }
        int nbLine = 0;
        double[][] coord = null;
        while (scanner.hasNextLine()){
            String line = scanner.nextLine();
            String[] lineSplit = line.split("\t");
            if(line == "" || line.startsWith("-")){
                continue;
            }else{
                switch (nbLine) {
                    case 0:
                        try {
                            nbHotelSupp = Integer.parseInt(lineSplit[1]);
                            nbDestination = Integer.parseInt(lineSplit[0]) + nbHotelSupp;
                            coord = new double[nbDestination][2];
                            scores = new int[nbDestination];
                            nbSiteTouristique = nbDestination - nbHotelSupp - nbHotel;
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
                        } catch (Exception e) {
                            System.err.println("Ligne " + nbLine + " : Nombre de jours ne coincide pas avec le tableau de distance max journalière." + lineSplit.length + " = " + jours);
                        }
                        break;
                    default:
                        try{
                            coord[nbLine - 3][0] = Double.parseDouble(lineSplit[0]);
                            coord[nbLine - 3][1] = Double.parseDouble(lineSplit[1]);
                            scores[nbLine - 3] = Integer.parseInt(lineSplit[2]);
                        }catch (NumberFormatException e){
                            System.err.println("Ligne " + nbLine + " : Format coordonnée/scores non valide.");
                        }
                        break;
                }
            }
            nbLine++;
        }
        distances = new double[nbDestination][nbDestination];
        for(int i = 0; i < nbDestination ; i++){
            for(int j = 0; j < nbDestination ; j++){
                if(i == j){
                    distances[i][j] = 0;
                }else{
                    distances[i][j] = Math.sqrt(Math.pow(coord[i][0] - coord[j][0], 2) + Math.pow(coord[i][1] - coord[j][1], 2));
                }
            }
        }
    }

    public static void showInfo(){
        System.out.println("Nb Destination : " + nbDestination);
        System.out.println("Nb Site T : " + nbSiteTouristique);
        System.out.println("Nb Hotel " + nbHotel);
        System.out.println("Nb Hotel Supp : " + nbHotelSupp);
        System.out.println("Jours : " + jours);
        System.out.println("Distance Max Jours : " + Arrays.toString(distanceMaxJour));
        System.out.println("Scores : " + Arrays.toString(scores));

    }

    public static int getNbDestination(){
        return nbDestination;
    }

    public static int getNbHotel(){
        return nbHotel;
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
