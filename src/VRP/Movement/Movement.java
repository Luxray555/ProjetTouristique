package VRP.Movement;

import VRP.solution.Solution;

public interface Movement {
    public boolean applyBestImprovement(Solution s);
    public boolean applyFirstImprovement(Solution s);
}
