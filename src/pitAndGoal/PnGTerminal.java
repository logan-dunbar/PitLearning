package pitAndGoal;

import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.state.State;

public class PnGTerminal implements TerminalFunction {

	@Override
	public boolean isTerminal(State s) {
		PnGState ps = (PnGState) s;
		return (ps.pit_dx == 0 && ps.pit_dy == 0) || (ps.goal_dx == 0 && ps.goal_dy == 0);
	}

}
