package threePitGoal;

import static threePitGoal.TPGDomainGenerator.GOAL_DX;
import static threePitGoal.TPGDomainGenerator.GOAL_DY;
import static threePitGoal.TPGDomainGenerator.PIT1_DX;
import static threePitGoal.TPGDomainGenerator.PIT1_DY;
import static threePitGoal.TPGDomainGenerator.PIT2_DX;
import static threePitGoal.TPGDomainGenerator.PIT2_DY;
import static threePitGoal.TPGDomainGenerator.PIT3_DX;
import static threePitGoal.TPGDomainGenerator.PIT3_DY;

import java.util.Arrays;
import java.util.List;

import burlap.mdp.core.state.MutableState;
import burlap.mdp.core.state.State;
import burlap.mdp.core.state.StateUtilities;
import burlap.mdp.core.state.UnknownKeyException;

public class TPGState implements MutableState {

	public int pit1_dx;
	public int pit1_dy;
	public int pit2_dx;
	public int pit2_dy;
	public int pit3_dx;
	public int pit3_dy;

	public int goal_dx;
	public int goal_dy;

	private final static List<Object> keys = Arrays.<Object> asList(PIT1_DX, PIT1_DY, PIT2_DX, PIT2_DY, PIT3_DX,
			PIT3_DY, GOAL_DX, GOAL_DY);

	public TPGState() {
	}

	public TPGState(int pit1_dx, int pit1_dy, int pit2_dx, int pit2_dy, int pit3_dx, int pit3_dy, int goal_dx,
			int goal_dy) {
		this.pit1_dx = pit1_dx;
		this.pit1_dy = pit1_dy;
		this.pit2_dx = pit2_dx;
		this.pit2_dy = pit2_dy;
		this.pit3_dx = pit3_dx;
		this.pit3_dy = pit3_dy;

		this.goal_dx = goal_dx;
		this.goal_dy = goal_dy;
	}

	@Override
	public List<Object> variableKeys() {
		return keys;
	}

	@Override
	public Object get(Object variableKey) {
		if (variableKey.equals(PIT1_DX)) {
			return this.pit1_dx;
		} else if (variableKey.equals(PIT1_DY)) {
			return this.pit1_dy;
		} else if (variableKey.equals(PIT2_DX)) {
			return this.pit2_dx;
		} else if (variableKey.equals(PIT2_DY)) {
			return this.pit2_dy;
		} else if (variableKey.equals(PIT3_DX)) {
			return this.pit3_dx;
		} else if (variableKey.equals(PIT3_DY)) {
			return this.pit3_dy;
		} else if (variableKey.equals(GOAL_DX)) {
			return this.goal_dx;
		} else if (variableKey.equals(GOAL_DY)) {
			return this.goal_dy;
		}
		throw new UnknownKeyException(variableKey);
	}

	@Override
	public MutableState set(Object variableKey, Object value) {
		if (variableKey.equals(PIT1_DX)) {
			this.pit1_dx = StateUtilities.stringOrNumber(value).intValue();
		} else if (variableKey.equals(PIT1_DY)) {
			this.pit1_dy = StateUtilities.stringOrNumber(value).intValue();
		} else if (variableKey.equals(PIT2_DX)) {
			this.pit2_dx = StateUtilities.stringOrNumber(value).intValue();
		} else if (variableKey.equals(PIT2_DY)) {
			this.pit2_dy = StateUtilities.stringOrNumber(value).intValue();
		} else if (variableKey.equals(PIT3_DX)) {
			this.pit3_dx = StateUtilities.stringOrNumber(value).intValue();
		} else if (variableKey.equals(PIT3_DY)) {
			this.pit3_dy = StateUtilities.stringOrNumber(value).intValue();
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
		return new TPGState(this.pit1_dx, this.pit1_dy, this.pit2_dx, this.pit2_dy, this.pit3_dx, this.pit3_dy,
				this.goal_dx, this.goal_dy);
	}

}
