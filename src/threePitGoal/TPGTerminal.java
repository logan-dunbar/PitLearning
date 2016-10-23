package threePitGoal;

import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.state.State;

public class TPGTerminal implements TerminalFunction {
	@Override
	public boolean isTerminal(State s) {
		TPGState ps = (TPGState) s;
		return (ps.pit1_dx == 0 && ps.pit1_dy == 0) || (ps.pit2_dx == 0 && ps.pit2_dy == 0)
				|| (ps.pit3_dx == 0 && ps.pit3_dy == 0) || (ps.goal_dx == 0 && ps.goal_dy == 0);
	}
}
