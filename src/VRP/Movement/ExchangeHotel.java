package VRP.Movement;

import VRP.Instance;
import VRP.checker.Checker;
import VRP.model.*;
        import VRP.solution.Solution;

import java.util.ArrayList;
import java.util.List;

public class ExchangeHotel {

    private List<SiteNode> check(Route route, HotelNode hotel, Solution s) {
        if (route.getHotelEnd() == hotel) return null;
        Route nextRoute = s.getRoutes().get(route.getId() + 1);
        List<SiteNode> deletedSite = new ArrayList<>();

        double startToNewHotel = Instance.getDistance(route.getHotelStart().getId(), hotel.getId());
        double newHotelToNextEnd = Instance.getDistance(hotel.getId(), nextRoute.getHotelEnd().getId());

        if (route.getDistanceMax() >= startToNewHotel && nextRoute.getDistanceMax() >= newHotelToNextEnd) {

            if (!route.getSites().isEmpty() && !nextRoute.getSites().isEmpty()) {
                double diffDistance = route.getDistanceTotal() - Instance.getDistance(route.getLast().getId(), route.getHotelEnd().getId()) +
                        Instance.getDistance(route.getLast().getId(), hotel.getId());
                double diffDistanceNext = nextRoute.getDistanceTotal() - Instance.getDistance(nextRoute.getHotelStart().getId(), nextRoute.getFirst().getId()) +
                        Instance.getDistance(hotel.getId(), nextRoute.getFirst().getId());

                if (route.getDistanceMax() >= diffDistance && nextRoute.getDistanceMax() >= diffDistanceNext) {
                    return deletedSite;
                } else {
                    Solution temp = s.copy();
                    temp.getRoutes().get(route.getId()).setHotelEnd(hotel);
                    temp.getRoutes().get(nextRoute.getId()).setHotelStart(hotel);
                    deletedSite.addAll(refindById(s, adjustDistance(temp, route.getId())));
                    deletedSite.addAll(refindById(s, adjustDistance(temp, nextRoute.getId())));
                    return deletedSite;
                }

            } else if (route.getSites().isEmpty() && !nextRoute.getSites().isEmpty()) {
                Solution temp = s.copy();
                temp.getRoutes().get(nextRoute.getId()).setHotelStart(hotel);
                deletedSite.addAll(refindById(s, adjustDistance(temp, nextRoute.getId())));
                return deletedSite;

            } else if (nextRoute.getSites().isEmpty() && !route.getSites().isEmpty()) {
                Solution temp = s.copy();
                temp.getRoutes().get(route.getId()).setHotelEnd(hotel);
                deletedSite.addAll(refindById(s, adjustDistance(temp, route.getId())));
                return deletedSite;
            } else {
                return deletedSite;
            }
        }
        return null;
    }

    private List<SiteNode> adjustDistance(Solution s, int idRoute) {
        List<SiteNode> deletedSites = new ArrayList<>();

        Route route = s.getRoutes().get(idRoute);

        if (route.getDistanceMax() < route.getDistanceTotal()) {
            List<SiteNode> ordered = orderedSites(route.getSites());

            for (SiteNode site : ordered) {
                route.removeSite(site);
                deletedSites.add(site);
                if (route.getDistanceTotal() <= route.getDistanceMax()) {
                    break;
                }
            }
        }

        return deletedSites;
    }

    private List<SiteNode> refindById(Solution s, List<SiteNode> sites){
        return sites.stream()
                .map(site -> s.getSiteWithId(site.getId()))
                .toList();
    }



    private int evaluate(List<SiteNode> siteDeleted, Solution s){
        int score = s.getScore();
        for(SiteNode site : siteDeleted){
            score -= site.getScore();
        }
        return score;
    }

    //Trié par score/distance voisin
    private List<SiteNode> orderedSites(List<SiteNode> sites){
        List<SiteNode> orderedSites = new ArrayList<>(sites);
        orderedSites.sort((s1, s2) -> {
            double score1 = s1.getScore() / getNeighboursDistance(s1);
            double score2 = s2.getScore() / getNeighboursDistance(s2);
            return Double.compare(score2, score1);
        });
        return orderedSites;
    }

    private double getNeighboursDistance(SiteNode node){
        return Instance.getDistance(node.getId(), node.getNext().getId()) + Instance.getDistance(node.getId(), node.getPrevious().getId());
    }

    private double getDeletedDiffNeighbour(SiteNode node){
        return -getNeighboursDistance(node) +
                Instance.getDistance(node.getPrevious().getId(), node.getNext().getId());
    }

    public void apply(Route route, HotelNode hotel, List<SiteNode> deletedSites, Solution s){
        route.setHotelEnd(hotel);
        for(SiteNode site : deletedSites){
            route.removeSite(site);
            s.getRoutes().get(route.getId() + 1).removeSite(site);
            System.out.println("Suppression du site " + site.getId() + " de la route " + route.getId());
        }
        s.getRoutes().get(route.getId() + 1).setHotelStart(hotel);
        s.evaluate();
    }

    public boolean applyBestImprovement(Solution s) {
        double max = -1;
        HotelNode hotel = null;
        List<SiteNode> deletedSites = new ArrayList<>();

        for(int i = 0; i < s.getRoutes().size() - 1; i++){
            for(HotelNode h : s.getHotels()){
                deletedSites = check(s.getRoutes().get(i), h, s);
                if(deletedSites != null){
                    int score = evaluate(deletedSites, s);
                    if(score > max){
                        max = score;
                        hotel = h;
                    }
                }
            }
        }
        if(hotel != null && deletedSites != null){
            apply(s.getRoutes().get(0), hotel, deletedSites, s);
            System.out.println("Remplacement de l'hotel " + hotel.getId() + " sur la route " + s.getRoutes().get(0).getId());
            return true;
        }
        return false;
    }

    public boolean applyTS(Solution s, List<Pair> tabuList, int tabuSize, double bestValue) {
        double max = -1;
        Pair best = new Pair(-1, -1);
        List<SiteNode> deletedSites = new ArrayList<>();

        for(int i = 0; i < s.getRoutes().size() - 1; i++){
            for(HotelNode h : s.getHotels()){
                if(!isTabu(i, h.getId(), tabuList)){
                    deletedSites = check(s.getRoutes().get(i), h, s);
                    if(deletedSites != null){
                        int eval = evaluate(deletedSites, s);
                        if(eval > bestValue && eval > max){
                            max = eval;
                            best.setI(i);
                            best.setJ(h.getId());
                        }else if(eval > max && !isTabu(i, h.getId(), tabuList)){
                            max = eval;
                            best.setI(i);
                            best.setJ(h.getId());
                        }
                    }
                }
            }
        }
        if(max > -1 && deletedSites != null){
            apply(s.getRoutes().get(best.getI()), s.getHotels().get(best.getJ()), deletedSites, s);
            tabuList.add(best);
            if(tabuList.size() > tabuSize){
                tabuList.remove(0);
            }
            System.out.println("Remplacement de l'hotel " + best.getJ() + " sur la route " + best.getI());
            return true;
        }
        return false;
    }

    private boolean isTabu(int i, int j, List<Pair> tabuList){
        for(Pair pair : tabuList){
            if((pair.getI() == i && pair.getJ() == j)){
                return true;
            }
        }
        return false;
    }


}
