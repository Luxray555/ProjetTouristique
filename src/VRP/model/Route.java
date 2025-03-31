package VRP.model;

import VRP.Instance;

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
        if(node == null) return;
        if (!sites.isEmpty()) {
            distanceTotal += Instance.getDistance(node.getId(), sites.get(0).getId());
            if(hotelStart != null) {
                distanceTotal += Instance.getDistance(hotelStart.getId(), node.getId());
                distanceTotal -= Instance.getDistance(hotelStart.getId(), sites.get(0).getId());
            }
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
        if(node == null) return;

        if (!sites.isEmpty()) {
            // Add distance from last site to new node
            distanceTotal += Instance.getDistance(sites.get(sites.size() - 1).getId(), node.getId());

            // Replace distance from last site to hotelEnd with node to hotelEnd
            if(hotelEnd != null) {
                distanceTotal -= Instance.getDistance(sites.get(sites.size() - 1).getId(), hotelEnd.getId());
                distanceTotal += Instance.getDistance(node.getId(), hotelEnd.getId());
            }
        } else {
            // Empty route case
            if(hotelStart != null && hotelEnd != null) {
                // Replace direct hotel-to-hotel distance with hotelStart->node->hotelEnd
                distanceTotal = Instance.getDistance(hotelStart.getId(), node.getId())
                        + Instance.getDistance(node.getId(), hotelEnd.getId());
            } else if (hotelStart != null) {
                distanceTotal = Instance.getDistance(hotelStart.getId(), node.getId());
            } else if (hotelEnd != null) {
                distanceTotal = Instance.getDistance(node.getId(), hotelEnd.getId());
            }
        }
        this.sites.add(node);
        node.addRoute(this);
        scoreTotal += node.getScore();
    }

    public void removeLast() {
        if (!sites.isEmpty()) {
            SiteNode node = sites.get(sites.size() - 1);

            if (sites.size() > 1) {
                // Remove distance from second-to-last to last node
                distanceTotal -= Instance.getDistance(sites.get(sites.size() - 2).getId(), node.getId());

                // Restore distance from second-to-last to hotelEnd
                if (hotelEnd != null) {
                    distanceTotal += Instance.getDistance(sites.get(sites.size() - 2).getId(), hotelEnd.getId());
                }
            } else {
                // Only one node - restore original hotel-to-hotel distance
                if (hotelStart != null && hotelEnd != null) {
                    distanceTotal = Instance.getDistance(hotelStart.getId(), hotelEnd.getId());
                } else {
                    distanceTotal = 0;
                }
            }

            scoreTotal -= node.getScore();
            this.sites.remove(node);
            node.removeRoute(this);
        }
    }

    public int removeSite(SiteNode node) {
        if (node == null || !sites.contains(node)) return -1;

        int index = sites.indexOf(node);
        if (index != -1) {
            if (index > 0) {
                distanceTotal -= Instance.getDistance(sites.get(index - 1).getId(), node.getId());
            } else {
                distanceTotal -= Instance.getDistance(hotelStart.getId(), node.getId());
            }
            if (index < sites.size() - 1) {
                distanceTotal -= Instance.getDistance(node.getId(), sites.get(index + 1).getId());
                if(index > 0) {
                    distanceTotal += Instance.getDistance(sites.get(index - 1).getId(), sites.get(index + 1).getId());
                }else{
                    distanceTotal += Instance.getDistance(hotelStart.getId(), sites.get(index + 1).getId());
                }
            } else {
                distanceTotal -= Instance.getDistance(node.getId(), hotelEnd.getId());
                distanceTotal += Instance.getDistance(hotelStart.getId(), hotelEnd.getId());
            }
            scoreTotal -= node.getScore();
            this.sites.remove(node);
            node.removeRoute(this);
        }
        return index;
    }

    public void addSite(SiteNode node, int index){
        if(node == null) return;
        if(index < 0 || index > sites.size()) return;
        if(index == 0){
            addFirst(node);
        }else if(index == sites.size()){
            addLast(node);
        }else{
            distanceTotal -= Instance.getDistance(sites.get(index - 1).getId(), sites.get(index).getId());
            distanceTotal += Instance.getDistance(sites.get(index - 1).getId(), node.getId());
            distanceTotal += Instance.getDistance(node.getId(), sites.get(index).getId());
            scoreTotal += node.getScore();
            this.sites.add(index, node);
            node.addRoute(this);
        }
    }

    public HotelNode getHotelStart() {
        return hotelStart;
    }

    public void setHotelStart(HotelNode hotelStart) {
        if(!sites.isEmpty()) {
            distanceTotal -= Instance.getDistance(this.hotelStart.getId(), sites.get(0).getId());
            this.hotelStart = hotelStart;
            distanceTotal += Instance.getDistance(this.hotelStart.getId(), sites.get(0).getId());
        }else{
            this.hotelStart = hotelStart;
            if(this.hotelEnd != null && this.hotelStart != null){
                distanceTotal = Instance.getDistance(this.hotelStart.getId(), this.hotelEnd.getId());
            }
        }
    }

    public HotelNode getHotelEnd() {
        return hotelEnd;
    }

    public void setHotelEnd(HotelNode hotelEnd) {
        if(!sites.isEmpty()) {
            distanceTotal -= Instance.getDistance(sites.get(sites.size() - 1).getId(), this.hotelEnd.getId());
            this.hotelEnd = hotelEnd;
            distanceTotal += Instance.getDistance(sites.get(sites.size() - 1).getId(), this.hotelEnd.getId());
        }else{
            this.hotelEnd = hotelEnd;
            if(this.hotelStart != null && this.hotelEnd != null){
                distanceTotal = Instance.getDistance(this.hotelStart.getId(), this.hotelEnd.getId());
            }
        }
    }

    public boolean checkDistanceLast(SiteNode node){
        if(!sites.isEmpty()){
            return distanceTotal - Instance.getDistance(sites.get(sites.size() - 1).getId(), hotelEnd.getId()) + Instance.getDistance(sites.get(sites.size() - 1).getId(), node.getId()) + Instance.getDistance(node.getId(), hotelEnd.getId()) <= distanceMax;
        }else{
            return Instance.getDistance(hotelStart.getId(), node.getId()) + Instance.getDistance(node.getId(), hotelEnd.getId()) <= distanceMax;
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
        }else{
            return hotelStart;
        }
    }

    public Node getNext(SiteNode node){
        int index = sites.indexOf(node);
        if(index != -1 && index < sites.size() - 1){
            return sites.get(index + 1);
        }else{
            return hotelEnd;
        }
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
