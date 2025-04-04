package VRP.model;

import java.util.ArrayList;
import java.util.List;

public abstract class Node {
    protected int id;
    protected List<Route> routes;
    protected int score;

    public Node(int id, int score) {
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

    public Route getRoute(int i){
        if(i >=0 && i < routes.size()){
            return routes.get(i);
        }
        return null;
    }

    public void setRoutes(Route... routes) {
        this.routes = new ArrayList<>();
        this.routes.addAll(List.of(routes));
    }

    public void addRoute(Route route) {
        this.routes.add(route);
    }

    public void removeRoute(Route route) {
        Route ru = this.routes.stream().filter(r -> r.getId() == route.getId()).findFirst().orElse(null);
        if (ru != null) {
            this.routes.remove(ru);
        }
    }

    public void removeAllRoutes(){
        routes.clear();
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
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
