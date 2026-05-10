import java.util.Objects;

public final class State {
  public final int r;
  public final int c;
  public final int phase;

  public State(int r, int c, int phase) {
    this.r = r;
    this.c = c;
    this.phase = phase;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof State)) return false;
    State s = (State) o;
    return r == s.r && c == s.c && phase == s.phase;
  }

  @Override
  public int hashCode() {
    return Objects.hash(r, c, phase);
  }
}
