package VRP.Movement;

import VRP.*;
import VRP.model.*;
import VRP.solution.Solution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Exchange implements Movement {

    boolean check(Solution s, int i, int j) {
        SiteNode nodeI = s.getSites().get(i);
        SiteNode nodeJ = s.getSites().get(j);

        Route routeI = nodeI.getRoute(0);
        Route routeJ = nodeJ.getRoute(0);

        double distanceBeforeSwapI = 0.0;

        double distanceAfterSwapI = 0.0;

        double distanceBeforeSwapJ = 0.0;

        double distanceAfterSwapJ = 0.0;

        if(routeI != null && routeJ != null){
            if(nodeI.getPrevious().getId() == nodeJ.getId()){
                distanceBeforeSwapI = Instance.getDistance(nodeJ.getPrevious().getId(), nodeJ.getId()) +
                        Instance.getDistance(nodeJ.getId(), nodeI.getId()) +
                        Instance.getDistance(nodeI.getId(), nodeI.getNext().getId());
                distanceAfterSwapI = Instance.getDistance(nodeJ.getPrevious().getId(), nodeI.getId()) +
                        Instance.getDistance(nodeI.getId(), nodeJ.getId()) +
                        Instance.getDistance(nodeJ.getId(), nodeI.getNext().getId());
                return (routeI.getDistanceTotal() - distanceBeforeSwapI + distanceAfterSwapI <= routeI.getDistanceMax());
            }else if(nodeJ.getPrevious().getId() == nodeI.getId()) {
                distanceBeforeSwapJ = Instance.getDistance(nodeI.getPrevious().getId(), nodeI.getId()) +
                        Instance.getDistance(nodeI.getId(), nodeJ.getId()) +
                        Instance.getDistance(nodeJ.getId(), nodeJ.getNext().getId());
                distanceAfterSwapJ = Instance.getDistance(nodeI.getPrevious().getId(), nodeJ.getId()) +
                        Instance.getDistance(nodeJ.getId(), nodeI.getId()) +
                        Instance.getDistance(nodeI.getId(), nodeJ.getNext().getId());
                return (routeJ.getDistanceTotal() - distanceBeforeSwapJ + distanceAfterSwapJ <= routeJ.getDistanceMax());
            }
            distanceBeforeSwapI = Instance.getDistance(nodeI.getPrevious().getId(), nodeI.getId()) +
                    Instance.getDistance(nodeI.getId(), nodeI.getNext().getId());
            distanceAfterSwapI = Instance.getDistance(nodeI.getPrevious().getId(), nodeJ.getId()) +
                    Instance.getDistance(nodeJ.getId(), nodeI.getNext().getId());
            distanceBeforeSwapJ = Instance.getDistance(nodeJ.getPrevious().getId(), nodeJ.getId()) +
                    Instance.getDistance(nodeJ.getId(), nodeJ.getNext().getId());
            distanceAfterSwapJ = Instance.getDistance(nodeJ.getPrevious().getId(), nodeI.getId()) +
                    Instance.getDistance(nodeI.getId(), nodeJ.getNext().getId());

            return (routeI.getDistanceTotal() - distanceBeforeSwapI + distanceAfterSwapI <= routeI.getDistanceMax()) &&
                    (routeJ.getDistanceTotal() - distanceBeforeSwapJ + distanceAfterSwapJ <= routeJ.getDistanceMax());
        }else if(routeI == null && routeJ != null){
            distanceBeforeSwapJ = Instance.getDistance(nodeJ.getPrevious().getId(), nodeJ.getId()) +
                    Instance.getDistance(nodeJ.getId(), nodeJ.getNext().getId());
            distanceAfterSwapJ = Instance.getDistance(nodeJ.getPrevious().getId(), nodeI.getId()) +
                    Instance.getDistance(nodeI.getId(), nodeJ.getNext().getId());
            return (routeJ.getDistanceTotal() - distanceBeforeSwapJ + distanceAfterSwapJ <= routeJ.getDistanceMax());
        }else if(routeI != null){
            distanceBeforeSwapI = Instance.getDistance(nodeI.getPrevious().getId(), nodeI.getId()) +
                    Instance.getDistance(nodeI.getId(), nodeI.getNext().getId());
            distanceAfterSwapI = Instance.getDistance(nodeI.getPrevious().getId(), nodeJ.getId()) +
                    Instance.getDistance(nodeJ.getId(), nodeI.getNext().getId());

            return (routeI.getDistanceTotal() - distanceBeforeSwapI + distanceAfterSwapI <= routeI.getDistanceMax());
        }else{
            return false;
        }
    }


    public int evaluate(Solution s, int i, int j) {
        SiteNode siteI = s.getSites().get(i);
        SiteNode siteJ = s.getSites().get(j);

        if (siteI.getRoutes().isEmpty()) {
            return s.getScore() - siteJ.getScore() + siteI.getScore();
        } else if (siteJ.getRoutes().isEmpty()) {
            return s.getScore() - siteI.getScore() + siteJ.getScore();
            }
        else {
            return s.getScore();
        }

    }

    private void apply(Solution s, int i, int j) {
        s.setScore(evaluate(s, i, j));

        SiteNode nodeI = s.getSites().get(i);
        SiteNode nodeJ = s.getSites().get(j);

        Route routeI = nodeI.getRoute(0);
        Route routeJ = nodeJ.getRoute(0);

        int indexI = -1;
        int indexJ = -1;

        if(routeI != null){
            indexI = routeI.removeSite(nodeI);
        }
        if(routeJ != null){
            indexJ = routeJ.removeSite(nodeJ);
        }
        if(indexI != -1){
            routeI.addSite(nodeJ, indexI);
        }
        if (indexJ != -1){
            routeJ.addSite(nodeI, indexJ);
        }
    }

    @Override
    public boolean applyBestImprovement(Solution s) {
        double max = 0;  // On commence avec la plus petite valeur possible pour chercher un maximum
        Pair bestPair = new Pair(-1, -1);
        // On teste tous les échanges possibles entre les sites
        for (int i = 0; i < s.getSites().size(); i++) {
            for (int j = i + 1; j < s.getSites().size(); j++) {
                if (check(s, i, j)) {
                    double eval = evaluate(s, i, j);
                    if (eval > max) {  // Si l'évaluation est meilleure
                        max = eval;
                        bestPair.setI(i);
                        bestPair.setJ(j);
                    }
                }
            }
        }
        if (max > s.getScore()) {
            apply(s, bestPair.getI(), bestPair.getJ());
            System.out.println("Échange effectué entre les noeuds " + bestPair.getI() + " et " + bestPair.getJ());
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
}
