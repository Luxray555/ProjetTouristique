package VRP.solution;

import VRP.Instance;
import VRP.Node;
import VRP.NodeType;
import VRP.Route;

import java.util.ArrayList;
import java.util.List;

public abstract class Solution {
    private List<Route> routes;
    private List<Node> nodes;
    private double score;

    public Solution(){
        nodes = new ArrayList<>();
        routes = new ArrayList<>();
        score = 0;
        for(int i = 0; i < Instance.getNbDestination(); i++){
            Node node = null;
            if(i <= 1 + Instance.getNbHotelSupp()){
                node = new Node(i, Instance.getScore(i), NodeType.HOTEL);
            }else{
                node = new Node(i, Instance.getScore(i), NodeType.SITE_TOURISTIQUE);
            }
            nodes.add(node);
        }
        for (int i = 0; i < Instance.getJours(); i++){
            routes.add(new Route(i, Instance.getDistanceMaxJour(i)));
        }
        routes.get(0).addFirst(nodes.get(0));
        routes.get(routes.size()-1).addLast(nodes.get(1));
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public abstract void construct();

    @Override
    public String toString() {
        return "Solution :" +
                routes.stream().map(Route::toString).reduce("", (a, b) -> a + "\n\t" + b) + "\n" +
                "Score :" + score;
    }
}
