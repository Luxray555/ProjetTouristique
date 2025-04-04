package VRP.checker;

import VRP.Instance;
import VRP.model.Route;
import VRP.model.SiteNode;
import VRP.solution.Solution;

public class Checker {

    public static boolean checkSolution(Solution solution){
        return checkScore(solution) && checkDistanceMax(solution) && checkLinkRoute(solution) && checkLinkRoute(solution) && checkDistanceRoute(solution) && checkScoreRoute(solution);
    }

    public static boolean checkScoreRoute(Solution solution){
        for(Route route : solution.getRoutes()) {
            int score = 0;
            for (int i = 0; i < route.getSites().size(); i++) {
                score += route.getSites().get(i).getScore();
            }
            if (score != route.getScoreTotal()) {
                System.out.println("Score Route " + route.getId() + " : " + route.getScoreTotal() + " != " + score + " : NON OK");
                return false;
            }
        }
        System.out.println("Score Routes OK");
        return true;
    }

    public static boolean checkScore(Solution solution){
        int score = 0;
        for(Route route : solution.getRoutes()){
            for(SiteNode site : route.getSites()){
                score += site.getScore();
            }
        }
        System.out.println("Score : " + score + " == " + solution.getScore() + (score == solution.getScore() ? " : OK" : " : NON OK"));
        return score == solution.getScore();
    }

    public static boolean checkDistanceRoute(Solution solution){
        for(Route route : solution.getRoutes()){
            double distance = 0;
            for(int i = 0; i < route.getSites().size() - 1; i++){
                distance += Instance.getDistance(route.getSites().get(i).getId(), route.getSites().get(i+1).getId());
            }
            if(!route.getSites().isEmpty()) {
                if(route.getHotelStart() != null) {
                    distance += Instance.getDistance(route.getHotelStart().getId(), route.getSites().get(0).getId());
                }
                if(route.getHotelEnd() != null) {
                    distance += Instance.getDistance(route.getSites().get(route.getSites().size() - 1).getId(), route.getHotelEnd().getId());
                }
            }else{
                if(route.getHotelStart() != null && route.getHotelEnd() != null) {
                    distance += Instance.getDistance(route.getHotelStart().getId(), route.getHotelEnd().getId());
                }
            }
            if(Math.round(distance) != Math.round(route.getDistanceTotal())){
                System.out.println("Distance Route " + route.getId() + " : " + route.getDistanceTotal() + " != " + distance + " : NON OK");
                return false;
            }
        }
        System.out.println("Distance Routes OK");
        return true;
    }

    public static boolean checkDistanceMax(Solution solution){
        for(Route route : solution.getRoutes()){
            if(route.getDistanceTotal() > route.getDistanceMax()){
                System.out.println("Distance Max Route " + route.getId() + " : " + route.getDistanceTotal() + " > " + route.getDistanceMax() + " : NON OK");
                return false;
            }
        }
        System.out.println("Distance Max OK");
        return true;
    }

    public static boolean checkLinkRoute(Solution solution){
        for(Route route : solution.getRoutes()){
            for(SiteNode site : route.getSites()){
                if(!site.getRoutes().contains(route)){
                    System.out.println("Link Route " + route.getId() + " : " + site.getId() + " : NON OK");
                    return false;
                }
            }
            if((route.getHotelStart() != null && !route.getHotelStart().getRoutes().contains(route)) || (route.getHotelEnd() != null && !route.getHotelEnd().getRoutes().contains(route))){
                System.out.println(route.getHotelStart().getRoutes().size());
                System.out.println(route.getHotelEnd().getRoutes().size());
                System.out.println("Link Route " + route.getId() + " : Hotel : NON OK");
                return false;
            }
        }
        System.out.println("Link Route OK");
        return true;
    }
}
