/**
 * This is where you build your AI for the Saloon game.
 */
package games.saloon;

import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Queue;
import java.util.Random;
import joueur.BaseAI;

/**
 * This is where you build your AI for the Saloon game.
 */
public class AI extends BaseAI
{
	/**
	 * This is the Game object itself, it contains all the information about the current game
	 */
	public Game game;

	/**
	 * This is your AI's player. This AI class is not a player, but it should command this Player.
	 */
	public Player player;

	// you can add additional fields here for your AI to use


	/**
	 * This returns your AI's name to the game server. Just replace the string.
	 * @return string of you AI's name
	 */
	public String getName()
	{
		return "Fussy Fugitives"; // REPLACE THIS WITH YOUR TEAM NAME!
	}

	/**
	 * This is automatically called when the game first starts, once the Game object and all GameObjects have been initialized, but before any players do anything.
	 * This is a good place to initialize any variables you add to your AI, or start tracking game objects.
	 */
	public void start()
	{
		super.start();
	}

	/**
	 * This is automatically called every time the game (or anything in it) updates.
	 * If a function you call triggers an update this will be called before that function returns.
	 */
	public void gameUpdated()
	{
		super.gameUpdated();
	}

	/**
	 * This is automatically called when the game ends.
	 * You can do any cleanup of you AI here, or do custom logging. After this function returns the application will close.
	 * @param  won  true if your player won, false otherwise
	 * @param  name  reason">a string explaining why you won or lost
	 */
	public void ended(boolean won, String reason)
	{
		super.ended(won, reason);
	}

	public String activeCowboyId = null;

	public HashMap<String, CowboyHelper> cowboysToHelpers = new HashMap<String, CowboyHelper>();

	public HashMap<String, Boolean> pianoHasGoals = new HashMap<String, Boolean>();

	
	List<Cowboy> joblessCowboys;

	public List<CowboyHelper> GeneratePianoGoals()
	{
		
		List<CowboyHelper> cowboysWithJobs = new ArrayList<CowboyHelper>();

		List<Furnishing> goallessPianos = new ArrayList<Furnishing>();

		for (int i = 0; i < this.game.furnishings.size(); i++)
		{
			Furnishing furnishing = this.game.furnishings.get(i);

			if (furnishing.isPiano && !furnishing.isDestroyed)
			{
	 
				goallessPianos.add(furnishing);
			}
		}



		while(joblessCowboys.size() > 0 && goallessPianos.size() > 0)
		{
			double MaxQualification = 0;
			int pianoIndex = 0;
			int cowboyIndex = 0;

			for(int i = 0; i < goallessPianos.size(); i++)
			{
			  System.out.println("Checking piano: " + goallessPianos.get(i).id);
				PianoGoal p = new PianoGoal(game, goallessPianos.get(i).id);
				
				Furnishing piano = p.findPiano();
				
				HeatMap heatmap = new HeatMap();
				
				
				//System.out.println("PIANO AT " + piano.tile.x + ", " + piano.tile.y);
				//heatmap.SetValue(piano.tile.x, piano.tile.y, 99);
				
				for(int j = 0; j < joblessCowboys.size(); j++)
				{
					double temp = p.Qualification(joblessCowboys.get(j));
					
					if(joblessCowboys.get(j).tile == null)
					{
					    System.out.println("NULL TILE FOUND");
					}
					else
					{
					  System.out.println("QUAL VAL: " + temp);
					  //heatmap.SetValue(joblessCowboys.get(j).tile.x, joblessCowboys.get(j).tile.y, temp);
					}
					
					if(MaxQualification < temp)
					{
						MaxQualification = temp;
						pianoIndex = i;
						cowboyIndex = j;
					}
				}
				
				//System.out.println(heatmap.toString());
				
			}
			
			
			
			//joblessCowboys.get(cowboyIndex).log(goallessPianos.get(pianoIndex).id);
			//goallessPianos.get(pianoIndex).log(goallessPianos.get(pianoIndex).id);
			
			PianoGoal pg = new PianoGoal(game, goallessPianos.get(pianoIndex).id);
			CowboyHelper newHelper = new CowboyHelper(joblessCowboys.get(cowboyIndex), pg);
			cowboysWithJobs.add(newHelper);
				
			long qualification = Math.round(100.0f * pg.Qualification(newHelper.cowboy));
			
			String cowboyinfo = goallessPianos.get(pianoIndex).id + "\n" + qualification;
			newHelper.cowboy.log(cowboyinfo);
			goallessPianos.get(pianoIndex).log(goallessPianos.get(pianoIndex).id);

			joblessCowboys.remove(cowboyIndex);
			goallessPianos.remove(pianoIndex);
		}

		while(joblessCowboys.size() > 0)
		{
		  double MaxQualification = 0;
		  int myCowboyIndex = 0;
		  int targetCowboyIndex = 0;
		  // do note we are iterating over all the cowboys
		  for(int i = 0; i < game.cowboys.size(); i++)
		  {
		    // if the cowboy is the enemies then they are potential target
		    if(!game.cowboys.get(i).owner.equals(player)) 
		    {
		      AttackGoal a = new AttackGoal(game, game.cowboys.get(i).id);
		      // iterate over our cowboys that do not have jobs
		      for(int j = 0; j < joblessCowboys.size(); j++)
		      {
			double temp = a.Qualification(joblessCowboys.get(j));

			if(MaxQualification < temp)
			{
			  MaxQualification = temp;
			  targetCowboyIndex = i;
			  myCowboyIndex = j;
			}
		      }
		    }
		  }
		  joblessCowboys.get(myCowboyIndex).log("Attacking...");

		  cowboysWithJobs.add(new CowboyHelper(joblessCowboys.get(myCowboyIndex), new AttackGoal(game, game.cowboys.get(targetCowboyIndex).id)));

		  joblessCowboys.remove(myCowboyIndex);
		}

		for(Furnishing piano: goallessPianos)
		piano.log("X");
            
		for(Cowboy cowboy: joblessCowboys)
		cowboy.log("X");
            

		return cowboysWithJobs;
	}
	

	/**
	 * This is called every time it is this AI.player's turn.
	 *
	 * @return Represents if you want to end your turn. True means end your turn, False means to keep your turn going and re-call this function.
	 */
	public boolean runTurn()
	{
		// This is "ShellAI", some basic code we've provided that does
		// everything in the game for demo purposed, but poorly so you
		// can get to optimizing or overwriting it ASAP
		//
		// ShellAI does a few things:
		// 1. Tries to spawn a new Cowboy
		// 2. Tries to move to a Piano
		// 3. Tries to play a Piano
		// 4. Tries to act

		System.out.println("Start of my turn: " + this.game.currentTurn);


	// Generates all valid goals

	// 
	
	joblessCowboys = new ArrayList<Cowboy>();
	
	for(int i = 0; i < this.player.cowboys.size(); i++)
		{
			Cowboy cowboy = this.player.cowboys.get(i);
			if(!cowboy.isDead)
			    joblessCowboys.add(cowboy);
		}



	// get list of pianos
	List<Furnishing> pianos = new ArrayList<Furnishing>();

	for (int i = 0; i < this.game.furnishings.size(); i++)
	{
		Furnishing furnishing = this.game.furnishings.get(i);

		if (furnishing.isPiano && !furnishing.isDestroyed)
		{
 
			pianos.add(furnishing);
		}
	}

	// setup goals for each cowboy
	int piano_index = 0;
	for(int i = 0; i < this.player.cowboys.size(); i++)
	{
		Cowboy cowboy = this.player.cowboys.get(i);
		if(cowboysToHelpers.get(cowboy.id) != null)
		{
			if(cowboysToHelpers.get(cowboy.id).goal.IsFinished)
			{
				cowboysToHelpers.get(cowboy.id).goal = new PianoGoal(game, pianos.get(piano_index).id);
			}
			cowboysToHelpers.get(cowboy.id).SetCowboy(cowboy);
		}
		else
		{
		  			cowboysToHelpers.put(cowboy.id, new CowboyHelper(cowboy, new PianoGoal(game, pianos.get(piano_index).id)));
		}
		piano_index += 1;
		if(piano_index >= pianos.size())
		{
			piano_index = 0;
		}
	}

	for (Map.Entry<String, CowboyHelper> entry : cowboysToHelpers.entrySet())
	{
		String key = entry.getKey();
		CowboyHelper value = entry.getValue();
		System.out.println("Cowboy id: " + key + " has goal " + value.goal);
	}

		// A random generator we use to do random silly things
	Random random = new Random();

	// 1. Try to spawn a cowboy.

	// Randomly select a job.
	String newJob = this.game.jobs.get(random.nextInt(this.game.jobs.size()));



	for (CowboyHelper cowboyHelper : GeneratePianoGoals())
	{
		cowboyHelper.Act();
	}

	System.out.println("Putting x number");
	for (CowboyHelper cowboyHelper : GeneratePianoGoals())
	{
		cowboyHelper.Act();
	}

	//Spawn a new cowboy if we can safely do it.
	if(this.player.youngGun.canCallIn)
	{
		//Make sure we don't have one of our cowboys here
		if(!(this.player.youngGun.callInTile.cowboy && this.player.youngGun.callInTile.cowboy.owner == _game.currentPlayer))
		{
			//Spawn a cowboy with a random job
			boolean newJobMade = false;
			int tryCount = 0;

			while(newJobMade == false && tryCount < 20)
			{
				tryCount++

				// Count cowboys with selected job
				int jobCount = 0;
				for (int i = 0; i < this.player.cowboys.size(); i++)
				{
					Cowboy cowboy = this.player.cowboys.get(i);

					if(!cowboy.isDead && cowboy.job.equals(newJob))
					{
						jobCount++;
					}
				}

				// Call in the new cowboy with that job if there aren't too many
				//   cowboys with that job already.
				if (jobCount < this.game.maxCowboysPerJob)
				{
					newJobMade = true;
					this.player.youngGun.callIn(newJob);
				}
			}
		}
	}






		System.out.println("Ending my turn.");

		return true;
	}
	/**
	 * A very basic path finding algorithm (Breadth First Search) that when given a starting Tile, will return a valid path to the goal Tile.
	 * @param  start  the starting Tile
	 * @param  goal  the goal Tile
	 * @return A List of Tiles representing the path, the the first element being a valid adjacent Tile to the start, and the last element being the goal. Or an empty list if no path found.
	 */
	// List<Tile> findPath(Tile start, Tile goal)
	// {
	// 	// no need to make a path to here...
	// 	if (start == goal)
	// 	{
	// 		return new ArrayList<Tile>();
	// 	}

	// 	// the tiles that will have their neighbors searched for 'goal'
	// 	Queue<Tile> fringe = new LinkedList<Tile>();

	// 	// How we got to each tile that went into the fringe.
	// 	HashMap<Tile, Tile> cameFrom = new HashMap<Tile, Tile>();

	// 	// Enqueue start as the first tile to have its neighbors searched.
	// 	fringe.add(start);

	// 	// keep exploring neighbors of neighbors... until there are no more.
	// 	while (!fringe.isEmpty()) {
	// 		// the tile we are currently exploring.
	// 		Tile inspect = fringe.remove();

	// 		// cycle through the tile's neighbors.
	// 		List<Tile> neighbors = inspect.getNeighbors();
	// 		for (int i = 0; i < neighbors.size(); i++)
	// 		{
	// 			Tile neighbor = neighbors.get(i);

	// 			// If we found the goal we've found the path!
	// 			if (neighbor == goal)
	// 			{
	// 				// Follow the path backward starting at the goal and return it.
	// 				List<Tile> path = new ArrayList<Tile>();
	// 				path.add(goal);

	// 				// Starting at the tile we are currently at, insert them retracing our steps till we get to the starting tile
	// 				for (Tile step = inspect; step != start; step = cameFrom.get(step))
	// 				{
	// 					path.add(0, step);
	// 				}

	// 				return path;
	// 			}

	// 			// if the tile exists, has not been explored or added to the fringe yet, and it is pathable
	// 			if (neighbor != null && !cameFrom.containsKey(neighbor) && neighbor.isPathable())
	// 			{
	// 				// add it to the tiles to be explored and add where it came from.
	// 				fringe.add(neighbor);
	// 				cameFrom.put(neighbor, inspect);
	// 			}

	// 		} // for each neighbor

	// 	} // while fringe not empty

	// 	// if you're here, that means that there was not a path to get to where you want to go.
	// 	//   in that case, we'll just return an empty path.
	// 	return new ArrayList<Tile>();
	// }
}
