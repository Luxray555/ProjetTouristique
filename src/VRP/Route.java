package VRP;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        node.addRoute(this);
        scoreTotal += node.getScore();
    }

    public void addLast(Node node){
        if (!nodes.isEmpty()) {
            distanceTotal += Instance.getDistance(nodes.get(nodes.size() - 1).getId(), node.getId());
        }
        this.nodes.add(node);
        node.addRoute(this);
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
            node2.addRoute(this);
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
            node2.addRoute(this);
            scoreTotal += node2.getScore();
        }
    }

    public void removeNode(Node node){
        int index = nodes.indexOf(node);
        if (index != -1) {
            if (index > 0) {
                distanceTotal -= Instance.getDistance(nodes.get(index - 1).getId(), node.getId());
            }
            if (index < nodes.size() - 1) {
                distanceTotal -= Instance.getDistance(node.getId(), nodes.get(index + 1).getId());
            }
            scoreTotal -= node.getScore();
            this.nodes.remove(node);
            node.removeRoute(this);
        }
    }

    public void removeFirst(){
        if (!this.nodes.isEmpty()) {
            if (nodes.size() > 1) {
                distanceTotal -= Instance.getDistance(nodes.get(0).getId(), nodes.get(1).getId());
            }
            scoreTotal -= nodes.get(0).getScore();
            Node node = nodes.get(0);
            this.nodes.remove(0);
            node.removeRoute(this);
        }
    }

    public void removeLast(){
        if (!this.nodes.isEmpty()) {
            if (nodes.size() > 1) {
                distanceTotal -= Instance.getDistance(nodes.get(nodes.size() - 2).getId(), nodes.get(nodes.size() - 1).getId());
            }
            scoreTotal -= nodes.get(nodes.size() - 1).getScore();
            Node node = nodes.get(nodes.size() - 1);
            this.nodes.remove(nodes.size() - 1);
            node.removeRoute(this);
        }
    }

    public Node getFirst(){
        return nodes.get(0);
    }

    public Node getLast(){
        return nodes.get(nodes.size() - 1);
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

    public double getDistanceMax(){
        return distanceMax;
    }

    @Override
    public String toString() {
        return "Jour " + id + " : " + nodes.stream().map(Node::toString).collect(Collectors.joining(" -> ")) + " | Score : " + scoreTotal + " | Score : " + scoreTotal + " | Distance : " + distanceTotal + " | DistanceMax : " + distanceMax;
    }
}
