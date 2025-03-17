package VRP;

public class Node {
    private int id;
    private Route route;
    private double score;

    public Node(int id, Route route, double score){
        this.id = id;
        this.route = route;
        this.score = score;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", route=" + route +
                ", score=" + score +
                '}';
    }
}
