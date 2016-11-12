package games.saloon;


public class BottleAvoider
{
	static Bottle CowboyNeedsToAvoidBottle(Game game, CowboyHelper cowboy)//TODO: CONSIDER THE DIRECTION WE WANT TO GO AND THE DIRECTION OF THE BOTTLE.
	{
		Tile nextTile = cowboy.futureTile(1);
		for(Bottle b: game.bottles)
		{
			if(GetFutureBottlePosition(game, b, 1) == nextTile)
			{
				//This bottle is going to hit us if we don't do anything.
				return b;
			}
		}

		//No need to avoid.
		return null;
	}


	static boolean AvoidBottle(Game game, CowboyHelper cowboy)
	{
		Tile moveTarget = this.GetBottleAvoidanceSuggestion(game, cowboy);
		if(moveTarget != null)
		{
			//We found somewhere to go.
			if(cowboy.move(moveTarget) == false)
			{
				//The move failed somehow.
				system.out.println("Failed to move cowboy  cowboy id:" + cowboy.cowboy.id + "   tile x, y = (" + moveTarget.x + ", " + moveTarget.y + ")");
				return false;
			}
			return true;
		} else
		{
			//Failed to find a place to move.
			system.out.println("Couldn't find a place where cowboy "+ cowboy.cowboy.id+"could go to avoid the bottle.");
			return false;
		}
	}


	static Tile GetBottleAvoidanceSuggestion(Game game, CowboyHelper cowboy)
	{

	}


	//Returns the tile where the bottle will be in turnOffset turns. returns null if the bottle will not exist.
	static Tile GetFutureBottlePosition(Game game, Bottle bottle, int turnOffset)
	{
		if(bottle.isDestroyed())
		{
			return null;
		}
		int xOffsetPerTurn = 0;
		int yOffsetPerTurn = 0;
		bool directionIsX;
		switch(bottle.direction)
		{
			case Constants.NORTH:
				yOffsetPerTurn = 2;//////////TODO: IS THIS CORRECT? is it this simple?
				directionIsX = false;
				break;

			case Constants.SOUTH:
				yOffsetPerTurn = -2;
				directionIsX = false;
				break;

			case Constants.WEST:
				xOffsetPerTurn = -2;
				directionIsX = true;
				break;

			case Constants.EAST:
				xOffsetPerTurn = 2;
				directionIsX = true;
				break;

			default:
				system.out.println("Invalid bottle direction (GetFutureBottlePosition).");	
		}

		int futureXPos = bottle.tile.x + xOffsetPerTurn * turnOffset;
		int futureYPos = bottle.tile.y + yOffsetPerTurn * turnOffset;

		//Make sure there is no table or piano in the way.
		if(directionIsX)
		{
			for(int i = bottle.tile.x; i < futureXPos; i++)
			{
				if(game.getTileAt(i, futureYPos).furnishing != null)
				{
					//A table will block this bottle.
					return null;
				}
			}
		} else
		{
			for(int i = bottle.tile.y; i < futureYPos; i++)
			{
				if(game.getTileAt(futureXPos, i).furnishing != null)
				{
					//A table will block this bottle.
					return null;
				}
			}
		}

		return game.getTileAt(futureXPos, futureYPos);
	}
	
}