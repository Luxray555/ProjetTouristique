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

    private void constructHotels(){
        this.recursiveHotels(routes.get(0));
    }

    private boolean recursiveHotels(Route route){
        List<HotelNode> orderedHotels = new ArrayList<>(this.hotels.stream().sorted((h1, h2) -> {
            double dist1 = Instance.getDistance(route.getHotelStart().getId(), h1.getId());
            double dist2 = Instance.getDistance(route.getHotelStart().getId(), h2.getId());
            return Double.compare(dist2, dist1);
        }).toList());
        orderedHotels.remove(route.getHotelStart());
        double mid = route.getDistanceMax() / 1.5;
        for(Node hotel : orderedHotels){
            double dist = Instance.getDistance(route.getHotelStart().getId(), hotel.getId());
            if(dist <= route.getDistanceMax() && dist <= mid){
                if(routes.size() - 1 > route.getId()){
                    route.setHotelEnd((HotelNode)hotel);
                    routes.get(route.getId() + 1).setHotelStart((HotelNode)hotel);
                    boolean verif = recursiveHotels(routes.get(route.getId() + 1));
                    if(verif){
                        return true;
                    }
                }else if(hotel.getId() == hotels.get(1).getId()) return true;
            }else{
                if(routes.size() - 1 > route.getId()){
                    routes.get(route.getId() + 1).setHotelStart(route.getHotelStart());
                    route.setHotelEnd(route.getHotelStart());
                }else{
                    route.setHotelEnd(route.getHotelStart());
                }
            }
        }
        return false;
    }

    private Result nearestSite(Node node, Route route, List<Node> visited){
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
