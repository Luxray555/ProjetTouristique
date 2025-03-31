package VRP.model;

public class HotelNode extends Node{
    public HotelNode(int id, int score) {
        super(id, score);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Hotel");
        sb.append(getId());
        return sb.toString();
    }
}
