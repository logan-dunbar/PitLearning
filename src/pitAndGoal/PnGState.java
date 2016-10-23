package pitAndGoal;

import static pitAndGoal.PnGDomainGenerator.GOAL_DX;
import static pitAndGoal.PnGDomainGenerator.GOAL_DY;
import static pitAndGoal.PnGDomainGenerator.PIT_DX;
import static pitAndGoal.PnGDomainGenerator.PIT_DY;

import java.util.Arrays;
import java.util.List;

import burlap.mdp.core.state.MutableState;
import burlap.mdp.core.state.State;
import burlap.mdp.core.state.StateUtilities;
import burlap.mdp.core.state.UnknownKeyException;

public class PnGState implements MutableState {

	public int pit_dx;
	public int pit_dy;

	public int goal_dx;
	public int goal_dy;

	private final static List<Object> keys = Arrays.<Object> asList(PIT_DX, PIT_DY, GOAL_DX, GOAL_DY);

	public PnGState() {
	}

	public PnGState(int pit_dx, int pit_dy, int goal_dx, int goal_dy) {
		this.pit_dx = pit_dx;
		this.pit_dy = pit_dy;

		this.goal_dx = goal_dx;
		this.goal_dy = goal_dy;
	}

	@Override
	public List<Object> variableKeys() {
		return keys;
	}

	@Override
	public Object get(Object variableKey) {
		if (variableKey.equals(PIT_DX)) {
			return this.pit_dx;
		} else if (variableKey.equals(PIT_DY)) {
			return this.pit_dy;
		} else if (variableKey.equals(GOAL_DX)) {
			return this.goal_dx;
		} else if (variableKey.equals(GOAL_DY)) {
			return this.goal_dy;
		}
		throw new UnknownKeyException(variableKey);
	}

	@Override
	public MutableState set(Object variableKey, Object value) {
		if (variableKey.equals(PIT_DX)) {
			this.pit_dx = StateUtilities.stringOrNumber(value).intValue();
		} else if (variableKey.equals(PIT_DY)) {
			this.pit_dy = StateUtilities.stringOrNumber(value).intValue();
		} else if (variableKey.equals(GOAL_DX)) {
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
		return new PnGState(this.pit_dx, this.pit_dy, this.goal_dx, this.goal_dy);
	}

}
