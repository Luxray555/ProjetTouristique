package VRP.solution;

import VRP.*;
import VRP.checker.Checker;
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
        List<List<Route>> solutions = this.constructHotels();
        solutions = this.constructSites(solutions.get(0));
        this.routes = solutions.get(0);
        this.resetLinks();
        this.evaluate();
    }

    protected List<List<Route>> constructSites(List<Route> routes){
        List<Node> visited = new ArrayList<>();
        for(int i = 0; i < routes.size(); i++){
            Route route = routes.get(i);
            Node current = route.getHotelStart();
            while(visited.size() < sites.size() && current != null){
                Result next = nearestSite(current, route, visited);
                if(next.getNode() != null && route.getDistanceMax() > route.getDistanceTotal() + next.getDistance()){
                    route.addLast((SiteNode)next.getNode());
                    if(!Checker.checkDistanceRoute(this)){
                        System.out.println("0");
                        System.out.println("Error: distance route is not valid");
                        System.exit(1);
                    }
                    visited.add(next.getNode());
                    current = next.getNode();
                }else{
                    current = null;
                }
            }
        }
        List<List<Route>> solutions = new ArrayList<>();
        solutions.add(routes);
        return solutions;
    }

    protected List<List<Route>> constructHotels() {
        List<Route> routesConstruct = new ArrayList<>(this.routes);

        List<List<Route>> solutions = new ArrayList<>();
        recursiveHotels(routesConstruct.get(0), routesConstruct, solutions);

        sortedSolutions(solutions);

        return solutions;
    }

    protected void sortedSolutions(List<List<Route>> solution){
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
                    if(!Checker.checkDistanceRoute(this)){
                        System.out.println("1");
                        System.out.println("Error: distance route is not valid");
                        System.exit(1);
                    }
                    routesConstruct.get(route.getId() + 1).setHotelStart((HotelNode) result.getNode());
                    if(!Checker.checkDistanceRoute(this)){
                        System.out.println("2");
                        System.out.println("Error: distance route is not valid");
                        System.exit(1);
                    }

                    recursiveHotels(routesConstruct.get(route.getId() + 1), routesConstruct, solutions);

                    route.removeHotelEnd();
                    if(!Checker.checkDistanceRoute(this)){
                        System.out.println("3");
                        System.out.println("Error: distance route is not valid");
                        System.exit(1);
                    }
                    routesConstruct.get(route.getId() + 1).removeHotelStart();
                    if(!Checker.checkDistanceRoute(this)){
                        System.out.println("4");
                        System.out.println("Error: distance route is not valid");
                        System.exit(1);
                    }
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
