package VRP.solution;

import VRP.Instance;
import VRP.model.Node;
import VRP.model.Route;
import VRP.model.SiteNode;

import java.util.List;

public class NearestNeighborScoreSolution extends NearestNeighborSolution{

    public NearestNeighborScoreSolution(){
        super();
    }

    @Override
    protected Result nearestSite(Node node, Route route, List<Node> visited){
        Result result = new Result(null, 0);
        for(SiteNode site : sites){
            if(!visited.contains(site)){
                double distance = Instance.getDistance(node.getId(), site.getId());
                if(route.checkDistanceLast(site)) {
                    double scoreDistanceSite = site.getScore()/distance;
                    double scoreDistanceResult = result.getNode() != null ? result.getNode().getScore()/result.getDistance() : 0.0;
                     if (scoreDistanceSite > scoreDistanceResult) {
                         result.setDistance(distance);
                         result.setNode(site);
                     }else if(scoreDistanceSite == scoreDistanceResult && distance < result.getDistance()){
                         result.setDistance(distance);
                         result.setNode(site);
                     }
                }
            }
        }
        return result;
    }
}
