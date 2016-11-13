package games.saloon;

public abstract class Goal
{
  public boolean IsFinished = false;
  public Goal()
  {
    
  }
  public abstract void Act(Cowboy cowboy, Game game);
  public abstract Tile TargetTile();
  public abstract double Qualification(Cowboy cowboy);
}
