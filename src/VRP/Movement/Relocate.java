package VRP.Movement;

import VRP.*;
import VRP.model.*;
import VRP.solution.Solution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Relocate implements Movement {

    private boolean check(Solution s, int i, int j) {

        SiteNode nodeI = s.getSites().get(i);
        SiteNode nodeJ = s.getSites().get(j);

        Route routeJ = s.getSites().get(j).getRoute(0);

        if (routeJ == null) {
            return false;
        }else{

            double distanceBeforeSwap = Instance.getDistance(nodeJ.getId(), nodeJ.getNext().getId());
            double distanceAfterSwap = Instance.getDistance(nodeJ.getId(), nodeI.getId()) + Instance.getDistance(nodeI.getId(), nodeJ.getNext().getId());

            return (routeJ.getDistanceTotal() - distanceBeforeSwap + distanceAfterSwap <= routeJ.getDistanceMax());
        }
    }

    public int evaluate(Solution s, int i, int j) {
        SiteNode siteI = s.getSites().get(i);
        SiteNode siteJ = s.getSites().get(j);

        if (siteI.getRoutes().isEmpty()) {
            return s.getScore() + siteI.getScore();
        } else {
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

        if (routeI != null) {
            indexI = routeI.removeSite(nodeI);
        }
        if (routeJ != null) {
            routeJ.addSite(nodeI, routeJ.getSites().indexOf(nodeJ) + 1);
        } else if (indexI != -1) {
            routeI.addSite(nodeI, indexI);
        }
    }

    @Override
    public boolean applyBestImprovement(Solution s) {
        double max = 0;
        Pair bestPair = new Pair(-1, -1);

        for (int i = 0; i < s.getSites().size(); i++) {
            for (int j = 0; j < s.getSites().size(); j++) {
                if (i != j && check(s, i, j)) {
                    double eval = evaluate(s, i, j);
                    if (eval > max) {
                        max = eval;
                        bestPair.setI(i);
                        bestPair.setJ(j);
                    }
                }
            }
        }
        if (max > s.getScore()) {
            apply(s, bestPair.getI(), bestPair.getJ());
            System.out.println("Déplacement effectué du noeud " + (bestPair.getI() + Instance.getNbHotel()) +
                    " vers " + (bestPair.getJ() + Instance.getNbHotel()));
            return true;
        }
        return false;
    }

    @Override
    public boolean applyFirstImprovement(Solution s) {
        List<Pair> toTest = getPairs(s);

        Collections.shuffle(toTest);

        for (Pair pair : toTest) {
            int i = pair.getI();
            int j = pair.getJ();
            if (check(s, i, j) && evaluate(s, i, j) > s.getScore()) {
                System.out.println("Déplacement effectué du noeud " + i + " vers " + j);
                apply(s, i, j);
                return true;
            }
        }
        return false;
    }

    @Override
    public void applyRandom(Solution s, int nb) {
        List<Pair> toTest = getPairs(s);

        Collections.shuffle(toTest);

        int applied = 0;
        for (Pair pair : toTest) {
            if (applied >= nb) break;

            int i = pair.getI();
            int j = pair.getJ();

            if (check(s, i, j)) {
                System.out.println("Déplacement du nœud " + i + " vers la position de " + j);
                apply(s, i, j);
                applied++;
            }
        }
    }

    @Override
    public List<Pair> getPairs(Solution s) {
        List<Pair> toTest = new ArrayList<>();

        for (int i = 0; i < s.getNodes().size(); i++) {
            for (int j = 0; j < s.getNodes().size(); j++) {
                if (i != j) {
                    toTest.add(new Pair(i, j));
                }
            }
        }

        return toTest;
    }
}
