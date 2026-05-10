import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;


public final class MainCli {

  private MainCli() {}

  public static void main(String[] args) throws IOException {
    Scanner sc = new Scanner(System.in);

    System.out.println(">> Masukan file input (.txt):");
    String pathStr = stripQuotes(sc.nextLine().trim());
    if (pathStr.isEmpty()) {
      System.err.println("Path kosong.");
      System.exit(1);
    }
    Path path = Path.of(pathStr);
    if (!Files.isRegularFile(path)) {
      System.err.println("File tidak ditemukan: " + pathStr);
      System.exit(1);
    }

    String raw = Files.readString(path, StandardCharsets.UTF_8);

    ProblemInstance prob;
    try {
      prob = InputParser.parse(raw);
    } catch (IllegalArgumentException e) {
      System.err.println("Input tidak valid: " + e.getMessage());
      System.exit(1);
      return;
    }

    System.out.println(">> Pilih Algoritma (UCS/GBFS/A*)");
    String algoStr = sc.nextLine().trim().toUpperCase(Locale.ROOT);
    AlgorithmKind algo;
    if ("UCS".equals(algoStr)) algo = AlgorithmKind.UCS;
    else if ("GBFS".equals(algoStr)) algo = AlgorithmKind.GBFS;
    else if ("A*".equals(algoStr) || "ASTAR".equals(algoStr)) algo = AlgorithmKind.ASTAR;
    else {
      System.err.println("Algoritma harus UCS, GBFS, atau A*.");
      System.exit(1);
      return;
    }

    HeuristicKind heur = HeuristicKind.H1;
    if (algo != AlgorithmKind.UCS) {
      System.out.println(">> Heuristic apa yang anda pilih? (H1/H2/H3)");
      String hStr = sc.nextLine().trim().toUpperCase(Locale.ROOT);
      if ("H1".equals(hStr)) heur = HeuristicKind.H1;
      else if ("H2".equals(hStr)) heur = HeuristicKind.H2;
      else if ("H3".equals(hStr)) heur = HeuristicKind.H3;
      else {
        System.err.println("Heuristic harus H1, H2, atau H3.");
        System.exit(1);
        return;
      }
    }

    SearchOutcome out = IceSearcher.search(prob, algo, heur);

    if (out.path == null) {
      System.out.println("Tidak ada solusi.");
      System.out.printf(Locale.ROOT, ">> Waktu eksekusi: %.2f ms%n", out.elapsedMs);
      System.out.println(">> Banyak iterasi yang dilakukan: " + out.iterations);
      return;
    }

    System.out.println();
    System.out.println("Solusi Yang Ditemukan : " + out.path);
    if (Math.abs(out.totalCost - Math.round(out.totalCost)) < 1e-6)
      System.out.println("Cost dari Solusi : " + Math.round(out.totalCost));
    else System.out.printf(Locale.ROOT, "Cost dari Solusi : %s%n", out.totalCost);

    List<String[]> frames = IceLogic.applyMoves(prob, out.path);
    System.out.println("\nInitial");
    printBoard(frames.get(0));
    for (int i = 1; i < frames.size(); i++) {
      char mv = out.path.charAt(i - 1);
      System.out.println();
      System.out.println("Step " + i + " : " + mv);
      printBoard(frames.get(i));
    }

    System.out.printf(Locale.ROOT, "%n>> Waktu eksekusi: %.2f ms%n", out.elapsedMs);
    System.out.println(">> Banyak iterasi yang dilakukan: " + out.iterations);

    System.out.println("\n>> Apakah Anda ingin melakukan playback? (Ya/Tidak):");
    if (yes(sc.nextLine())) {
      System.out.println(">> Pada step berapa anda ingin memulai playback:");
      int startStep = 0;
      try {
        startStep = Integer.parseInt(sc.nextLine().trim());
      } catch (NumberFormatException ignored) {
      }
      System.out.println(">> Playback solusi (1) atau eksplorasi node (2)? [1/2]");
      String mode = sc.nextLine().trim();
      if ("2".equals(mode)) {
        Playback.playExploration(out.explorationOrder, prob, startStep, sc);
      } else Playback.playSolution(frames, startStep, sc);
    }

    System.out.println("\n>> Apakah Anda ingin menyimpan solusi? (Ya/Tidak):");
    if (yes(sc.nextLine())) {
      System.out.println(">> Path file output solusi:");
      String outp = stripQuotes(sc.nextLine().trim());
      String algoOut = algo == AlgorithmKind.ASTAR ? "A*" : algo.name();
      StringBuilder sb = new StringBuilder();
      sb.append("path ").append(out.path).append('\n');
      sb.append("cost ").append(formatCost(out.totalCost)).append('\n');
      sb.append("algorithm ").append(algoOut).append('\n');
      sb.append("heuristic ").append(heur.name()).append('\n');
      sb.append("iterations ").append(out.iterations).append('\n');
      sb.append(String.format(Locale.ROOT, "time_ms %.6f%n%n", out.elapsedMs));
      for (int i = 0; i < frames.size(); i++) {
        sb.append("step ").append(i).append('\n');
        sb.append(joinBoard(frames.get(i))).append("\n\n");
      }
      Files.writeString(Path.of(outp), sb.toString(), StandardCharsets.UTF_8);
      System.out.println("Solusi disimpan pada " + outp);
    }

    System.out.println("\n>> Simpan log iterasi ke .txt? (Ya/Tidak):");
    if (yes(sc.nextLine())) {
      System.out.println(">> Path file log iterasi:");
      String logp = stripQuotes(sc.nextLine().trim());
      Playback.writeIterationLog(Path.of(logp), out.explorationOrder);
      System.out.println("Log disimpan pada " + logp);
    }
  }

  private static String formatCost(double c) {
    if (Math.abs(c - Math.round(c)) < 1e-6) return Long.toString(Math.round(c));
    return Double.toString(c);
  }

  private static void printBoard(String[] rows) {
    System.out.println(joinBoard(rows));
  }

  private static String joinBoard(String[] rows) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < rows.length; i++) {
      sb.append(rows[i]);
      if (i + 1 < rows.length) sb.append('\n');
    }
    return sb.toString();
  }

  private static boolean yes(String line) {
    String s = line.strip().toLowerCase(Locale.ROOT);
    return s.equals("ya") || s.equals("y") || s.equals("yes");
  }

  private static String stripQuotes(String s) {
    if (s.length() >= 2 && ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'")))) {
      return s.substring(1, s.length() - 1);
    }
    return s;
  }
}
