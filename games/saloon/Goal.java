package games.saloon;

public abstract class Goal
{
  public boolean IsFinished = false;
  protected Game _game;
  public Goal(Game game)
  {
    _game = game;
  }
  public abstract void Act(Cowboy cowboy);
  public abstract Tile TargetTile();
  public abstract double Qualification(Cowboy cowboy);
  
  
  
}
