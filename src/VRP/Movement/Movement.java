package VRP.Movement;

import VRP.model.Pair;
import VRP.solution.Solution;

import java.util.ArrayList;
import java.util.List;

public interface Movement {
    public boolean applyBestImprovement(Solution s);
    public boolean applyFirstImprovement(Solution s);
    public void applyRandom(Solution s, int nb);
    public List<Pair> getPairs(Solution s);
}
