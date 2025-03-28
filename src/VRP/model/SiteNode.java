package VRP.model;

public class SiteNode extends Node{

    public SiteNode(int id, double score) {
        super(id, score);
    }

    public Node getPrevious(){
        return getRoutes().get(0).getPrevious(this);
    }

    public Node getNext(){
        return getRoutes().get(0).getNext(this);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Site");
        sb.append(getId());
        sb.append("(" + getScore() + ")");
        return sb.toString();
    }
}
