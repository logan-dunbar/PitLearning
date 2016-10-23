package pit;

import static pit.PitDomainGenerator.PIT_DX;
import static pit.PitDomainGenerator.PIT_DY;

import java.util.Arrays;
import java.util.List;

import burlap.mdp.core.state.MutableState;
import burlap.mdp.core.state.State;
import burlap.mdp.core.state.StateUtilities;
import burlap.mdp.core.state.UnknownKeyException;

public class PitState implements MutableState {

	public int pit_dx;
	public int pit_dy;

	private final static List<Object> keys = Arrays.<Object> asList(PIT_DX, PIT_DY);

	public PitState() {
	}

	public PitState(int pit_dx, int pit_dy) {
		this.pit_dx = pit_dx;
		this.pit_dy = pit_dy;
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
		}
		throw new UnknownKeyException(variableKey);
	}

	@Override
	public MutableState set(Object variableKey, Object value) {
		if (variableKey.equals(PIT_DX)) {
			this.pit_dx = StateUtilities.stringOrNumber(value).intValue();
		} else if (variableKey.equals(PIT_DY)) {
			this.pit_dy = StateUtilities.stringOrNumber(value).intValue();
		} else {
			throw new UnknownKeyException(variableKey);
		}
		return this;
	}

	@Override
	public State copy() {
		return new PitState(this.pit_dx, this.pit_dy);
	}

}
