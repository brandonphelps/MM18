package games.saloon;

import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Queue;
import java.util.HashMap;

public class PathFinder
{
  public static List<Tile> findPath(Tile start, Tile goal)
  {
    // no need to make a path to here...
    if (start == goal)
    {
      return new ArrayList<Tile>();
    }

    // the tiles that will have their neighbors searched for 'goal'
      Queue<Tile> fringe = new LinkedList<Tile>();

      // How we got to each tile that went into the fringe.
      HashMap<Tile, Tile> cameFrom = new HashMap<Tile, Tile>();

      // Enqueue start as the first tile to have its neighbors searched.
      fringe.add(start);

      // keep exploring neighbors of neighbors... until there are no more.
      while (!fringe.isEmpty()) {
	// the tile we are currently exploring.
	Tile inspect = fringe.remove();

	// cycle through the tile's neighbors.
	List<Tile> neighbors = inspect.getNeighbors();
	for (int i = 0; i < neighbors.size(); i++)
	{
	  Tile neighbor = neighbors.get(i);

	  // If we found the goal we've found the path!
	  if (neighbor == goal)
	  {
	    // Follow the path backward starting at the goal and return it.
	    List<Tile> path = new ArrayList<Tile>();
	    path.add(goal);

	    // Starting at the tile we are currently at, insert them retracing our steps till we get to the starting tile
	    for (Tile step = inspect; step != start; step = cameFrom.get(step))
	    {
	      path.add(0, step);
	    }

	    return path;
	  }

	  // if the tile exists, has not been explored or added to the fringe yet, and it is pathable
	  if (neighbor != null && !cameFrom.containsKey(neighbor) && neighbor.isPathable()) {
	    // add it to the tiles to be explored and add where it came from.
	    fringe.add(neighbor);
	    cameFrom.put(neighbor, inspect);
	  }

	} // for each neighbor

      } // while fringe not empty

        // if you're here, that means that there was not a path to get to where you want to go.
        //   in that case, we'll just return an empty path.
      return new ArrayList<Tile>();
  }
}
