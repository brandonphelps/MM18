
package games.saloon;

public class CowboyHelper
{
  private Cowboy cowboy;
  private Goal goal;

  public CowboyHelper(Goal g, Cowboy c)
  {
    cowboy = c;
    goal = g;
  }

  public void act(Game game)
  {
    goal.act(cowboy, game);
  }
}
