import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class InputParser {

  private InputParser() {}

  public static ProblemInstance parse(String text) {
    List<String> raw = new ArrayList<>();
    for (String line : text.split("\\R")) {
      if (!line.strip().isEmpty()) raw.add(line);
    }
    if (raw.isEmpty()) throw new IllegalArgumentException("File kosong.");

    String[] head = raw.get(0).trim().split("\\s+");
    if (head.length != 2) throw new IllegalArgumentException("Baris pertama harus berisi N M.");
    int n = Integer.parseInt(head[0]);
    int m = Integer.parseInt(head[1]);
    if (n <= 0 || m <= 0) throw new IllegalArgumentException("N dan M harus positif.");

    int needLines = 1 + 2 * n;
    if (raw.size() < needLines) {
      throw new IllegalArgumentException(
          "Diharapkan setidaknya " + needLines + " baris non-kosong, dapat " + raw.size() + ".");
    }

    List<String> gridLines = raw.subList(1, 1 + n);
    List<String> costLines = raw.subList(1 + n, 1 + 2 * n);

    for (int i = 0; i < n; i++) {
      String row = gridLines.get(i);
      if (row.length() != m) throw new IllegalArgumentException("Panjang baris grid tidak konsisten dengan M.");
    }

    int[][] costs = new int[n][m];
    for (int i = 0; i < n; i++) {
      String[] parts = costLines.get(i).trim().split("\\s+");
      if (parts.length != m)
        throw new IllegalArgumentException("Baris cost " + (i + 1) + " harus berisi " + m + " angka.");
      for (int j = 0; j < m; j++) costs[i][j] = Integer.parseInt(parts[j]);
    }

    char[][] g = new char[n][m];
    for (int i = 0; i < n; i++) g[i] = gridLines.get(i).toCharArray();

    Integer startR = null, startC = null, goalR = null, goalC = null;
    Map<Integer, int[]> digitPos = new LinkedHashMap<>();

    for (int i = 0; i < n; i++) {
      for (int j = 0; j < m; j++) {
        char ch = g[i][j];
        if (ch == 'Z') {
          if (startR != null) throw new IllegalArgumentException("Lebih dari satu Z.");
          startR = i;
          startC = j;
          g[i][j] = '*';
        } else if (ch == 'O') {
          if (goalR != null) throw new IllegalArgumentException("Lebih dari satu O.");
          goalR = i;
          goalC = j;
          g[i][j] = '*';
        } else if (ch >= '0' && ch <= '9') {
          int d = ch - '0';
          if (digitPos.containsKey(d)) throw new IllegalArgumentException("Digit " + d + " muncul lebih dari sekali.");
          digitPos.put(d, new int[] {i, j});
        } else if (ch != '*' && ch != 'X' && ch != 'L') {
          throw new IllegalArgumentException("Karakter tidak dikenal di (" + i + "," + j + "): '" + ch + "'");
        }
      }
    }

    if (startR == null || goalR == null) throw new IllegalArgumentException("Harus ada tepat satu Z dan satu O.");

    String[] grid = new String[n];
    for (int i = 0; i < n; i++) grid[i] = new String(g[i]);

    int[] goals;
    if (digitPos.isEmpty()) goals = new int[0];
    else {
      List<Integer> keys = new ArrayList<>(digitPos.keySet());
      keys.sort(Integer::compareTo);
      int lo = keys.get(0);
      int hi = keys.get(keys.size() - 1);
      for (int d = lo; d <= hi; d++) {
        if (!digitPos.containsKey(d)) {
          throw new IllegalArgumentException(
              "Digit pada papan harus berurutan tanpa celah dari " + lo + " sampai " + hi + ".");
        }
      }
      goals = new int[hi - lo + 1];
      for (int k = 0; k < goals.length; k++) goals[k] = lo + k;
    }

    return new ProblemInstance(grid, costs, startR, startC, goalR, goalC, goals);
  }
}
