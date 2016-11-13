
package games.saloon;

import java.util.List;
import java.util.Queue;

public class CowboyHelper
{
  // the specific cowboy that the goal is assoiciated with
  public Cowboy cowboy;
  public Goal goal;

  public CowboyHelper(Cowboy c, Goal g)
  {
    cowboy = c;
    goal = g;
  }

  public void SetCowboy(Cowboy c)
  {
    cowboy = c;
  }

  public void Act()
  {
    goal.Act(cowboy);
  }

  public Tile TargetTile()
  {
    return goal.TargetTile();
  }
  
  public Tile FutureTile(int turnOffset)
  {
    List<Tile> path = PathFinder.findPath(cowboy.tile, TargetTile());

    if(turnOffset > path.size())
    {
       return path.get(path.size()-1);
    }

    return null;
  }
}
