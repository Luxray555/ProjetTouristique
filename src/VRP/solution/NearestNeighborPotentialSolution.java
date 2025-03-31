package VRP.solution;

import VRP.Instance;
import VRP.model.HotelNode;
import VRP.model.Node;
import VRP.model.Route;
import VRP.model.SiteNode;

import java.util.List;

public class NearestNeighborPotentialSolution extends NearestNeighborSolution{

    public NearestNeighborPotentialSolution(){
        super();
    }

    @Override
    protected Result nearestHotel(Route route, List<HotelNode> hotels){
        ResultPotentiel result = new ResultPotentiel(null, 0, 0);
        for(HotelNode hotel : hotels){
            double distance = Instance.getDistance(route.getHotelStart().getId(), hotel.getId());
            int potentiel = potentielHotel(route, hotel);
            if(result.getNode() == null || potentiel > result.getPotentiel()){
                result.setPotentiel(potentiel);
                result.setDistance(distance);
                result.setNode(hotel);
            }
        }
        return result;
    }

    private int potentielHotel(Route route, HotelNode hotel){
        int score = 0;
        for(SiteNode site : sites){
            if(route.getDistanceMax() >= Instance.getDistance(route.getHotelStart().getId(), site.getId()) + Instance.getDistance(site.getId(), hotel.getId())){
                score += site.getScore();
            }
        }
        return score;
    }

    public class ResultPotentiel extends Result{
        private int potentiel;

        public ResultPotentiel(Node node, double distance, int potentiel){
            super(node, distance);
            this.potentiel = potentiel;
        }

        public int getPotentiel() {
            return potentiel;
        }

        public void setPotentiel(int potentiel) {
            this.potentiel = potentiel;
        }

    }
}
