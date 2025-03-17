package VRP;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    private List<Route> routes;
    private List<Node> nodes;
    private double score;

    public Solution(){
        nodes = new ArrayList<>();
        routes = new ArrayList<>();
        score = 0;
        for(int i = 0; i < Instance.getNbDestination(); i++){
            nodes.add(new Node(i, null, Instance.getScore(i)));
        }
        for (int i = 0; i < Instance.getJours(); i++){
            routes.add(new Route(i, Instance.getDistanceMaxJour(i)));
        }
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
}
