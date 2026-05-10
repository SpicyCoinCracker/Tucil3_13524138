import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public final class IceSearcher {

  private static final double EPS = 1e-9;

  private IceSearcher() {}

  public static SearchOutcome search(ProblemInstance p, AlgorithmKind algo, HeuristicKind heur) {
    String[] grid = p.grid;
    int[][] costs = p.costs;
    int n = p.n(), m = p.m();
    Map<Integer, int[]> digitPos = IceLogic.digitPositions(grid);
    int minPc = IceLogic.minPositiveTraversableCost(grid, costs);
    int[] goals = p.goals;
    int goalR = p.goalR, goalC = p.goalC;

    State start = new State(p.startR, p.startC, 0);

    Map<State, MoveParent> parents = new HashMap<>();
    parents.put(start, new MoveParent(null, null));

    Map<State, Double> gScore = new HashMap<>();
    gScore.put(start, 0.0);

    int tieCounter = 0;
    PriorityQueue<PQNode> pq =
        new PriorityQueue<>(
            Comparator.comparingDouble((PQNode x) -> x.priority).thenComparingInt(x -> x.tie));

    pq.add(
        new PQNode(priority(algo, heur, goals, digitPos, minPc, goalR, goalC, start, 0.0), tieCounter++, start, 0.0));

    List<State> exploration = new ArrayList<>();
    int iterations = 0;
    long t0 = System.nanoTime();
    State goalState = null;

    while (!pq.isEmpty()) {
      iterations++;
      PQNode node = pq.poll();
      State st = node.state;
      double g = node.g;
      double bestG = gScore.getOrDefault(st, Double.POSITIVE_INFINITY);
      if (g > bestG + EPS) continue;

      exploration.add(st);
      if (st.r == goalR && st.c == goalC && st.phase >= goals.length) {
        goalState = st;
        break;
      }

      for (int d = 0; d < IceLogic.numDirections(); d++) {
        SlideResult res = IceLogic.slide(grid, costs, st.r, st.c, d, st.phase, goals, n, m);
        if (res == null) continue;
        double ng = g + res.moveCost;
        State next = new State(res.endR, res.endC, res.newPhase);
        if (ng + EPS < gScore.getOrDefault(next, Double.POSITIVE_INFINITY)) {
          gScore.put(next, ng);
          char mv = IceLogic.moveName(d);
          parents.put(next, new MoveParent(st, mv));
          double pri =
              priority(algo, heur, goals, digitPos, minPc, goalR, goalC, next, ng);
          pq.add(new PQNode(pri, tieCounter++, next, ng));
        }
      }
    }

    double elapsedMs = (System.nanoTime() - t0) / 1_000_000.0;

    if (goalState == null) return new SearchOutcome(null, 0, iterations, elapsedMs, exploration);

    StringBuilder path = new StringBuilder();
    State cur = goalState;
    while (cur != null) {
      MoveParent mp = parents.get(cur);
      if (mp == null) break;
      if (mp.move != null) path.append(mp.move);
      cur = mp.prev;
    }
    path.reverse();

    double totalCost = gScore.get(goalState);
    return new SearchOutcome(path.toString(), totalCost, iterations, elapsedMs, exploration);
  }

  private static double priority(
      AlgorithmKind algo,
      HeuristicKind heur,
      int[] goals,
      Map<Integer, int[]> digitPos,
      int minPc,
      int goalR,
      int goalC,
      State st,
      double g) {
    double h =
        IceLogic.heuristic(heur, st.r, st.c, st.phase, goals, digitPos, goalR, goalC, minPc);
    switch (algo) {
      case UCS:
        return g;
      case GBFS:
        return h;
      case ASTAR:
        return g + h;
      default:
        throw new IllegalArgumentException("Algorithm");
    }
  }

  private static final class PQNode {
    final double priority;
    final int tie;
    final State state;
    final double g;

    PQNode(double priority, int tie, State state, double g) {
      this.priority = priority;
      this.tie = tie;
      this.state = state;
      this.g = g;
    }
  }
}
