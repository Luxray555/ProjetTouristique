package VRP;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private int id;
    private List<Route> routes;
    private double score;
    private NodeType type;

    public Node(int id, double score, NodeType type) {
        this.id = id;
        this.score = score;
        this.routes = new ArrayList<>();
        this.type = type;
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
        if(type == NodeType.SITE_TOURISTIQUE && routes.length > 1) return;
        this.routes.addAll(List.of(routes));
    }

    public void addRoute(Route route) {
        if(type == NodeType.SITE_TOURISTIQUE && !this.routes.isEmpty()) return;
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
        switch (type) {
            case SITE_TOURISTIQUE:
                sb.append("SiteTour");
                break;
            case HOTEL:
                sb.append("Hotel");
                break;
        }
        sb.append(id);
        return sb.toString();
    }

    public NodeType getType(){
        return type;
    }
}
