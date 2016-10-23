package pit;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.model.RewardFunction;

public class PitReward implements RewardFunction {

	@Override
	public double reward(State s, Action a, State s_prime) {
		PitState ps = (PitState) s_prime;
		return ps.pit_dx == 0 && ps.pit_dy == 0 ? -50 : 0;
	}

}
