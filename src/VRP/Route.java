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
        if (!nodes.isEmpty()) {
            distanceTotal += Instance.getDistance(node.getId(), nodes.get(0).getId());
        }
        this.nodes.add(0, node);
        scoreTotal += node.getScore();
    }

    public void addLast(Node node){
        if (!nodes.isEmpty()) {
            distanceTotal += Instance.getDistance(nodes.get(nodes.size() - 1).getId(), node.getId());
        }
        this.nodes.add(node);
        scoreTotal += node.getScore();
    }

    public void addAfter(Node node1, Node node2){
        int index = nodes.indexOf(node1);
        if (index != -1) {
            if (index < nodes.size() - 1) {
                distanceTotal -= Instance.getDistance(node1.getId(), nodes.get(index + 1).getId());
                distanceTotal += Instance.getDistance(node1.getId(), node2.getId());
                distanceTotal += Instance.getDistance(node2.getId(), nodes.get(index + 1).getId());
            }else{
                distanceTotal += Instance.getDistance(node1.getId(), node2.getId());
            }
            this.nodes.add(index + 1, node2);
            scoreTotal += node2.getScore();
        }
    }

    public void addBefore(Node node1, Node node2){
        int index = nodes.indexOf(node1);
        if (index != -1) {
            if (index > 0) {
                distanceTotal -= Instance.getDistance(nodes.get(index - 1).getId(), node1.getId());
                distanceTotal += Instance.getDistance(nodes.get(index - 1).getId(), node2.getId());
                distanceTotal += Instance.getDistance(node2.getId(), node1.getId());
            }else{
                distanceTotal += Instance.getDistance(node2.getId(), node1.getId());
            }
            this.nodes.add(index, node2);
            scoreTotal += node2.getScore();
        }
    }

    public void removeNode(int index){
        if (index > 0 && index < nodes.size() - 1) {
            distanceTotal -= Instance.getDistance(nodes.get(index - 1).getId(), nodes.get(index).getId());
            distanceTotal -= Instance.getDistance(nodes.get(index).getId(), nodes.get(index + 1).getId());
            distanceTotal += Instance.getDistance(nodes.get(index - 1).getId(), nodes.get(index + 1).getId());
        }
        scoreTotal -= nodes.get(index).getScore();
        this.nodes.remove(index);
    }

    public void removeFirst(){
        if (!this.nodes.isEmpty()) {
            if (nodes.size() > 1) {
                distanceTotal -= Instance.getDistance(nodes.get(0).getId(), nodes.get(1).getId());
            }
            scoreTotal -= nodes.get(0).getScore();
            this.nodes.remove(0);
        }
    }

    public void removeLast(){
        if (!this.nodes.isEmpty()) {
            if (nodes.size() > 1) {
                distanceTotal -= Instance.getDistance(nodes.get(nodes.size() - 2).getId(), nodes.get(nodes.size() - 1).getId());
            }
            scoreTotal -= nodes.get(nodes.size() - 1).getScore();
            this.nodes.remove(this.nodes.size() - 1);
        }
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
        this.scoreTotal = nodes.stream().mapToDouble(Node::getScore).sum();
        this.distanceTotal = 0;
        for (int i = 0; i < nodes.size() - 1; i++) {
            this.distanceTotal += Instance.getDistance(nodes.get(i).getId(), nodes.get(i + 1).getId());
        }
    }

    public double getScoreTotal() {
        return scoreTotal;
    }

    public double getDistanceTotal() {
        return distanceTotal;
    }

    @Override
    public String toString() {
        return "Route{" +
                "id=" + id +
                ", nodes=" + nodes +
                ", scoreTotal=" + scoreTotal +
                ", distanceTotal=" + distanceTotal +
                '}';
    }
}
