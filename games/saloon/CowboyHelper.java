
package games.saloon;

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

  public void Act(Game game)
  {
    goal.Act(cowboy, game);
  }

  public Tile FutureTile(int turnOffset)
  {
    return null;
  }
}
