public final class SlideResult {
  public final int endR;
  public final int endC;
  public final int newPhase;
  public final int moveCost;

  public SlideResult(int endR, int endC, int newPhase, int moveCost) {
    this.endR = endR;
    this.endC = endC;
    this.newPhase = newPhase;
    this.moveCost = moveCost;
  }
}
