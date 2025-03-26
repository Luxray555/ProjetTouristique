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

    private void constructSites(){
        List<Node> visited = new ArrayList<>();
        for(int i = 0; i < routes.size(); i++){
            Route route = routes.get(i);
            Node current = route.getHotelStart();
            while(visited.size() < sites.size() && current != null){
                Result next = nearestSite(current, route.getHotelEnd(), visited);
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

    private void constructHotels(){
        for(int i = 0; i < routes.size() - 1; i++){
            Route route = routes.get(i);
            Node current = route.getHotelStart();
            Result next = satisfiedHotel(current);
            route.setHotelEnd((HotelNode)next.getNode());
            if(routes.size() - 1 > i){
                routes.get(i+1).setHotelStart((HotelNode)next.getNode());
            }
        }
    }

    private Result satisfiedHotel(Node node){
        Result result = new Result(null, 0);
        for(Node hotel : hotels){
            double dist = Instance.getDistance(node.getId(), hotel.getId());
            double distanceLast = Instance.getDistance(hotel.getId(), hotels.get(1).getId());
            if(dist > result.getDistance() && distanceLast < routes.get(routes.size() - 1).getDistanceMax()){
                result.setDistance(dist);
                result.setNode(hotel);
            }
        }
        if (result.getNode() == null){
            result = nearestHotel(node);
        }
        return result;
    }

    private Result nearestHotel(Node node){
        Result result = new Result(null, Double.MAX_VALUE);
        for(Node hotel : hotels){
            double dist = Instance.getDistance(node.getId(), hotel.getId());
            if(dist < result.getDistance()){
                result.setDistance(dist);
                result.setNode(hotel);
            }
        }
        return result;
    }

    private Result nearestSite(Node node, Node EndHotel, List<Node> visited){
        Result result = new Result(null, Double.MAX_VALUE);
        for(Node site : sites){
            if(!visited.contains(site)){
                double dist = Instance.getDistance(node.getId(), site.getId());
                double distanceLast = Instance.getDistance(site.getId(), EndHotel.getId());
                if(dist < result.getDistance() && distanceLast < routes.get(routes.size() - 1).getDistanceMax()){
                    result.setDistance(dist);
                    result.setNode(site);
                }
            }
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
