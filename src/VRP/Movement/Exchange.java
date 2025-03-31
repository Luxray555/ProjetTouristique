package VRP.Movement;

import VRP.*;
import VRP.solution.Solution;
import VRP.model.Node;
import VRP.model.Route;
import VRP.model.SiteNode;
import VRP.model.HotelNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Exchange implements Movement {

    boolean check(Solution s, int i, int j) {
        SiteNode nodeI = (SiteNode) s.getNodes().get(i);
        SiteNode nodeJ = (SiteNode) s.getNodes().get(j);

        Route routeI = nodeI.getRoute(0);
        Route routeJ = nodeJ.getRoute(0);

        // Calcul des distances I
        double originalDistanceI = routeI.getDistanceTotal();
        double distanceBeforeSwapI = Instance.getDistance(nodeI.getPrevious().getId(), nodeI.getId()) +
                Instance.getDistance(nodeI.getId(), nodeI.getNext().getId());
        double distanceAfterSwapI = Instance.getDistance(nodeI.getPrevious().getId(), nodeJ.getId()) +
                Instance.getDistance(nodeJ.getId(), nodeI.getNext().getId());

        // Calcul des distances J
        double originalDistanceJ = routeJ.getDistanceTotal();
        double distanceBeforeSwapJ = Instance.getDistance(nodeJ.getPrevious().getId(), nodeJ.getId()) +
                Instance.getDistance(nodeJ.getId(), nodeJ.getNext().getId());
        double distanceAfterSwapJ = Instance.getDistance(nodeJ.getPrevious().getId(), nodeI.getId()) +
                Instance.getDistance(nodeI.getId(), nodeJ.getNext().getId());

        // Vérifie si les nouvelles distances respectent la capacité maximale de la route
        return (originalDistanceI - distanceBeforeSwapI + distanceAfterSwapI <= routeI.getDistanceMax()) &&
                (originalDistanceJ - distanceBeforeSwapJ + distanceAfterSwapJ <= routeJ.getDistanceMax());
    }


    public double evaluate(Solution s, int i, int j) {
        SiteNode siteI = (SiteNode) s.getNodes().get(i);
        SiteNode siteJ = (SiteNode) s.getNodes().get(j);

        //double distPlus = 0;
        //double distMoins = 0;
        double nvScore = 0;

        /*
        distMoins += Instance.getDistance(siteI.getPrevious().getId(), siteI.getId()) +
                Instance.getDistance(siteJ.getNext().getId(), siteJ.getId());
        distPlus += Instance.getDistance(siteI.getPrevious().getId(), siteJ.getId()) +
                Instance.getDistance(siteJ.getPrevious().getId(), siteI.getId());

        distMoins += Instance.getDistance(siteI.getNext().getId(), siteI.getId()) +
                Instance.getDistance(siteJ.getPrevious().getId(), siteJ.getId());
        distPlus += Instance.getDistance(siteI.getNext().getId(), siteJ.getId()) +
                Instance.getDistance(siteJ.getNext().getId(), siteI.getId());
        */

        if (siteI.getRoutes().get(0) == null) {
            nvScore = s.getScore() - siteJ.getScore() + siteI.getScore();
        } else if (siteJ.getRoutes().get(0) == null) {
            nvScore = s.getScore() - siteI.getScore() + siteJ.getScore();
            }
        else {
            nvScore = s.getScore(); }

        return nvScore;
    }

    void apply(Solution s, int i, int j) {
        s.setScore((int) (evaluate(s, i, j)));

        SiteNode siteI = (SiteNode) s.getNodes().get(i);
        SiteNode siteJ = (SiteNode) s.getNodes().get(j);
        Route routeI = siteI.getRoutes().get(0);
        Route routeJ = siteJ.getRoutes().get(0);

        // Échange des noeuds dans le tableau
        Node tmp = s.getNodes().get(i);
        s.getNodes().set(i, s.getNodes().get(j));
        s.getNodes().set(j, tmp);

        // Mise à jour des indices et des scores des noeuds
        s.getNodes().get(i).setId(i);
        s.getNodes().get(j).setId(j);

        // Mise à jour des scores des noeuds échangés
        double tmpScore = s.getNodes().get(i).getScore();
        s.getNodes().get(i).setScore(s.getNodes().get(j).getScore());
        s.getNodes().get(j).setScore(tmpScore);
    }

    @Override
    public boolean applyBestImprovement(Solution s) {
        double max = Double.MIN_VALUE;  // On commence avec la plus petite valeur possible pour chercher un maximum
        int mini = -1;
        int minj = -1;

        // On teste tous les échanges possibles entre les sites
        for (int i = 0; i < s.getNodes().size(); i++) {
            for (int j = i + 1; j < s.getNodes().size(); j++) {
                if (check(s, i, j)) {
                    double eval = evaluate(s, i, j);

                    if (eval > max) {  // Si l'évaluation est meilleure
                        max = eval;
                        mini = i;
                        minj = j;
                    }
                }
            }
        }

        if (max > s.getScore()) {
            apply(s, mini, minj);
            System.out.println("Échange effectué entre les noeuds " + mini + " et " + minj);
            return true;  // Un échange améliorant a été effectué
        }

        return false;  // Aucun échange améliorant trouvé
    }


    @Override
    public boolean applyFirstImprovement(Solution s) {
        List<Pair> toTest = new ArrayList<>();

        // On génère toutes les paires possibles de sites
        for (int i = 0; i < s.getNodes().size(); i++) {
            for (int j = i + 1; j < s.getNodes().size(); j++) {
                toTest.add(new Pair(i, j));
            }
        }

        // Mélange des paires pour tester un échange aléatoire
        Collections.shuffle(toTest);

        // On parcourt les paires et applique le premier échange améliorant trouvé
        for (Pair pair : toTest) {
            int i = pair.getI();
            int j = pair.getJ();
            if (check(s, i, j) && evaluate(s, i, j) > s.getScore()) {
                System.out.println("Échange effectué entre les noeuds " + i + " et " + j);
                apply(s, i, j);
                return true;
            }
        }

        return false; // Si aucun échange n'est améliorant
    }

    // Classe interne pour représenter une paire de noeuds
    private static class Pair {
        private int i, j;

        public Pair(int i, int j) {
            this.i = i;
            this.j = j;
        }

        public int getI() {
            return i;
        }

        public int getJ() {
            return j;
        }
    }
}
