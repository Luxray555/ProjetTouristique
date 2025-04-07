package VRP.model;

public class SiteNode extends Node{

    public SiteNode(int id, int score) {
        super(id, score);
    }

    public Node getPrevious(){
        if(!routes.isEmpty()){
            return getRoutes().get(0).getPrevious(this);
        }else{
            return null;
        }
    }

    public Node getNext() {
        if (!routes.isEmpty()) {
            return getRoutes().get(0).getNext(this);
        } else {
            return null;
        }
    }
}
