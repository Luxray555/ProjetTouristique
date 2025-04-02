package VRP.solution;

import VRP.*;
import VRP.model.HotelNode;
import VRP.model.Node;
import VRP.model.Route;
import VRP.model.SiteNode;

import java.util.ArrayList;
import java.util.List;

public class NearestNeighborSolution extends Solution{

    public NearestNeighborSolution(){
        super();
    }

    @Override
    public void construct() {
        this.constructHotels();
        this.constructSites();
        this.evaluate();
    }

    protected void constructSites(){
        List<Node> visited = new ArrayList<>();
        for(int i = 0; i < routes.size(); i++){
            Route route = routes.get(i);
            Node current = route.getHotelStart();
            while(visited.size() < sites.size() && current != null){
                Result next = nearestSite(current, route, visited);
                if(next.getNode() != null && route.getDistanceMax() > route.getDistanceTotal() + next.getDistance()){
                    route.addLast((SiteNode)next.getNode());
                    visited.add(next.getNode());
                    current = next.getNode();
                }else{
                    current = null;
                }
            }
        }
    }

    protected void constructHotels() {
        List<Route> routesConstruct = new ArrayList<>(this.routes);
        List<List<Route>> solutions = new ArrayList<>();
        recursiveHotels(routesConstruct.get(0), routesConstruct, solutions);

        this.routes = solutions.isEmpty() ? routesConstruct : solutions.get(0);
        for(HotelNode hotel : hotels){
            hotel.removeAllRoutes();
        }
        for(Route route : this.routes){
            route.getHotelStart().addRoute(route);
            route.getHotelEnd().addRoute(route);
        }
    }


    protected void recursiveHotels(Route route, List<Route> routesConstruct, List<List<Route>> solutions) {
        if (route.getId() == routesConstruct.size() - 1) {
            if (route.getDistanceTotal() <= route.getDistanceMax()) {
                List<Route> solution = new ArrayList<>();
                for (Route r : routesConstruct) {
                    solution.add(new Route(r.getId(), r.getHotelStart(), r.getHotelEnd(), r.getDistanceMax()));
                }
                solutions.add(solution);
            }
            return;
        }

        List<HotelNode> orderedHotels = new ArrayList<>(hotels);
        while (!orderedHotels.isEmpty()) {
            Result result = nearestHotel(route, orderedHotels);
            if (result.getDistance() <= route.getDistanceMax()) {
                if (routesConstruct.size() - 1 > route.getId()) {
                    route.setHotelEnd((HotelNode) result.getNode());
                    routesConstruct.get(route.getId() + 1).setHotelStart((HotelNode) result.getNode());

                    recursiveHotels(routesConstruct.get(route.getId() + 1), routesConstruct, solutions);

                    route.removeHotelEnd();
                    routesConstruct.get(route.getId() + 1).removeHotelStart();
                }
            }
            orderedHotels.remove(result.getNode());
        }
    }


    protected Result nearestSite(Node node, Route route, List<Node> visited){
        Result result = new Result(null, Double.MAX_VALUE);
        for(SiteNode site : sites){
            if(!visited.contains(site)){
                double dist = Instance.getDistance(node.getId(), site.getId());
                if(dist < result.getDistance() && route.checkDistanceLast(site)){
                    result.setDistance(dist);
                    result.setNode(site);
                }
            }
        }
        return result;
    }

    protected Result nearestHotel(Route route, List<HotelNode> hotels){
        Result result = new Result(null, Double.MAX_VALUE);
        for(HotelNode hotel : hotels){
            double dist = Instance.getDistance(route.getHotelStart().getId(), hotel.getId());
            if(dist < result.getDistance()){
                result.setDistance(dist);
                result.setNode(hotel);
            }
        }
        return result;
    }

    public class Result{
        private double distance;
        private Node node;

        public Result(Node node, double distance){
            this.node = node;
            this.distance = distance;
        }

        public Node getNode() {
            return node;
        }

        public void setNode(Node node) {
            this.node = node;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }
    }



}
