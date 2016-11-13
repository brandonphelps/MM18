package games.saloon;

import java.util.List;

public class AttackGoal extends Goal
{

  String _targetCowboyId;

  public AttackGoal(Game game)
  {
    super(game);
  }

  public AttackGoal(Game game, String targetCowboyId)
  {
    super(game);
    _targetCowboyId = targetCowboyId;
  }

  public void Act(Cowboy cowboy)
  {
    // get the cowboy we want to attack
    Cowboy targetCowboy = null;
    for(int i = 0; i < _game.cowboys.size(); i++)
    {
      if(_game.cowboys.get(i).id.equals(_targetCowboyId))
      {
	targetCowboy = _game.cowboys.get(i);
	if(targetCowboy.isDead)
	{
	  targetCowboy = null;
	}
      }
    }

    if(targetCowboy != null)
    {
      if(cowboy.job.equals("Brawler"))
      {
	if(cowboy.canMove && !cowboy.isDead)
	{
	  List<Tile> path = PathFinder.findPath(cowboy.tile, targetCowboy.tile);

	  if(path.size() > 1)
	  {
	    Tile moveTile = path.get(0);

	    int moveTileDanger = DangerAvoidance.CalculateTileDanger(_game, moveTile, cowboy);

	    if(moveTileDanger >= Constants.MEDIUM_DANGER_THRESHOLD)
	    {
	      DangerAvoidance.avoidDangerAndMove(_game, cowboy, moveTile);
	    }
	    cowboy.move(moveTile);
	  }
	}
      }
    }
    else
    {
      IsFinished = true;
    }
  }

  public double Qualification(Cowboy cowboy)
  {
    return 0;
  }

  public Tile TargetTile()
  {
    return null;
  }
}
