import java.util.List;

/** Result of pathfinding: path may be null if unsolvable. */
public final class SearchOutcome {
  public final String path;
  public final double totalCost;
  public final int iterations;
  public final double elapsedMs;
  public final List<State> explorationOrder;

  public SearchOutcome(
      String path, double totalCost, int iterations, double elapsedMs, List<State> explorationOrder) {
    this.path = path;
    this.totalCost = totalCost;
    this.iterations = iterations;
    this.elapsedMs = elapsedMs;
    this.explorationOrder = explorationOrder;
  }
}
