
package games.saloon;

import java.util.List;

public class PianoGoal extends Goal
{
  private Furnishing piano = null;

  public PianoGoal()
  {
    
  }

  public PianoGoal(Furnishing piano)
  {
    if(!piano.isPiano)
    {
      System.out.println("Oh no you setup a piano goal for a table!");
    }

    this.piano = piano;
  }

  public void SetPiano(Furnishing piano)
  {
    if(!piano.isPiano)
    {
      System.out.println("You are trying to setup a table as a goal for as a piano");
    }
    this.piano = piano;
  }

  public void Act(Cowboy cowboy, Game game)
  {
    if(piano == null){
      System.out.println("I do not have a piano for my goal");
    }

    // There will always be pianos or the game will end. No need to check for existence.
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
	cowboy.move(path.get(0));
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

  public Tile TargetTile()
  {
    if(piano != null)
    {
      return piano.tile;
    }
    return null;
  }
}
