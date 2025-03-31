package VRP.solution;

import VRP.Instance;
import VRP.model.HotelNode;
import VRP.model.Node;
import VRP.model.Route;
import VRP.model.SiteNode;

import java.util.ArrayList;
import java.util.List;

public class NearestNeighborPotentialSolution extends NearestNeighborSolution{

    public NearestNeighborPotentialSolution(){
        super();
    }

    @Override
    protected void constructHotels() {
        List<Route> routesConstruct = new ArrayList<>(this.routes);

        List<List<Route>> solutions = new ArrayList<>();
        recursiveHotels(routesConstruct.get(0), routesConstruct, solutions);

        // Tri des solutions par potentiel et pas distance
        solutions.sort((o1, o2) -> {
            int potentialCompare = Integer.compare(potentielHotels(o2), potentielHotels(o1)); // Descending order
            if (potentialCompare != 0) return potentialCompare; // If different, use this order
            return Double.compare(sumDistance(o1), sumDistance(o2)); // Otherwise, sort by distance (ascending)
        });
        this.routes = solutions.isEmpty() ? routesConstruct : solutions.get(0);

        for(HotelNode hotel : hotels){
            hotel.removeAllRoutes();
        }
        for(Route route : this.routes){
            route.getHotelStart().addRoute(route);
            route.getHotelEnd().addRoute(route);
        }
    }

    private int potentielHotels(List<Route> routes){
        int score = 0;
        for(Route route : routes){
            score += potentielHotel(route);
        }
        return score;
    }

    private int potentielHotel(Route route){
        int score = 0;
        for(SiteNode site : sites){
            if(route.getDistanceMax() >= Instance.getDistance(route.getHotelStart().getId(), site.getId()) + Instance.getDistance(site.getId(), route.getHotelEnd().getId())){
                score += site.getScore();
            }
        }
        return score;
    }

    private double sumDistance(List<Route> routes){
        double sum = 0;
        for(Route route : routes){
            sum += Instance.getDistance(route.getHotelStart().getId(), route.getHotelEnd().getId());
        }
        return sum;
    }
}
