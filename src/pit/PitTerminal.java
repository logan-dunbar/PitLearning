package pit;

import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.state.State;

public class PitTerminal implements TerminalFunction {

	@Override
	public boolean isTerminal(State s) {
		PitState ps = (PitState) s;
		return ps.pit_dx == 0 && ps.pit_dy == 0;
	}

}
