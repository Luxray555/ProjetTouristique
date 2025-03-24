package VRP.solution;

import VRP.Instance;
import VRP.Node;
import VRP.NodeType;
import VRP.Route;

import java.util.ArrayList;
import java.util.List;

public abstract class Solution {
    protected List<Route> routes;
    protected List<Node> nodes;
    protected List<Node> sitesTouristrique;
    protected List<Node> hotels;
    protected double score;

    public Solution(){
        nodes = new ArrayList<>();
        routes = new ArrayList<>();
        sitesTouristrique = new ArrayList<>();
        hotels = new ArrayList<>();
        score = 0;
        for(int i = 0; i < Instance.getNbDestination(); i++){
            Node node = null;
            if(i <= 1 + Instance.getNbHotelSupp()){
                node = new Node(i, Instance.getScore(i), NodeType.HOTEL);
                hotels.add(node);
            }else{
                node = new Node(i, Instance.getScore(i), NodeType.SITE_TOURISTIQUE);
                sitesTouristrique.add(node);
            }
            nodes.add(node);
        }
        for (int i = 0; i < Instance.getJours(); i++){
            routes.add(new Route(i, Instance.getDistanceMaxJour(i)));
        }
        routes.get(0).addFirst(nodes.get(0));
    }

    public void evaluate(){
        score = 0;
        for(Route route : routes){
            score += route.getScoreTotal();
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

    public abstract void construct();

    public void solveILS(){

    }

    public void solveTS(){

    }

    public void solveVND(){

    }

    public void solveLNS(){

    }

    public void solveVNS(){

    }

    @Override
    public String toString() {
        return "Solution :" +
                routes.stream().map(Route::toString).reduce("", (a, b) -> a + "\n\t" + b) + "\n" +
                "Score : " + score;
    }
}
