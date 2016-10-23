package goal;

import static goal.GoalDomainGenerator.GOAL_DX;
import static goal.GoalDomainGenerator.GOAL_DY;

import java.util.Arrays;
import java.util.List;

import burlap.mdp.core.state.MutableState;
import burlap.mdp.core.state.State;
import burlap.mdp.core.state.StateUtilities;
import burlap.mdp.core.state.UnknownKeyException;

public class GoalState implements MutableState {

	public int goal_dx;
	public int goal_dy;

	private final static List<Object> keys = Arrays.<Object> asList(GOAL_DX, GOAL_DY);

	public GoalState() {
	}

	public GoalState(int goal_dx, int goal_dy) {
		this.goal_dx = goal_dx;
		this.goal_dy = goal_dy;
	}

	@Override
	public List<Object> variableKeys() {
		return keys;
	}

	@Override
	public Object get(Object variableKey) {
		if (variableKey.equals(GOAL_DX)) {
			return this.goal_dx;
		} else if (variableKey.equals(GOAL_DY)) {
			return this.goal_dy;
		}
		throw new UnknownKeyException(variableKey);
	}

	@Override
	public MutableState set(Object variableKey, Object value) {
		if (variableKey.equals(GOAL_DX)) {
			this.goal_dx = StateUtilities.stringOrNumber(value).intValue();
		} else if (variableKey.equals(GOAL_DY)) {
			this.goal_dy = StateUtilities.stringOrNumber(value).intValue();
		} else {
			throw new UnknownKeyException(variableKey);
		}
		return this;
	}

	@Override
	public State copy() {
		return new GoalState(this.goal_dx, this.goal_dy);
	}

}
