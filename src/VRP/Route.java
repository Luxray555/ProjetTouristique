package VRP;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Route {
    private int id;
    private HotelNode hotelStart;
    private HotelNode hotelEnd;
    private List<SiteNode> sites;
    private double scoreTotal;
    private double distanceTotal;
    private double distanceMax;

    public Route(int id, HotelNode hotelStart, HotelNode hotelEnd, double distanceMax) {
        this.id = id;
        this.hotelStart = hotelStart;
        this.hotelEnd = hotelEnd;
        this.sites = new ArrayList<>();
        this.scoreTotal = 0;
        if(hotelStart != null && hotelEnd != null){
            this.distanceTotal = Instance.getDistance(hotelStart.getId(), hotelEnd.getId());
        }else{
            this.distanceTotal = 0;
        }
        this.distanceMax = distanceMax;
    }

    public void addFirst(SiteNode node) {
        if (!sites.isEmpty()) {
            distanceTotal += Instance.getDistance(node.getId(), sites.get(0).getId());
        } else {
            if(hotelStart != null){
                distanceTotal += Instance.getDistance(hotelStart.getId(), node.getId());
                if(hotelEnd != null){
                    distanceTotal += Instance.getDistance(node.getId(), hotelEnd.getId());
                    distanceTotal -= Instance.getDistance(hotelStart.getId(), hotelEnd.getId());
                }
            }
        }
        this.sites.add(0, node);
        node.addRoute(this);
        scoreTotal += node.getScore();
    }

    public void addLast(SiteNode node) {
        if (!sites.isEmpty()) {
            distanceTotal += Instance.getDistance(sites.get(sites.size() - 1).getId(), node.getId());
        } else {
            if(hotelEnd != null){
                distanceTotal += Instance.getDistance(hotelStart.getId(), node.getId());
                distanceTotal += Instance.getDistance(node.getId(), hotelEnd.getId());
                distanceTotal -= Instance.getDistance(hotelStart.getId(), hotelEnd.getId());
            }
        }
        this.sites.add(node);
        node.addRoute(this);
        scoreTotal += node.getScore();
    }

    public void removeSite(SiteNode node) {
        if (node == null || !sites.contains(node)) return;

        int index = sites.indexOf(node);
        if (index != -1) {
            if (index > 0) {
                distanceTotal -= Instance.getDistance(sites.get(index - 1).getId(), node.getId());
            } else {
                distanceTotal -= Instance.getDistance(hotelStart.getId(), node.getId());
            }
            if (index < sites.size() - 1) {
                distanceTotal -= Instance.getDistance(node.getId(), sites.get(index + 1).getId());
                distanceTotal += Instance.getDistance(sites.get(index - 1).getId(), sites.get(index + 1).getId());
            } else {
                distanceTotal -= Instance.getDistance(node.getId(), hotelEnd.getId());
                distanceTotal += Instance.getDistance(hotelStart.getId(), hotelEnd.getId());
            }
            scoreTotal -= node.getScore();
            this.sites.remove(node);
            node.removeRoute(this);
        }
    }

    public Node getHotelStart() {
        return hotelStart;
    }

    public void setHotelStart(HotelNode hotelStart) {
        if(!sites.isEmpty()) {
            distanceTotal -= Instance.getDistance(this.hotelStart.getId(), sites.get(0).getId());
            this.hotelStart = hotelStart;
            distanceTotal += Instance.getDistance(this.hotelStart.getId(), sites.get(0).getId());
        }else{
            this.hotelStart = hotelStart;
            if(this.hotelEnd != null){
                distanceTotal = Instance.getDistance(this.hotelStart.getId(), this.hotelEnd.getId());
            }
        }
    }

    public Node getHotelEnd() {
        return hotelEnd;
    }

    public void setHotelEnd(HotelNode hotelEnd) {
        if(!sites.isEmpty()) {
            distanceTotal -= Instance.getDistance(sites.get(sites.size() - 1).getId(), this.hotelEnd.getId());
            this.hotelEnd = hotelEnd;
            distanceTotal += Instance.getDistance(sites.get(sites.size() - 1).getId(), this.hotelEnd.getId());
        }else{
            this.hotelEnd = hotelEnd;
            if(this.hotelStart != null){
                distanceTotal = Instance.getDistance(this.hotelStart.getId(), this.hotelEnd.getId());
            }
        }
    }

    public void replaceSite(SiteNode node, SiteNode newNode){
        if(sites.contains(node)){
            int index = sites.indexOf(node);
            if(index > 0){
                distanceTotal -= Instance.getDistance(sites.get(index - 1).getId(), node.getId());
                distanceTotal += Instance.getDistance(sites.get(index - 1).getId(), newNode.getId());
            }else{
                distanceTotal -= Instance.getDistance(hotelStart.getId(), node.getId());
                distanceTotal += Instance.getDistance(hotelStart.getId(), newNode.getId());
            }
            if(index < sites.size() - 1){
                distanceTotal -= Instance.getDistance(node.getId(), sites.get(index + 1).getId());
                distanceTotal += Instance.getDistance(newNode.getId(), sites.get(index + 1).getId());
            }else{
                distanceTotal -= Instance.getDistance(node.getId(), hotelEnd.getId());
                distanceTotal += Instance.getDistance(newNode.getId(), hotelEnd.getId());
            }
            scoreTotal -= node.getScore();
            scoreTotal += newNode.getScore();
            sites.set(index, newNode);
            node.removeRoute(this);
            newNode.addRoute(this);
        }
    }

    public Node getLast() {
        if(!sites.isEmpty()){
            return sites.get(sites.size() - 1);
        }
        return null;
    }

    public Node getFirst() {
        if(!sites.isEmpty()){
            return sites.get(0);
        }
        return null;
    }

    public Node getPrevious(SiteNode node){
        int index = sites.indexOf(node);
        if(index > 0){
            return sites.get(index - 1);
        }
        return null;
    }

    public Node getNext(SiteNode node){
        int index = sites.indexOf(node);
        if(index != -1 && index < sites.size() - 1){
            return sites.get(index + 1);
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public List<SiteNode> getSites() {
        return sites;
    }

    public double getScoreTotal() {
        return scoreTotal;
    }

    public double getDistanceTotal() {
        return distanceTotal;
    }

    public double getDistanceMax() {
        return distanceMax;
    }

    @Override
    public String toString() {
        return "Jour " + id + " : " + hotelStart + " -> " + sites.stream().map(Node::toString).collect(Collectors.joining(" -> ")) + " -> " + hotelEnd +
                " | Score : " + scoreTotal + " | Distance : " + distanceTotal + " | DistanceMax : " + distanceMax;
    }
}
