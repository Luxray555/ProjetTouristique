package VRP.solution;

import VRP.Instance;
import VRP.Node;
import VRP.NodeType;
import VRP.Route;

import java.util.ArrayList;
import java.util.List;

public class NearestNeighborSolution extends Solution{
    public NearestNeighborSolution(){
        super();
    }

    @Override
    public void construct() {
        List<Node> visited = new ArrayList<>();
        for (int i = 0; i < routes.size(); i++){
            Route route = routes.get(i);
            do{
                System.out.println(getRoutes());
                NearestResult nearSite = nearestSite(route.getLast(), visited);
                if(route.getDistanceTotal() + nearSite.getDistance() < route.getDistanceMax()){
                    NearestResult toHotel = routes.size() - 1 > i ? nearestHotel(route.getLast()) : finalHotel(route.getLast());
                    if(route.getDistanceTotal() + nearSite.getDistance() + toHotel.getDistance() < route.getDistanceMax()){
                        route.addLast(nearSite.getNode());
                        visited.add(nearSite.getNode());
                    }else{
                        if(routes.size()-1 > i){
                            route.addLast(nearestHotel(route.getLast()).getNode());
                            routes.get(i+1).addFirst(route.getLast());
                        }else{
                            route.addLast(hotels.get(1));
                        }
                    }
                }else{
                    if(routes.size()-1 > i){
                        route.addLast(nearestHotel(route.getLast()).getNode());
                        routes.get(i+1).addFirst(route.getLast());
                    }else{
                        route.addLast(hotels.get(1));
                    }
                }

            }while(route.getLast().getType() != NodeType.HOTEL);
        }
        this.evaluate();
    }

    private NearestResult nearestHotel(Node node){
        NearestResult result = new NearestResult(null, Double.MAX_VALUE);
        for(Node hotel : hotels){
            double dist = Instance.getDistance(node.getId(), hotel.getId());
            if(dist < result.getDistance()){
                result.setDistance(dist);
                result.setNode(hotel);
            }
        }
        return result;
    }

    private NearestResult finalHotel(Node node){
        Node lastHotel = hotels.get(1);
        return new NearestResult(lastHotel, Instance.getDistance(node.getId(), lastHotel.getId()));
    }

    private NearestResult nearestSite(Node node, List<Node> visited){
        NearestResult result = new NearestResult(null, Double.MAX_VALUE);
        for(Node site : sitesTouristrique){
            if(!visited.contains(site)){
                double dist = Instance.getDistance(node.getId(), site.getId());
                if(dist < result.getDistance()){
                    result.setDistance(dist);
                    result.setNode(site);
                }
                }
        }
        return result;
    }

    public static class NearestResult{
        private double distance;
        private Node node;

        public NearestResult(Node node, double distance){
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
