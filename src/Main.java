import VRP.Solver;

public class Main {

    public static void main(String[] args){
        Solver solver = new Solver(new String[]{"INPUT=data/instance1.txt", "CONSTRUCT=0", "METHOD=0", "OUTPUT=result/Instance1.sol"});
        solver.solve();
    }
}
