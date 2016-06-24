package se.vestlife.bathbot.model;

public class BathroomState {

  private final boolean busy;
  private final String description;

  public BathroomState(boolean busy, String description) {
    this.busy = busy;
    this.description = description;
  }

  public boolean isBusy() {
    return busy;
  }

  public String getDescription() {
    return description;
  }
}
