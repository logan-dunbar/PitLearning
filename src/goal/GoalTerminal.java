package goal;

import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.state.State;

public class GoalTerminal implements TerminalFunction {

	@Override
	public boolean isTerminal(State s) {
		GoalState gs = (GoalState) s;
		return gs.goal_dx == 0 && gs.goal_dy == 0;
	}

}
