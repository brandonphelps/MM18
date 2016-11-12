

package games.saloon;

public abstract class Goal
{
  public Goal()
  {
    
  }

  public abstract void Act(Cowboy cowboy, Game game);

  public abstract Tile TargetTile();
}
