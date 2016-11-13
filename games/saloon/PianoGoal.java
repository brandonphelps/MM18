
package games.saloon;

import java.util.List;



public class PianoGoal extends Goal
{
  
  final float QUALIFICATION_DISTANCE_WEIGHT = 0.8f;
  final float QUALIFICATION_HEALTH_WEIGHT = 0.2f;
  
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

  private Furnishing findPiano(String piano_id)
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
          int moveTileDanger = DangerAvoidance.CalculateTileDanger(_game, moveTile, cowboy);

          if(moveTileDanger >= Constants.MEDIUM_DANGER_THRESHOLD)
          {
            //We are in some danger. See if other places are safer.
            int targetXCoord = this.TargetTile().x;
            int targetYCoord = this.TargetTile().y;
            Tile alternateTileA, alternateTileB;
            if(moveTile == cowboy.tile.tileNorth || moveTile == cowboy.tile.tileSouth)
            {
              //We are moving on the y axis. alternate tiles are on either side on x axis.
              alternateTileB = cowboy.tile.tileWest;
              alternateTileA = cowboy.tile.tileEast;
            } else if(moveTile == cowboy.tile.tileEast || moveTile == cowboy.tile.tileWest)
            {
              alternateTileA = cowboy.tile.tileNorth;
              alternateTileB = cowboy.tile.tileSouth;
            } else
            {
              System.out.println("Error finding alternate route.");
              alternateTileA = moveTile;
              alternateTileB = moveTile;
            }

            //Find min danger.
            int alternateADanger = DangerAvoidance.CalculateTileDanger(_game, alternateTileA, cowboy);
            int alternateBDanger = DangerAvoidance.CalculateTileDanger(_game, alternateTileB, cowboy);

            int minDanger = Math.min(moveTileDanger, Math.min(alternateADanger, alternateBDanger));
            if(minDanger == alternateADanger)
            {
              moveTile = alternateTileA;
            } else if(minDanger == alternateBDanger)
            {
              moveTile = alternateTileB;
            }
          }


          cowboy.move(moveTile);
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
            break;
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
    
    int distance = Math.abs(piano.tile.x - cowboy.tile.x) + Math.abs(piano.tile.y - cowboy.tile.y);
    
    
    float distanceFactor = (1 - (distance / MAX_DISTANCE));
    
    float healthFactor = (cowboy.health / MAX_COWBOY_HEALTH);
    
    return (QUALIFICATION_DISTANCE_WEIGHT * distanceFactor) + (QUALIFICATION_HEALTH_WEIGHT * healthFactor);
    
    

  }

  public String toString()
  {
    return "Piano Id: " + id + " with piano " + piano_id;
  }
}
