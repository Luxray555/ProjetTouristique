package VRP.solution;

import VRP.Instance;
import VRP.model.Node;
import VRP.model.Route;
import VRP.model.SiteNode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NearestNeighborPotentialImproveSolution extends NearestNeighborSolution{

    public NearestNeighborPotentialImproveSolution(){
        super();
    }

    @Override
    public void construct() {
        this.constructHotels();
        this.constructSites();
        for (Node hotel : hotels) {
            hotel.removeAllRoutes();
        }
        for (Route route : this.routes) {
            route.getHotelStart().addRoute(route);
            route.getHotelEnd().addRoute(route);
        }
        this.evaluate();
    }

    @Override
    protected void constructSites() {
        List<Route> routesConstruct = new ArrayList<>();
        for (Route route : this.routes) {
            routesConstruct.add(new Route(route.getId(), route.getHotelStart(), route.getHotelEnd(), route.getDistanceMax()));
        }
        List<List<Route>> solutions = new ArrayList<>();
        recursiveSites(routesConstruct.get(0), routesConstruct, solutions, new ArrayList<>(), routesConstruct.get(0).getHotelStart());
        solutions.sort((o1, o2) -> {
            double sum1 = o1.stream().mapToDouble(Route::getDistanceTotal).sum();
            double sum2 = o2.stream().mapToDouble(Route::getDistanceTotal).sum();
            return Double.compare(sum2, sum1);
        });

        this.routes = solutions.isEmpty() ? routesConstruct : solutions.get(0);
        for(Route route : this.routes){
            for(SiteNode site : route.getSites()){
                site.removeAllRoutes();
                site.addRoute(route);
            }
        }
    }


    @Override
    protected void constructHotels() {
        List<Route> routesConstruct = new ArrayList<>(this.routes);

        List<List<Route>> solutions = new ArrayList<>();
        recursiveHotels(routesConstruct.get(0), routesConstruct, solutions);

        solutions.sort((o1, o2) -> {
            int potentialCompare = Integer.compare(potentielHotels(o2), potentielHotels(o1));
            if (potentialCompare != 0) return potentialCompare;
            return Double.compare(sumDistance(o2), sumDistance(o1));
        });
        this.routes = solutions.isEmpty() ? routesConstruct : solutions.get(0);
        for (Node hotel : hotels) {
            System.out.println("Avant suppression, routes de " + hotel + " : " + hotel.getRoutes().size());
            hotel.removeAllRoutes();
            System.out.println("Après suppression, routes de " + hotel + " : " + hotel.getRoutes().size());
        }
        for (Route route : this.routes) {
            route.getHotelStart().addRoute(route);
            route.getHotelEnd().addRoute(route);
        }
    }

    protected void recursiveSites(Route currentRoute, List<Route> routesConstruct,
                                  List<List<Route>> solutions, List<Node> visitedSites, Node lastSite) {
        Result result = nearestSite(lastSite, currentRoute, visitedSites);
        SiteNode site = (SiteNode) result.getNode();

        if (site != null && currentRoute.checkDistanceLast(site) && !visitedSites.contains(site)) {
            currentRoute.addLast(site);
            visitedSites.add(site);

            recursiveSites(currentRoute, routesConstruct, solutions, visitedSites, site);

            currentRoute.removeLast();
            visitedSites.remove(site);
        }

        if (currentRoute.getId() < routesConstruct.size() - 1) {
            Route nextRoute = routesConstruct.get(currentRoute.getId() + 1);
            recursiveSites(nextRoute, routesConstruct, solutions, visitedSites, nextRoute.getHotelStart());
        } else {
            if (currentRoute.getDistanceTotal() <= currentRoute.getDistanceMax()) {
                solutions.add(cloneRoutes(routesConstruct));
            }
        }
    }

    private List<Route> cloneRoutes(List<Route> routes) {
        List<Route> cloned = new ArrayList<>();
        for (Route r : routes) {
            Route newRoute = new Route(r.getId(), r.getHotelStart(), r.getHotelEnd(), r.getDistanceMax());
            for (SiteNode site : r.getSites()) {
                newRoute.addLast(site);
            }
            cloned.add(newRoute);
        }
        return cloned;
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
