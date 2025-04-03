package VRP.solution;

import VRP.*;
import VRP.Movement.Exchange;
import VRP.Movement.Relocate;
import VRP.checker.Checker;
import VRP.model.HotelNode;
import VRP.model.Node;
import VRP.model.Route;
import VRP.model.SiteNode;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    protected List<Route> routes;
    protected List<Node> nodes;
    protected List<SiteNode> sites;
    protected List<HotelNode> hotels;
    protected int score;

    public Solution(){
        nodes = new ArrayList<>();
        routes = new ArrayList<>();
        sites = new ArrayList<>();
        hotels = new ArrayList<>();
        score = 0;
        for(int i = 0; i < Instance.getNbDestination(); i++){
            if(i <= 1 + Instance.getNbHotelSupp()){
                HotelNode node = new HotelNode(i, Instance.getScore(i));
                hotels.add(node);
                nodes.add(node);
            }else{
                SiteNode node = new SiteNode(i, Instance.getScore(i));
                sites.add(node);
                nodes.add(node);
            }
        }
        for (int i = 0; i < Instance.getJours(); i++){
            routes.add(new Route(i, null, null, Instance.getDistanceMaxJour(i)));
        }
        routes.get(0).setHotelStart(hotels.get(0));
        routes.get(routes.size()-1).setHotelEnd(hotels.get(1));
    }

    public void evaluate(){
        score = 0;
        for(Route route : routes){
            score += route.getScoreTotal();
        }
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public List<SiteNode> getSites() {
        return sites;
    }

    public SiteNode getSiteWithId(int i){
        return sites.get(i - Instance.getNbHotel());
    }

    public List<HotelNode> getHotels() {
        return hotels;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void construct(){

    }

    public void solveILS(){
        Solution best = this.copy();
        for(int i = 0; i < 1000; i++){
            Solution solution = best.copy();
            deconstructSolution(20);
            solution.solveVND();
            if(solution.getScore() > best.getScore()){
                best = solution;
            }
        }
        this.routes = best.getRoutes();
        this.score = best.getScore();
        Checker.checkSolution(this);
    }

    private void deconstructSolution(int percentage){
        for(Route route : this.routes){
            int size = route.getSites().size();
            for(int i = 0; i < size*(percentage/100); i++){
                int index = (int)(Math.random() * route.getSites().size());
                SiteNode site = route.getSites().get(index);
                route.removeSite(site);
            }
        }
        this.evaluate();
    }

    public void solveTS(){

    }

    public void solveVND(){
        boolean solved = false;
        Exchange exchange = new Exchange();
        Relocate relocate = new Relocate();
        int i = 0;
        while(!solved){
            switch(i){
                case 0: {
                    boolean verif = exchange.applyBestImprovement(this);
                    if(!verif){
                        i++;
                    }

                    break;
                }
                case 1: {
                    boolean verif = relocate.applyBestImprovement(this);
                    if(!verif){
                        solved = true;
                    }else{
                        i = 0;
                    }
                    break;
                }
            }
        }
    }

    public void solveLNS(){

    }

    public void solveVNS(){
    }

    protected void resetLinks(){
        for(Node node : nodes){
            node.removeAllRoutes();
        }
        for(Route route : this.routes){
            route.getHotelStart().addRoute(route);
            route.getHotelEnd().addRoute(route);
            for(SiteNode site : route.getSites()){
                site.addRoute(route);
            }
        }
    }

    private void setSolution(Solution solution){
        List<Route> routes = new ArrayList<>();
        for(Route route : solution.getRoutes()){
            Route newRoute = new Route(route.getId(), hotels.get(route.getHotelStart().getId()), hotels.get(route.getHotelEnd().getId()), route.getDistanceMax());
            for(SiteNode site : route.getSites()){
                newRoute.addLast(getSiteWithId(site.getId()));
            }
            routes.add(newRoute);
        }
        this.routes = routes;
        this.score = solution.getScore();
    }

    @Override
    public String toString() {
        return "Solution :" +
                routes.stream().map(Route::toString).reduce("", (a, b) -> a + "\n\t" + b) + "\n" +
                "Score : " + score;
    }

    public Solution copy(){
        Solution solution = new Solution();
        solution.setScore(score);
        List<Route> newRoutes = new ArrayList<>();
        for(Route route : routes){
            Route newRoute = new Route(route.getId(), solution.getHotels().get(route.getHotelStart().getId()), solution.getHotels().get(route.getHotelEnd().getId()), route.getDistanceMax());
            for(SiteNode site : route.getSites()){
                newRoute.addLast(solution.getSiteWithId(site.getId()));
            }
            newRoutes.add(newRoute);
        }
        solution.setRoutes(newRoutes);
        System.out.println(Checker.checkSolution(solution));
        return solution;
    }
}
