package VRP.solution;

import VRP.*;
import VRP.Movement.Exchange;
import VRP.Movement.ExchangeHotel;
import VRP.Movement.Relocate;
import VRP.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

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

    public List<SiteNode> getSites() { return sites;
    }

    public SiteNode getSiteWithId(int i){ return sites.get(i - Instance.getNbHotel());
    }

    public List<HotelNode> getHotels() { return hotels;
    }

    public void setRoutes(List<Route> routes) { this.routes = routes;
    }

    public int getScore() { return score;
    }

    public void setScore(int score) { this.score = score;
    }

    public void construct(){}

    public Solution solveILS(Function<Solution, Solution> localSearchMethod, int iterations){
        Solution best = this.copy();
        Solution current = this.copy();
        List<Pair> tabuList = new ArrayList<>();
        ExchangeHotel exchangeHotel = new ExchangeHotel();
        for(int i = 0; i < iterations; i++){
            current = localSearchMethod.apply(current);
            if(current.getScore() > best.getScore()){
                best = current.copy();
            }
            exchangeHotel.applyTS(current, tabuList, this.getHotels().size(), best.getScore());
            current.deconstructSolution(50);
        }
        return best;
    }

    private void deconstructSolution(int percentage){
        for(Route route : this.routes){
            int size = route.getSites().size();
            for(int i = 0; i < size*(percentage/100.0); i++){
                int index = (int)(Math.random() * route.getSites().size());
                SiteNode site = route.getSites().get(index);
                route.removeSite(site);
            }
        }
        this.evaluate();
    }

    public Solution solveVND(){
        boolean solved = false;
        ExchangeHotel exchangeHotel = new ExchangeHotel();
        Exchange exchange = new Exchange();
        Relocate relocate = new Relocate();
        Solution solution = this.copy();
        Solution best = this.copy();
        List<Pair> tabuListHotel = new ArrayList<>();
        List<Pair> tabuList = new ArrayList<>();
        int i = 0;
        while(!solved){
            switch(i){
                case 0: {
                    for(int j = 0; j < 50 ; j++){
                        boolean verif = exchange.applyTS(solution, tabuList, 20, best.getScore());
                        if(!verif){
                            break;
                        }else{
                            if(solution.getScore() > best.getScore()){
                                best = solution.copy();
                            }
                        }
                    }
                    i++;
                    break;
                }
                case 1: {
                    boolean verif = relocate.applyBestImprovement(solution);
                    if(!verif){
                        i++;
                    }else{
                        if(solution.getScore() > best.getScore()){
                            best = solution.copy();
                        }
                        i = 0;
                    }
                    break;
                }
                case 2: {
                    boolean verif = exchangeHotel.applyTS(solution, tabuListHotel, (int)Math.pow(2, solution.getHotels().size()), best.getScore());
                    if(!verif){
                        solved = true;
                    }else{
                        solution.deconstructSolution(20);
                        while(relocate.applyBestImprovement(solution)){
                            if(solution.getScore() > best.getScore()){
                                best = solution.copy();
                            }
                        }
                        i = 0;
                    }
                    break;
                }
            }
        }
        return best;
    }

    public Solution solveVNS(){
        Exchange exchange = new Exchange();
        Relocate relocate = new Relocate();
        ExchangeHotel exchangeHotel = new ExchangeHotel();

        Solution best = this.copy();
        Solution current = this.copy();
        int k = 0;
        int kMax = 3;

        while(k < kMax){
            Solution newSolution = current.copy();

            switch(k){
                case 0:
                    exchange.applyRandom(newSolution, 10);
                    break;
                case 1:
                    relocate.applyRandom(newSolution, 10);
                    break;
                case 2:
                    int routeIndex = (int)(Math.random() * (newSolution.getRoutes().size() - 1));
                    int hotelIndex = (int)(Math.random() * newSolution.getHotels().size());
                    List<SiteNode> deletedSites = new ArrayList<>();
                    List<Pair> dummyTabu = new ArrayList<>();
                    if(exchangeHotel.applyTS(newSolution, dummyTabu, 1, best.getScore())){
                        while(relocate.applyBestImprovement(newSolution)){}
                    }
                    break;
            }

            newSolution = newSolution.solveVND();

            if(newSolution.getScore() > best.getScore()){
                best = newSolution.copy();
                current = newSolution.copy();
                k = 0;
            }else{
                k++;
            }
        }
        return best;
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

    public void setSolution(Solution solution){
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
        StringBuilder text = new StringBuilder();
        text.append(score).append("\n");
        for(Route route : routes){
            text.append(route).append("\n");
        }
        return text.toString();
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
        return solution;
    }
}
