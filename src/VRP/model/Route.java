package VRP.model;

import VRP.Instance;
import VRP.checker.Checker;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Route {
    private int id;
    private HotelNode hotelStart;
    private HotelNode hotelEnd;
    private List<SiteNode> sites;
    private int scoreTotal;
    private double distanceTotal;
    private double distanceMax;

    public Route(int id, HotelNode hotelStart, HotelNode hotelEnd, double distanceMax) {
        this.id = id;
        this.hotelStart = hotelStart;
        if(hotelStart != null){
            hotelStart.addRoute(this);
        }
        this.hotelEnd = hotelEnd;
        if(hotelEnd != null){
            hotelEnd.addRoute(this);
        }
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
            if(hotelStart != null && hotelEnd != null) {
                distanceTotal = Instance.getDistance(hotelStart.getId(), node.getId())
                        + Instance.getDistance(node.getId(), hotelEnd.getId());
            }else if(hotelStart != null){
                distanceTotal = Instance.getDistance(hotelStart.getId(), node.getId());
            }else if(hotelEnd != null){
                distanceTotal = Instance.getDistance(hotelEnd.getId(), node.getId());
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
                // Enlever la distance entre l'avant-dernier et le dernier site
                distanceTotal -= Instance.getDistance(sites.get(sites.size() - 2).getId(), node.getId());

                // Restaurer la distance entre l'avant-dernier et l'hôtel de fin
                if (hotelEnd != null) {
                    distanceTotal -= Instance.getDistance(node.getId(), hotelEnd.getId());
                    distanceTotal += Instance.getDistance(sites.get(sites.size() - 2).getId(), hotelEnd.getId());
                }
            } else {
                // Si on retire le seul site, restaurer la distance hôtel-départ <-> hôtel-fin
                if (hotelStart != null && hotelEnd != null) {
                    distanceTotal = Instance.getDistance(hotelStart.getId(), hotelEnd.getId());
                } else {
                    distanceTotal = 0;
                }
            }

            scoreTotal -= node.getScore();

            node.removeRoute(this);
            this.sites.remove(node);
        }
    }

    public void removeFirst() {
        if (!sites.isEmpty()) {
            SiteNode node = sites.get(0);

            if (sites.size() > 1) {
                // Enlever la distance entre le premier et le deuxième site
                distanceTotal -= Instance.getDistance(node.getId(), sites.get(1).getId());

                // Restaurer la distance entre l'hôtel de départ et le deuxième site
                if (hotelStart != null) {
                    distanceTotal -= Instance.getDistance(hotelStart.getId(), node.getId());
                    distanceTotal += Instance.getDistance(hotelStart.getId(), sites.get(1).getId());
                }
            } else {
                // Si on retire le seul site, restaurer la distance hôtel-départ <-> hôtel-fin
                if (hotelStart != null && hotelEnd != null) {
                    distanceTotal = Instance.getDistance(hotelStart.getId(), hotelEnd.getId());
                } else {
                    distanceTotal = 0;
                }
            }

            scoreTotal -= node.getScore();

            node.removeRoute(this);
            this.sites.remove(node);
        }
    }


    public int removeSite(SiteNode node) {
        if (node == null || !sites.contains(node)) return -1;
        int index = sites.indexOf(node);
        if(index != -1){
            if(index == 0){
                removeFirst();
            }else if(index == sites.size() - 1){
                removeLast();
            }else{
                distanceTotal -= Instance.getDistance(sites.get(index - 1).getId(), node.getId());
                distanceTotal -= Instance.getDistance(node.getId(), sites.get(index + 1).getId());
                distanceTotal += Instance.getDistance(sites.get(index - 1).getId(), sites.get(index + 1).getId());
                scoreTotal -= node.getScore();
                node.removeRoute(this);
                this.sites.remove(node);
            }
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
            distanceTotal += Instance.getDistance(hotelStart.getId(), sites.get(0).getId());
        }else{
            if(this.hotelEnd != null){
                distanceTotal = Instance.getDistance(hotelStart.getId(), this.hotelEnd.getId());
            }else{
                distanceTotal = 0;
            }
        }
        if (this.hotelStart != null) {
            this.hotelStart.removeRoute(this);
        }
        hotelStart.addRoute(this);
        this.hotelStart = hotelStart;
    }

    public HotelNode getHotelEnd() {
        return hotelEnd;
    }

    public void setHotelEnd(HotelNode hotelEnd) {
        if(!sites.isEmpty()) {
            distanceTotal -= Instance.getDistance(sites.get(sites.size() - 1).getId(), this.hotelEnd.getId());
            distanceTotal += Instance.getDistance(sites.get(sites.size() - 1).getId(), hotelEnd.getId());
        }else{
            if(this.hotelStart != null){
                distanceTotal = Instance.getDistance(this.hotelStart.getId(), hotelEnd.getId());
            }else{
                distanceTotal = 0;
            }
        }
        if(this.hotelEnd != null){
            this.hotelEnd.removeRoute(this);
        }
        hotelEnd.addRoute(this);
        this.hotelEnd = hotelEnd;

    }

    public void removeHotelEnd(){
        if(hotelEnd != null){
            if(!sites.isEmpty()){
                distanceTotal -= Instance.getDistance(sites.get(sites.size() - 1).getId(), hotelEnd.getId());
            }else{
                distanceTotal = 0;
            }
            hotelEnd.removeRoute(this);
            hotelEnd = null;
        }
    }

    public void removeHotelStart(){
        if(hotelStart != null){
            if(!sites.isEmpty()){
                distanceTotal -= Instance.getDistance(hotelStart.getId(), sites.get(0).getId());
            }else{
                distanceTotal = 0;
            }
            hotelStart.removeRoute(this);
            hotelStart = null;
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

    public int getScoreTotal() {
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
