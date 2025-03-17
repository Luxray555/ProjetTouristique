package VRP;

import java.util.ArrayList;
import java.util.List;

public class Route {
    private int id;
    private List<Node> nodes;
    private double scoreTotal;
    private double distanceTotal;
    private double distanceMax;

    public Route(int id, double distanceMax){
        this.id = id;
        this.nodes = new ArrayList<>();
        this.scoreTotal = 0;
        this.distanceTotal = 0;
        this.distanceMax = distanceMax;
    }

    public void addFirst(Node node){
        this.nodes.add(0, node);
    }

    public void addLast(Node node){
        this.nodes.add(node);
    }

    public void addNode(int index, Node node){
        this.nodes.add(index, node);
    }

    public void removeNode(int index){
        this.nodes.remove(index);
    }

    public void removeFirst(){
        this.nodes.remove(0);
    }

    public void removeLast(){
        this.nodes.remove(this.nodes.size() - 1);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public double getScoreTotal() {
        return scoreTotal;
    }

    public void setScoreTotal(double scoreTotal) {
        this.scoreTotal = scoreTotal;
    }

    @Override
    public String toString() {
        return "Route{" +
                "id=" + id +
                ", nodes=" + nodes +
                ", scoreTotal=" + scoreTotal +
                '}';
    }
}
