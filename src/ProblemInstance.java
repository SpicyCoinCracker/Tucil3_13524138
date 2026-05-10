public final class ProblemInstance {
  public final String[] grid;
  public final int[][] costs;
  public final int startR;
  public final int startC;
  public final int goalR;
  public final int goalC;
  public final int[] goals;

  public ProblemInstance(
      String[] grid,
      int[][] costs,
      int startR,
      int startC,
      int goalR,
      int goalC,
      int[] goals) {
    this.grid = grid;
    this.costs = costs;
    this.startR = startR;
    this.startC = startC;
    this.goalR = goalR;
    this.goalC = goalC;
    this.goals = goals;
  }

  public int n() {
    return grid.length;
  }

  public int m() {
    return grid[0].length();
  }
}
