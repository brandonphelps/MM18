package games.saloon;


public class DangerAvoidance
{
	static int brawlerDangerValue = 20;
	static int brawlerDangerDropoff = 5;

	static int glassDangerValue = 40;

	static int bottleDangerValue = 100;
	static int bottleDangerDropoff = 20;

	static int bartenderDangerValue = 100;
	static int bartenderDangerDropoff = 20;

	static int sharpshooterDangerValue = 40;
	static int sharpshooterDropoff = 10;

	static int youngGunDangerValue = 60;
	static int youngGunDropoff = 20;



	public static void avoidDangerAndMove(Game game, Cowboy cowboy, Tile moveTile)
	{
		int moveTileDanger = DangerAvoidance.CalculateTileDanger(game, moveTile, cowboy);
		Tile alternateTileA, alternateTileB;
		if(moveTile == cowboy.tile.tileNorth || moveTile == cowboy.tile.tileSouth)
		{
		  //We are moving on the y axis. alternate tiles are on either side on x axis.
		  alternateTileB = cowboy.tile.tileWest;
		  alternateTileA = cowboy.tile.tileEast;
		}
		else if(moveTile == cowboy.tile.tileEast || moveTile == cowboy.tile.tileWest)
		{
		  alternateTileA = cowboy.tile.tileNorth;///////////TODO: OPTIMIZE THIS!
		  alternateTileB = cowboy.tile.tileSouth;
		}
		else
		{
		  System.out.println("Error finding alternate route.");
		  alternateTileA = moveTile;
		  alternateTileB = moveTile;
		}


		System.out.println("AlternateB: " + alternateTileB);
		System.out.println("Game: " + game);
		System.out.println("Cowboy: " + cowboy);

		if(!alternateTileA.isPathable())
		{
		  System.out.println("AlternateA: " + alternateTileA + " is not pathable");
		}

		if(!alternateTileB.isPathable())
		{
		  System.out.println("AlternateB: " + alternateTileB + " is not pathable");
		}

		

		//Find min danger.
		int alternateADanger = DangerAvoidance.CalculateTileDanger(game, alternateTileA, cowboy);
		int alternateBDanger = DangerAvoidance.CalculateTileDanger(game, alternateTileB, cowboy);

		//int alternateADanger = 10;
		//int alternateBDanger = 2;
		
		int minDanger = Math.min(moveTileDanger, Math.min(alternateADanger, alternateBDanger));
		if(minDanger == alternateADanger)
		{
		  moveTile = alternateTileA;
		}
		else if(minDanger == alternateBDanger)
		{
		  moveTile = alternateTileB;
		}
	}



	//returns null if the tile is not a valid location.
	//Returns a number between 0 and 100. 100 is very dangerous.
        public static Integer CalculateTileDanger(Game game, Tile tile, Cowboy cowboy)
	{
		Integer dangerValue = 0;


		//Make sure this is a valid place to be.
		if(tile.isPathable() == false)
		{
			return 1000;
		}

		//Calculate glass danger
		if(tile.hasHazard)
		{
			dangerValue += glassDangerValue;
		}

		//Calculate danger due to bottles
		for(Bottle b: game.bottles)
		{
			java.util.ArrayList<Tile> bottlePath = GetBottlePath(b);
			if(bottlePath.contains(tile))
			{
				//We are in danger of a bottle hitting us. Calculate danger.
				int effectiveBottleDangerVal = bottleDangerValue - bottleDangerDropoff*ManhattanDistance(b.tile, tile);
				if(effectiveBottleDangerVal > 0)
				{
					dangerValue += effectiveBottleDangerVal;
				}
			}
		}


		//Calculate danger to other people
		for(Cowboy c: game.cowboys)
		{
			if(!c.isDead && c != cowboy)
			{
				//All brawlers are dangerous.
				if(c.job == Constants.BRAWLER)//TODO: maybe treat our brawlers differently if we know their path.
				{
					dangerValue += ThingsDangerToTile(ManhattanDistance(tile, c.tile), brawlerDangerValue, brawlerDangerDropoff);///TODO: PROBABLY SHOULD CHECK THESE. COULD THEY BE OFF BY ONE?
				}
				else if(c.owner != game.currentPlayer)
				{
					//This cowboy is no our friend.
					if(c.job == Constants.SHARPSHOOTER)
					{
						int turnsUntilDangerous = ManhattanDistance(tile, c.tile) - c.focus;
						if(turnsUntilDangerous < 0)
							turnsUntilDangerous = 0;
						dangerValue += ThingsDangerToTile(turnsUntilDangerous, sharpshooterDangerValue, sharpshooterDropoff);
						

					} else if(c.job == Constants.BARTENDER)
					{
						int turnsUntilDangerous = c.turnsBusy;//todo: offset by one?
						dangerValue += ThingsDangerToTile(ManhattanDistance(tile, c.tile)+turnsUntilDangerous, bartenderDangerValue, bartenderDangerDropoff);
					} else
					{
						System.out.println("Invalid job " + c.job);
					}
				}
			}
		}


		//Calculate danger due to young guns
		for(Player p: game.players)
		{
			YoungGun youngGun = p.youngGun;
			int turnsUntilDangerous;
			if(youngGun.tile.y == 0)
			{
				//Younggun is going to the right
				turnsUntilDangerous = tile.x - youngGun.tile.x;

			} else if(youngGun.tile.y == game.mapHeight-1)
			{
				//Younggun is going left
				turnsUntilDangerous = youngGun.tile.x - tile.x;

			} else if(youngGun.tile.x == 0)
			{
				//Younggun is going up
				turnsUntilDangerous = tile.y - youngGun.tile.y;

			} else if(youngGun.tile.x == game.mapWidth-1)
			{
				turnsUntilDangerous = youngGun.tile.y - tile.y;

			} else
			{
				System.out.println("Couldnt locate younggun "+youngGun.tile.x+", "+youngGun.tile.y);
				turnsUntilDangerous = -1;//this will cause us to skip this one.
			}

			//the youngun passed the tile already. not dangerous.
			if(turnsUntilDangerous < 0)
				continue;

			dangerValue += ThingsDangerToTile(ManhattanDistance(tile, youngGun.tile)+turnsUntilDangerous, youngGunDangerValue, youngGunDropoff);

		}

		return dangerValue;
	}



	public static java.util.ArrayList<Tile> GetBottlePath(Bottle b)
	{
		java.util.ArrayList<Tile> bottlePath = new java.util.ArrayList<Tile>();

		Tile nextTile = b.tile;

		while(nextTile.isBalcony == false && nextTile.furnishing != null)
		{
			bottlePath.add	(nextTile);

			switch(b.direction)
			{
				case Constants.SOUTH:
					nextTile = nextTile.tileSouth;
					break;
				case Constants.EAST:
					nextTile = nextTile.tileEast;
					break;
				case Constants.NORTH:
					nextTile = nextTile.tileNorth;
					break;
				case Constants.WEST:
					nextTile = nextTile.tileWest;
					break;
				default:
					System.out.println("Invalid direction " + b.direction);
					return null;
			}
		}

		return bottlePath;
	}

	public static int ManhattanDistance(Tile from, Tile to)
	{
		return Math.abs((to.x - from.x) + (to.y - from.y));
	}

	public static int ThingsDangerToTile(int distanceToTile, int dangerValue, int dangerDropoff)
	{
		int retVal = dangerValue - (distanceToTile-1)*dangerDropoff;
		retVal = retVal < 0? 0: retVal;
		return retVal;
	}
}
