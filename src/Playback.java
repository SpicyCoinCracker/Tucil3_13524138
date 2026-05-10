import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public final class Playback {

  private Playback() {}

  public static void playSolution(List<String[]> frames, int startStep, Scanner sc) {
    int idx = Math.max(0, Math.min(startStep, frames.size() - 1));
    System.out.println();
    System.out.println(
        "Playback solusi: ketik `a` atau `>` maju, `d` atau `<` mundur, `w` lompat ke step, `q` keluar.");
    while (true) {
      System.out.printf(Locale.ROOT, "%n--- Step %d / %d ---%n", idx, frames.size() - 1);
      System.out.println(String.join(System.lineSeparator(), frames.get(idx)));

      System.out.print("> ");
      String line = sc.nextLine().trim().toLowerCase(Locale.ROOT);
      if (line.isEmpty()) continue;
      char c = line.charAt(0);
      if (c == 'q') break;
      if (c == 'a' || c == '>') idx = Math.min(frames.size() - 1, idx + 1);
      else if (c == 'd' || c == '<') idx = Math.max(0, idx - 1);
      else if (c == 'w') {
        System.out.print("Lompat ke step (angka): ");
        try {
          int j = Integer.parseInt(sc.nextLine().trim());
          idx = Math.max(0, Math.min(j, frames.size() - 1));
        } catch (NumberFormatException ignored) {
        }
      }
    }
  }

  public static void playExploration(
      List<State> exploration, ProblemInstance p, int startStep, Scanner sc) {
    if (exploration.isEmpty()) {
      System.out.println("Tidak ada data eksplorasi.");
      return;
    }
    int n = p.n(), m = p.m();
    List<String[]> frames = new ArrayList<>(exploration.size());
    for (State st : exploration) {
      frames.add(IceLogic.renderExplorationState(p.grid, p.goalR, p.goalC, st, p.goals, n, m));
    }
    playSolution(frames, startStep, sc);
  }

  public static void writeIterationLog(Path path, List<State> exploration) throws IOException {
    StringBuilder sb = new StringBuilder();
    sb.append("total_iterations ").append(exploration.size()).append('\n');
    int i = 0;
    for (State st : exploration) {
      sb.append("iter ")
          .append(i++)
          .append(" row ")
          .append(st.r)
          .append(" col ")
          .append(st.c)
          .append(" phase ")
          .append(st.phase)
          .append('\n');
    }
    Files.writeString(path, sb.toString(), StandardCharsets.UTF_8);
  }
}
