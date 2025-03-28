package VRP;

import java.util.ArrayList;
import java.util.List;

public abstract class Node {
    protected int id;
    protected List<Route> routes;
    protected double score;

    public Node(int id, double score) {
        this.id = id;
        this.score = score;
        this.routes = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(Route... routes) {
        this.routes = new ArrayList<>();
        this.routes.addAll(List.of(routes));
    }

    public void addRoute(Route route) {
        this.routes.add(route);
    }

    public void removeRoute(Route route) {
        this.routes.remove(route);
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Node");
        sb.append(id);
        return sb.toString();
    }
}
