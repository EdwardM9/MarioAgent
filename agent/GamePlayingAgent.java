/**
 * 
 */
package edu.utep.cs.ai.agent;

import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.BasicMarioAIAgent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;

/**
 * Game playing agent that uses A* as a decision making tool. Will progress through
 * the level as quickly as possible until it encounters danger, at which point
 * it will query the pathfinding algorithm for the safest way through.
 * 
 * @author Edward Moreno
 */
public class GamePlayingAgent extends BasicMarioAIAgent implements Agent {
	GreedyAStarSearch search;
	int ticks = 0;
	int jumpTimer = 0;
	boolean isJumping = false;
	/**
	 * Creates a new gameplaying agent based off the BasicMarioAgent provided by the
	 * framework.
	 */
	public GamePlayingAgent() 
	{
		super("Greedy A* Agent");
		search = new GreedyAStarSearch();
	}

	/**
	 * Function called by the engine to retrieve the move that the agent has chosen.
	 */
	public boolean[] getAction()
	{

		//Move right quickly all of the time.
		//Unless we find trouble, then find the best way out.
		if(getEnemiesCellValue(marioEgoRow, marioEgoCol + 2) != 0 || //enemy 2 to the right
				getEnemiesCellValue(marioEgoRow, marioEgoCol + 1) != 0 || //Enemy 1 to the right
				getEnemiesCellValue(marioEgoRow + 1, marioEgoCol + 1) != 0) //Enemy dropping in on us
		{
			action = search.getAction(levelScene, enemies, ticks, jumpTimer, isMarioAbleToJump);
			if(action[Mario.KEY_JUMP])
			{
				isJumping = true;
			}
		}

		//There can also be an obstacle to find our way out of
		else if((getReceptiveFieldCellValue(marioEgoRow + 2, marioEgoCol + 1) == 0 ||
				getReceptiveFieldCellValue(marioEgoRow + 1, marioEgoCol + 1) == 0) ||
				getReceptiveFieldCellValue(marioEgoRow, marioEgoCol + 1) != 0 ||
				getReceptiveFieldCellValue(marioEgoRow, marioEgoCol + 2) != 0 )
		{
			action[Mario.KEY_JUMP] = true;
			//Reduce speed so that we can perform our move properly
			action[Mario.KEY_SPEED] = false;
			isJumping = true;	
		}

		if(isJumping)
		{
			jumpTimer++;
		}

		if(jumpTimer > 15) //Ensure that the jump is of the full height
		{
			jumpTimer = 0;
			isJumping = false;
			this.reset();
		}
		return action;
	}

	/**
	 * When an action has finished or the environment has changed, reset the state
	 * of this agent.
	 */
	public void reset()
	{
		action = new boolean[Environment.numberOfKeys];
		search = new GreedyAStarSearch();
		action[Mario.KEY_SPEED] = true;
		action[Mario.KEY_RIGHT] = true;
	}
}
