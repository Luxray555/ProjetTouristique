package VRP.solution;

import VRP.*;

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

    protected void constructHotels(){
        this.recursiveHotels(routes.get(0));
    }

    protected boolean recursiveHotels(Route route){
        if (route.getId() == routes.size() - 1) {
            if(route.getDistanceTotal() <= route.getDistanceMax()){
                return true;
            }else{
                return false;
            }
        }
        List<HotelNode> orderedHotels = new ArrayList<>(this.hotels.stream().sorted((h1, h2) -> {
            double dist1 = Instance.getDistance(route.getHotelStart().getId(), h1.getId());
            double dist2 = Instance.getDistance(route.getHotelStart().getId(), h2.getId());
            return Double.compare(dist1, dist2);
        }).toList());
        orderedHotels.remove(route.getHotelStart());
        while (!orderedHotels.isEmpty()) {
            Result result = nearestHotel(route.getHotelStart(), orderedHotels);
            if (result.getDistance() <= route.getDistanceMax()) {
                if (routes.size() - 1 > route.getId()) {
                    route.setHotelEnd((HotelNode) result.getNode());
                    routes.get(route.getId() + 1).setHotelStart((HotelNode) result.getNode());
                    boolean verif = recursiveHotels(routes.get(route.getId() + 1));
                    if (verif) {
                        System.out.println("True : " + route.getId());
                        return true;
                    } else {
                        orderedHotels.remove(result.getNode());
                    }
                }
            }else{
                orderedHotels.remove(result.getNode());
            }
        }
        return false;
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

    protected Result nearestHotel(HotelNode node, List<HotelNode> hotels){
        Result result = new Result(null, Double.MAX_VALUE);
        for(HotelNode hotel : hotels){
            double dist = Instance.getDistance(node.getId(), hotel.getId());
            if(dist < result.getDistance()){
                result.setDistance(dist);
                result.setNode(hotel);
            }
        }
        if(result.getNode() == null){
            result.setNode(node);
        }
        return result;
    }

    public static class Result{
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
