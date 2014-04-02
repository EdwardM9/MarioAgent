package edu.utep.cs.ai.agent;

import java.util.PriorityQueue;

import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;

/**
 * 	Performs a greedy A* search, that is an A* search that terminates after finding
 * the first solution. 
 * @author Edward Moreno
 */
public class GreedyAStarSearch 
{
	private PriorityQueue<ActionNode> actions;

	//Search greedily, if we find a non dangerous move that is an improvement to our value,
	// then expand it.
	//The search should be: find mario's position
	// 						generate the actions(move right, move left, jump right, jump left, sprint right,
	//											sprint left, sprint jump right, sprint jump left)
	//						give a heuristic to all of them
	//						expand the one with the highest value
	//						repeat these steps MAXIMUM_DEPTH times
	//						trace the best greedy path backwards and output the best action
	// GOAL STATE IS THE END OF WHAT WE CAN SEE, if position x is 11 units past mario,
	//				then heuristic should be 0!

	/**
	 * Using A* search, find the most valuable move. Will search until it reaches the end of
	 * the vision space
	 * @param levelScene the state of the environment terrain
	 * @param enemies the state of the enemies in the environment
	 * @param marioEgoRow Mario's current row in the scene
	 * @param marioEgoCol Mario's current column in the scene
	 * @param isMarioAbleToJump whether Mario is able to Jump
	 * @return a boolean array that shows the action selected
	 */
	public boolean[] getAction(byte[][] levelScene, byte[][] enemies, int marioEgoRow, int marioEgoCol, boolean isMarioAbleToJump)
	{
		actions = new PriorityQueue<ActionNode>();
		boolean[] action = new boolean[Environment.numberOfKeys]; //empty action
		//root node
		ActionNode startState = new ActionNode(action, null, 0, 0, 0);
		actions.add(startState);
		boolean goalFound = false;
		while(!goalFound)
		{
			generateNextActions(levelScene, enemies, marioEgoRow, marioEgoCol, isMarioAbleToJump); //Create the children of the cheapest node
			if(actions.peek().getDistanceCovered() >= 11)//We've traversed as far as we can see
			{
				goalFound = true;
			}
		}
		//Add the nodes of this tree to the priority queue
		action = findPath();
		return action;
	}

	/**
	 * Generates the future actions from the current state of the world
	 * @param levelScene the state of the environment terrain
	 * @param enemies the state of the enemies in the environment
	 * @param marioEgoRow Mario's current row in the scene
	 * @param marioEgoCol Mario's current column in the scene
	 * @param isMarioAbleToJump whether Mario is able to Jump
	 */
	private void generateNextActions(byte[][] levelScene, byte[][] enemies, int marioEgoRow, int marioEgoCol, boolean isMarioAbleToJump)
	{

		//Generate all the future actions here from the best current action.
		ActionNode currentBest = actions.remove();
		boolean[] newAction = new boolean[Environment.numberOfKeys];

		//This is ugly and hacky, but without a proper hook into physics, all we can do
		// is estimate positions and actions.
		//Move Right
		newAction[Mario.KEY_RIGHT] = true;
		actions.add(new ActionNode(newAction, currentBest, levelScene, enemies, marioEgoRow, marioEgoCol));
		//Jump Right

		if(isMarioAbleToJump)
		{
			newAction[Mario.KEY_JUMP] = true;
			actions.add(new ActionNode(newAction, currentBest, levelScene, enemies, marioEgoRow, marioEgoCol));
		}
		//Sprint Right
		newAction[Mario.KEY_JUMP] = false;
		newAction[Mario.KEY_SPEED] = true;
		actions.add(new ActionNode(newAction, currentBest, levelScene, enemies, marioEgoRow, marioEgoCol));
		//Sprint Jump Right
		if(isMarioAbleToJump)
		{
			newAction[Mario.KEY_JUMP] = true;
			actions.add(new ActionNode(newAction, currentBest, levelScene, enemies, marioEgoRow, marioEgoCol));
		}
	}

	/**
	 * Find the actual action that was found to be the most valuable.
	 * @return the action to be used
	 */
	private boolean[] findPath()
	{
		ActionNode finalPath = actions.remove();// Will trace back to the current state
		ActionNode lastChild = null; //Will hold the most valuable move from the current state
		while(finalPath.getParentAction() != null)
		{
			lastChild = finalPath;
			finalPath = finalPath.getParentAction();
		}

		return lastChild.getAction();
	}
}