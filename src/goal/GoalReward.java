package goal;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.model.RewardFunction;

public class GoalReward implements RewardFunction {

	@Override
	public double reward(State s, Action a, State sprime) {
		GoalState gs = (GoalState) sprime;
		return gs.goal_dx == 0 && gs.goal_dy == 0 ? 100 : -0.5;
	}

}
