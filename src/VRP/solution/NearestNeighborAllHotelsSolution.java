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

    @Override
    public void construct() {
        List<List<Route>> solutions =  this.constructHotels();
        solutions = this.constructSitesAllHotels(solutions);
        this.routes = getBestSolution(solutions);
        this.resetLinks();
        this.evaluate();
    }

    protected List<Route> getBestSolution(List<List<Route>> solutions){
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

    protected List<List<Route>> constructSitesAllHotels(List<List<Route>> solutions){
        for(List<Route> solution : solutions){
            constructSites(solution);
        }

        return solutions;
    }
}
