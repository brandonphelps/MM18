// package games.saloon;





// /////////////////////NOTE THAT THIS WILL LIKELY GO AWAY AS IT IS NOT VERY BENEFICIAL. THERE ARE MANY OTHER HAZARDS AND THIS ONLY HANDLES BOTTLES.








// public class BottleAvoidance
// {
// 	static Bottle CowboyNeedsToAvoidBottle(Game game, CowboyHelper cowboyH)//TODO: CONSIDER THE DIRECTION WE WANT TO GO AND THE DIRECTION OF THE BOTTLE.
// 	{
// 		Tile nextTile = cowboyH.FutureTile(1);
// 		for(Bottle b: game.bottles)
// 		{
// 			if(GetFutureBottlePosition(game, b, 1) == nextTile)
// 			{
// 				//This bottle is going to hit us if we don't do anything.
// 				return b;
// 			}
// 		}

// 		//No need to avoid.
// 		return null;
// 	}


// 	static boolean AvoidBottle(Game game, CowboyHelper cowboyH)
// 	{
// 		Tile moveTarget = GetBottleAvoidanceSuggestion(game, cowboyH);
// 		if(moveTarget != null)
// 		{
// 			//We found somewhere to go.
// 			if(cowboyH.cowboy.move(moveTarget) == false)
// 			{
// 				//The move failed somehow.
// 				System.out.println("Failed to move cowboy  cowboy id:" + cowboyH.cowboy.id + "   tile x, y = (" + moveTarget.x + ", " + moveTarget.y + ")");
// 				return false;
// 			}
// 			return true;
// 		} else
// 		{
// 			//Failed to find a place to move.
// 			System.out.println("Couldn't find a place where cowboy "+ cowboyH.cowboy.id+"could go to avoid the bottle.");
// 			return false;
// 		}
// 	}


// 	//Returns null if it doesn't suggest anywhere specific to go.
// 	static Tile GetBottleAvoidanceSuggestion(Game game, CowboyHelper cowboyH)/////TODO: MAYBE MAKE THIS LOOK AHEAD MORE!
// 	{
// 		Bottle bottle = CowboyNeedsToAvoidBottle(game, cowboyH);
// 		if(bottle == null)
// 			return null;


// 		Tile targetTile = cowboyH.TargetTile();
// 		Tile nextTile = cowboyH.FutureTile(1);
// 		Tile currentTile = cowboyH.cowboy.tile;
// 		if(currentTile == null)
// 		{
// 			//Something went wrong.
// 			return null;
// 		}
// 		if((nextTile == null) || (targetTile == null) || (nextTile == currentTile))
// 		{
// 			//We are where we want to go. Try to avoid without moving far.
// 			Tile direction1, direction2, direction3;
// 			if(currentTile.north)





// 		} else
// 		{
// 			//We are trying to go somewhere else.
// 			int targetXOffset = targetTile.x - currentTile.x;
// 			int targetYOffset = targetTile.y - currentTile.y;/////TODO: CHECK THIS!!!

// 			Tile positiveAvoidanceTile;
// 			Tile negativeAvoidanceTile;
// 			boolean preferPositiveDirection;
// 			if(bottle.direction == Constants.EAST || bottle.direction == Constants.WEST)////TODO: MAYBE OPTIMIZE AND DON'T ALWAYS DO X FIRST. OPTIMIZE ON SHORTEST PATH.
// 			{
// 				//We will be avoiding by moving north or south.
// 				positiveAvoidanceTile = currentTile.tileSouth;
// 				negativeAvoidanceTile = currentTile.tileNorth;
// 				preferPositiveDirection = (targetYOffset > 0);
// 			} else if(bottle.direction == Constants.NORTH || bottle.direction == Constants.SOUTH)
// 			{
// 				//We will be avoiding by moving east or west.
// 				positiveAvoidanceTile = currentTile.tileEast;
// 				negativeAvoidanceTile = currentTile.tileWest;
// 				preferPositiveDirection = (targetXOffset > 0);
// 			} else
// 			{
// 				System.out.println("Something weird in bottle avoidance(1)");
// 				return null;
// 			}

// 			if(positiveAvoidanceTile.isPathable() == false && negativeAvoidanceTile.isPathable() == false)
// 			{
// 				//We need to avoid, but there isn't anything we can do.
// 				return null;
// 			} else if(positiveAvoidanceTile.isPathable() == false)
// 			{
// 				return negativeAvoidanceTile;
// 			} else if(negativeAvoidanceTile.isPathable() == false)
// 			{
// 				return positiveAvoidanceTile;
// 			} else
// 			{
// 				//We have our choice in direction.
// 				if(preferPositiveDirection)
// 				{
// 					return positiveAvoidanceTile;
// 				} else
// 				{
// 					return negativeAvoidanceTile;
// 				}
// 			}
// 		}

// 		return null;
// 	}


// 	//Returns the tile where the bottle will be in turnOffset turns. returns null if the bottle will not exist.
// 	static Tile GetFutureBottlePosition(Game game, Bottle bottle, int turnOffset)
// 	{
// 		if(bottle.isDestroyed)
// 		{
// 			return null;
// 		}
// 		int xOffsetPerTurn = 0;
// 		int yOffsetPerTurn = 0;
// 		boolean directionIsX = false;
// 		switch(bottle.direction)
// 		{
// 			case Constants.NORTH:
// 				yOffsetPerTurn = 2;//////////TODO: IS THIS CORRECT? is it this simple?
// 				directionIsX = false;
// 				break;

// 			case Constants.SOUTH:
// 				yOffsetPerTurn = -2;
// 				directionIsX = false;
// 				break;

// 			case Constants.WEST:
// 				xOffsetPerTurn = -2;
// 				directionIsX = true;
// 				break;

// 			case Constants.EAST:
// 				xOffsetPerTurn = 2;
// 				directionIsX = true;
// 				break;

// 			default:
// 				System.out.println("Invalid bottle direction (GetFutureBottlePosition).");
// 				break;
// 		}

// 		int futureXPos = bottle.tile.x + xOffsetPerTurn * turnOffset;
// 		int futureYPos = bottle.tile.y + yOffsetPerTurn * turnOffset;

// 		//Make sure there is no table or piano in the way.
// 		if(directionIsX)
// 		{
// 			for(int i = bottle.tile.x; i < futureXPos; i++)
// 			{
// 				if(game.getTileAt(i, futureYPos).furnishing != null)
// 				{
// 					//A table will block this bottle.
// 					return null;
// 				}
// 			}
// 		} else
// 		{
// 			for(int i = bottle.tile.y; i < futureYPos; i++)
// 			{
// 				if(game.getTileAt(futureXPos, i).furnishing != null)
// 				{
// 					//A table will block this bottle.
// 					return null;
// 				}
// 			}
// 		}

// 		return game.getTileAt(futureXPos, futureYPos);
// 	}
	
// }