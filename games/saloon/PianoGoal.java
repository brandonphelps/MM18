
package games.saloon;

import java.util.List;



public class PianoGoal extends Goal
{
  
  final float QUALIFICATION_DISTANCE_WEIGHT = 1.0f;
  final float QUALIFICATION_HEALTH_WEIGHT = 0.0f;
  
  final int MAX_DISTANCE=30;
  final int MAX_COWBOY_HEALTH=10;
  public static int next_id = 0;
  public int id = 0;
  private Furnishing piano = null;
  private String piano_id = "";

  public PianoGoal(Game game)
  {
    super(game);
  }

  public PianoGoal(Game game, String piano_id)
  {
    super(game);
    this.piano_id = piano_id;
    id = next_id++;
  }

  public Furnishing findPiano()
  {
    return(findPiano(piano_id));
  }

  public Furnishing findPiano(String piano_id)
  {
    Furnishing piano = null;

    for(int i = 0; i < _game.furnishings.size(); i++)
    {
      if(_game.furnishings.get(i).isPiano && _game.furnishings.get(i).id == piano_id)
      {
	piano = _game.furnishings.get(i);
      }
    }

    return piano;
  }

  public void Act(Cowboy cowboy)
  {
    Furnishing piano = findPiano(piano_id);

    if(piano == null)
    {
      System.out.println("I do not have a piano for my goal");
      IsFinished = false;
    }
    else
    {
      // There will always be pianos or the _game will end. No need to check for existence.
      // Attempt to move toward the piano by finding a path.
      if(cowboy.canMove && !cowboy.isDead)
      {
        // Find a path of tiles to the piano from our active cowboy's tile
        List<Tile> path = PathFinder.findPath(cowboy.tile, piano.tile);

        // if there is a path, move along it
        //      length 0 means no path could be found to the tile
        //      length 1 means the piano is adjacent, and we can't move onto the same tile as the piano
        if(path.size() > 1)
        {
          Tile moveTile = path.get(0);

          //If this path looks dangerous enough, try another path.
          int moveTileDanger = DangerAvoidance.CalculateTileDanger(_game, moveTile, cowboy);

          if(moveTileDanger >= Constants.MEDIUM_DANGER_THRESHOLD)
          {
            //We are in some danger. See if other places are safer.
            DangerAvoidance.avoidDangerAndMove(_game, cowboy, moveTile);
          }
	  else
          {
            //We aren't in significant danger.
            cowboy.move(moveTile);
          }
        }
      }

      // 3. Try to play a nearby piano.
      if(!cowboy.isDead && cowboy.turnsBusy == 0)
      {
        System.out.println("Trying to use Cowboy #" + cowboy.id);

        List<Tile> neighbors = cowboy.tile.getNeighbors();

        for(int i = 0; i < neighbors.size(); i++)
        {
          Tile neighbor = neighbors.get(i);
          if(neighbor.furnishing != null && neighbor.furnishing.isPiano)
          {
            cowboy.play(neighbor.furnishing);
          }
      	}
      }
    }
  }

  public Tile TargetTile()
  {
    if(piano != null)
    {
      return piano.tile;
    }
    return null;
  }

  public double Qualification(Cowboy cowboy)
  {
    
    System.out.println("looking for piano id" + piano_id.toString());
    Furnishing piano = findPiano(piano_id);
    
    if(cowboy.isDrunk || cowboy.isDead)
      return(0);
    
    float distance = Math.abs(piano.tile.x - cowboy.tile.x) + Math.abs(piano.tile.y - cowboy.tile.y);
    
    
    float distanceFactor = (1 - (distance / MAX_DISTANCE));
    
    float healthFactor = (cowboy.health / MAX_COWBOY_HEALTH);
    
    return (QUALIFICATION_DISTANCE_WEIGHT * distanceFactor) + (QUALIFICATION_HEALTH_WEIGHT * healthFactor);
    
    

  }

  public String toString()
  {
    return "Piano Id: " + id + " with piano " + piano_id;
  }
}
