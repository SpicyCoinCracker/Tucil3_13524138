import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class IceLogic {

  private static final int[][] DELTAS = {{0, 1}, {0, -1}, {-1, 0}, {1, 0}};
  private static final char[] MOVE_NAME = {'R', 'L', 'U', 'D'};

  private IceLogic() {}

  public static char moveName(int dirIndex) {
    return MOVE_NAME[dirIndex];
  }

  public static int numDirections() {
    return 4;
  }

  //Kembalikan null jika invalid
  public static SlideResult slide(
      String[] grid,
      int[][] costs,
      int r,
      int c,
      int dirIndex,
      int phase,
      int[] goals,
      int n,
      int m) {
    int dr = DELTAS[dirIndex][0];
    int dc = DELTAS[dirIndex][1];
    int p = phase;
    int cr = r, cc = c;
    long moveCost = 0;
    int tr = cr + dr, tc = cc + dc;
    if (tr < 0 || tr >= n || tc < 0 || tc >= m) return null;

    while (true) {
      if (tr < 0 || tr >= n || tc < 0 || tc >= m) return null;
      char ch = grid[tr].charAt(tc);
      if (ch == 'X') break;
      if (ch == 'L') return null;
      if (ch >= '0' && ch <= '9') {
        int d = ch - '0';
        if (p < goals.length) {
          int need = goals[p];
          if (d > need) return null;
          if (d == need) p++;
        }
      }
      moveCost += costs[tr][tc];
      cr = tr;
      cc = tc;
      tr += dr;
      tc += dc;
    }
    if (cr == r && cc == c) return null;
    if (moveCost > Integer.MAX_VALUE) throw new IllegalStateException("Cost overflow");
    return new SlideResult(cr, cc, p, (int) moveCost);
  }

  public static int minPositiveTraversableCost(String[] grid, int[][] costs) {
    Integer best = null;
    int n = grid.length, m = grid[0].length();
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < m; j++) {
        char ch = grid[i].charAt(j);
        if (ch == '*' || ch == 'O' || (ch >= '0' && ch <= '9')) {
          int v = costs[i][j];
          if (v > 0 && (best == null || v < best)) best = v;
        }
      }
    }
    return best == null ? 1 : best;
  }

  /** Digit -> (row,col) for digits present on grid. */
  public static Map<Integer, int[]> digitPositions(String[] grid) {
    Map<Integer, int[]> map = new HashMap<>();
    int n = grid.length, m = grid[0].length();
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < m; j++) {
        char ch = grid[i].charAt(j);
        if (ch >= '0' && ch <= '9') map.put(ch - '0', new int[] {i, j});
      }
    }
    return map;
  }

  public static double heuristic(
      HeuristicKind h,
      int r,
      int c,
      int phase,
      int[] goals,
      Map<Integer, int[]> digitPos,
      int gr,
      int gc,
      int minPosCost) {
    switch (h) {
      case H1:
        return (Math.abs(r - gr) + Math.abs(c - gc)) * (double) minPosCost;
      case H2:
        {
          int tr, tc;
          if (phase < goals.length) {
            int[] xy = digitPos.get(goals[phase]);
            tr = xy[0];
            tc = xy[1];
          } else {
            tr = gr;
            tc = gc;
          }
          return (Math.abs(r - tr) + Math.abs(c - tc)) * (double) minPosCost;
        }
      case H3:
        return Math.max(Math.abs(r - gr), Math.abs(c - gc)) * (double) minPosCost;
      default:
        throw new IllegalArgumentException("Unknown heuristic");
    }
  }

  public static List<String[]> applyMoves(ProblemInstance p, String path) {
    int n = p.n(), m = p.m();
    int zr = p.startR, zc = p.startC;
    int phase = 0;
    List<String[]> frames = new ArrayList<>();
    frames.add(render(p.grid, p.goalR, p.goalC, zr, zc, phase, p.goals, n, m));
    for (int k = 0; k < path.length(); k++) {
      char mv = path.charAt(k);
      int di = moveIndex(mv);
      if (di < 0) break;
      SlideResult res = slide(p.grid, p.costs, zr, zc, di, phase, p.goals, n, m);
      if (res == null) break;
      zr = res.endR;
      zc = res.endC;
      phase = res.newPhase;
      frames.add(render(p.grid, p.goalR, p.goalC, zr, zc, phase, p.goals, n, m));
    }
    return frames;
  }

  private static int moveIndex(char mv) {
    if (mv == 'R') return 0;
    if (mv == 'L') return 1;
    if (mv == 'U') return 2;
    if (mv == 'D') return 3;
    return -1;
  }

  private static String[] render(
      String[] grid,
      int gr,
      int gc,
      int zr,
      int zc,
      int phase,
      int[] goals,
      int n,
      int m) {
    char[][] g2 = new char[n][m];
    for (int i = 0; i < n; i++) g2[i] = grid[i].toCharArray();
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < m; j++) {
        char ch = g2[i][j];
        if (ch >= '0' && ch <= '9') {
          int d = ch - '0';
          if (phase >= goals.length) g2[i][j] = '*';
          else {
            int need = goals[phase];
            if (d < need) g2[i][j] = '*';
          }
        }
      }
    }
    g2[gr][gc] = 'O';
    g2[zr][zc] = 'Z';
    String[] out = new String[n];
    for (int i = 0; i < n; i++) out[i] = new String(g2[i]);
    return out;
  }

  public static String[] renderExplorationState(
      String[] grid, int gr, int gc, State st, int[] goals, int n, int m) {
    return render(grid, gr, gc, st.r, st.c, st.phase, goals, n, m);
  }
}
