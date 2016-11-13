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
      if(cowboy.canMove && !cowboy.isDead)
      {
        List<Tile> path = PathFinder.findPath(cowboy.tile, targetCowboy.tile);

        if(path.size() > 1)
        {
          //Move in place to attack.
          Tile moveTile = path.get(0);

          int moveTileDanger = DangerAvoidance.CalculateTileDanger(_game, moveTile, cowboy);

          if(path.size() > 3 && moveTileDanger >= Constants.MEDIUM_DANGER_THRESHOLD)
          {
            //If we aren't really close, take some move for a medium danger.
            DangerAvoidance.avoidDangerAndMove(_game, cowboy, moveTile);
          }
          else if(path.size() <= 3 && moveTileDanger >= Constants.HIGHISH_DANGER_THRESHOLD)
          {
            //If we aren't really close, take some move for a high danger.
            DangerAvoidance.avoidDangerAndMove(_game, cowboy, moveTile);
          } else
          {
            //There is no significant danger. Just move.
            cowboy.move(moveTile);
          }
        } else
        {
          //Time to attack

          if(cowboy.job.equals("Brawler"))
          {
            //Do nothing. Brawlers automatically attack.
          } else if(cowboy.job.equals(Constants.BARTENDER))
          {
            if(cowboy.turnsBusy == 0)
            {
              cowboy.act(targetCowboy.tile, Constants.NORTH);//TODO: MAYBE BE SMART ABOUT DIRECTION.
            }
          } else if(cowboy.job.equals(Constants.SHARPSHOOTER))
          {
              cowboy.act(targetCowboy.tile);
          } else
          {
            System.out.println("Invalid job: " + cowboy.job);
          }
        }
      }
      {

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
