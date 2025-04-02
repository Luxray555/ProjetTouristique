package VRP.solution;

import VRP.Instance;
import VRP.model.HotelNode;
import VRP.model.Node;
import VRP.model.Route;
import VRP.model.SiteNode;

import java.util.ArrayList;
import java.util.List;

public class NearestNeighborAllHotelsSolution extends NearestNeighborSolution {

    public NearestNeighborAllHotelsSolution(){
        super();
    }

    private List<List<Route>> solutions = new ArrayList<>();

    @Override
    public void construct() {
        this.constructHotels();
        this.constructSitesAllHotels();
        this.routes = getBestSolution();
        this.resetLinks();
        this.evaluate();
    }

    protected List<Route> getBestSolution(){
        int maxScore = 0;
        List<Route> bestSolution = null;
        for(List<Route> solution : solutions){
            int score = 0;
            for(Route route : solution){
                score += route.getScoreTotal();
            }
            if(score >= maxScore){
                bestSolution = solution;
                maxScore = score;
            }
        }
        return bestSolution;
    }

    protected void constructSitesAllHotels(){
        for(List<Route> solution : solutions){
            constructSites(solution);
        }
    }

    @Override
    protected void constructHotels() {
        List<Route> routesConstruct = new ArrayList<>(this.routes);

        List<List<Route>> solutions = new ArrayList<>();
        recursiveHotels(routesConstruct.get(0), routesConstruct, solutions);

        sortedSolutions(solutions);

        this.solutions = solutions;
    }
}
