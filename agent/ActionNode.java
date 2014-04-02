package edu.utep.cs.ai.agent;

import ch.idsia.benchmark.mario.engine.sprites.Mario;

/**
 * ActionNode with helper functions that represent a node/state
 * for creating an A* search tree.
 * @author Edward Moreno
 */
public class ActionNode implements Comparable<Object>
{
	private static final int MAX_VIEW_RANGE = 11;
	private boolean action[];
	private int heuristicValue;
	private int pathCost;
	private ActionNode parentAction;
	private int distanceCovered;

	/**
	 * Constructs a base ActionNode based on given values.
	 * @param action the action chosen
	 * @param parent the parent node
	 * @param pathCost the cost of traveling this path, default of zero
	 * @param distanceCovered the distance that has been covered so far, default of zero
	 * @param heuristicValue the heuristic to assign to this state, default of zero
	 */
	public ActionNode(boolean[] action, ActionNode parent, int pathCost, int distanceCovered, int heuristicValue)
	{
		this.action = new boolean[action.length];
		for(int i = 0; i < action.length; i++)
		{
			this.action[i] = action[i];
		}

		this.parentAction = parent;
		this.pathCost = pathCost;
		this.distanceCovered = distanceCovered;
		this.heuristicValue = heuristicValue;
	}
	
	/**
	 * Constructs a new ActionNode based off a parent node.
	 * @param action the action to assign to the state
	 * @param parent the parent of this state
	 * @param levelScene the state of the environment terrain
	 * @param enemies the state of enemies in the environment
	 * @param marioEgoRow Mario's current row in the scene
	 * @param marioEgoCol Mario's current column in the scene
	 */
	public ActionNode(boolean[] action, ActionNode parent, byte[][] levelScene, byte[][] enemies, int marioEgoRow, int marioEgoCol)
	{
		this.action = new boolean[action.length];
		for(int i = 0; i < action.length; i++)
		{
			this.action[i] = action[i];
		}

		this.parentAction = parent;
		this.pathCost = parent.getPathCost() + 1;//One more action taken
		this.distanceCovered = calculateDistanceCovered() + parent.distanceCovered; //Parent action cost is part of this
		this.heuristicValue = calculateHeuristic(levelScene, enemies, marioEgoRow, marioEgoCol);//get h(n)
	}
	
	/**
	 * Helper function to estimate the distance that an action covers.
	 * @return the estimation of distance.
	 */
	private int calculateDistanceCovered() {
		int distance = 1; //Always moving in one direction or another

		if(action[Mario.KEY_SPEED]) //Sprinting so move an extra block
		{
			distance++;
		}

		if(action[Mario.KEY_JUMP])//Jumping gives more distance
		{
			distance++;
		}

		if(action[Mario.KEY_LEFT])//Moving opposite direction
		{
			distance *= -1;
		}

		return distance;
	}

	/**
	 * Calculates h(n) by calculating the distance from the goal and assigns a large
	 * negative value if the heuristic would cause Mario to get hurt or stuck.
	 * 
	 * @param levelScene the state of the environment terrain
	 * @param enemies the state of the enemies in the environment
	 * @param marioEgoRow Mario's current row in the scene
	 * @param marioEgoCol Mario's current column in the scene
	 * @return the heuristic value for this state
	 */
	private int calculateHeuristic(byte[][] levelScene, byte[][] enemies, int marioEgoRow, int marioEgoCol) 
	{
		int heuristic = MAX_VIEW_RANGE - distanceCovered;
		
		//Jumping
		if(action[Mario.KEY_JUMP])
		{
			//Sprinting
			if(action[Mario.KEY_SPEED])
			{
				int[] coords = {1,1,2,1,3,1,4,1,4,2};
				if(findDanger(coords, levelScene, enemies, marioEgoRow, marioEgoCol))
				{
					return heuristic - 100;
				}
			}
			//Simple jump
			else
			{
				int[] coords = {1,1,2,1,3,1,3,2};
				if(findDanger(coords, levelScene, enemies, marioEgoRow, marioEgoCol))
				{
					return heuristic - 100;
				}
			}
		}

		//Not jumping
		else
		{
			//Sprinting
			if(action[Mario.KEY_SPEED])
			{
				int[] coords = {0,1,0,2};
				if(findDanger(coords, levelScene, enemies, marioEgoRow, marioEgoCol))
				{
					return heuristic - 100;
				}
			}
			//Simple Movement
			else
			{
				int[] coords = {0,1};
				if(findDanger(coords, levelScene, enemies, marioEgoRow, marioEgoCol))
				{
					return heuristic - 100;
				}
			}
		}
		return heuristic;
	}
	/**
	 * Detects whether an obstacle exist in the direction of the currently selected action.
	 * @param coordinatesToCheck the coordinates to search in
	 * @param levelScene the state of the environment terrain
	 * @param enemies the state of the enemies in the environment
	 * @param marioEgoRow Mario's current row in the scene
	 * @param marioEgoCol Mario's current column in the scene
	 * @return whether there is danger in performing this move
	 */
	private boolean findDanger(int[] coordinatesToCheck, byte[][] levelScene, 
			byte[][] enemies, int marioEgoRow, int marioEgoCol) 
	{
		int direction = 1; //moving right
		if(action[Mario.KEY_LEFT])//moving left
		{
			direction = -1; 
		}
		for(int i = 0; i < coordinatesToCheck.length; i+=2)
		{
			int x = coordinatesToCheck[i] * direction;
			int y = coordinatesToCheck[i+1] * direction;
			if(enemies[marioEgoRow + x][marioEgoRow + y] != 0)
			{
				return true;
			}
			
			if(levelScene[marioEgoRow + x][marioEgoRow + y] != 0)
			{
				return true;
			}
			
		}
		return false;
	}
	/**
	 * @return the heuristicValue
	 */
	public int getHeuristicValue() 
	{
		return heuristicValue;
	}

	/**
	 * @param heuristicValue the heuristicValue to set
	 */
	public void setHeuristicValue(int heuristicValue) 
	{
		this.heuristicValue = heuristicValue;
	}

	/**
	 * @return the pathCost
	 */
	public int getPathCost()
	{
		return pathCost;
	}

	/**
	 * @return the action taken
	 */
	public boolean[] getAction()
	{
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(boolean[] action) 
	{
		this.action = action;
	}

	/**
	 * @return the parentAction
	 */
	public ActionNode getParentAction()
	{
		return parentAction;
	}

	/**
	 * @param parentAction the parentAction to set
	 */
	public void setParentAction(ActionNode parentAction) 
	{
		this.parentAction = parentAction;
	}

	/**
	 * @return the distanceCovered
	 */
	public int getDistanceCovered() 
	{
		return distanceCovered;
	}
	/**
	 * @param distanceCovered the distanceCovered to set
	 */
	public void setDistanceCovered(int distanceCovered) 
	{
		this.distanceCovered = distanceCovered;
	}

	/**
	 * Compares f(n) of two functions where f(n) = g(n) + h(n). g(n) is path cost and h(n) is heuristic
	 */
	@Override
	public int compareTo(Object otherAction) 
	{
		return (this.pathCost + this.heuristicValue) -
				(((ActionNode) otherAction).getHeuristicValue() + 
						((ActionNode) otherAction).getPathCost());
	}

}
