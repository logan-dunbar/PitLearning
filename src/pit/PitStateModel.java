package pit;

import static pit.PitDomainGenerator.ACTION_EAST;
import static pit.PitDomainGenerator.ACTION_NORTH;
import static pit.PitDomainGenerator.ACTION_SOUTH;
import static pit.PitDomainGenerator.ACTION_WEST;

import java.util.ArrayList;
import java.util.List;

import burlap.mdp.core.StateTransitionProb;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.model.statemodel.FullStateModel;

public class PitStateModel implements FullStateModel {

	private int[] worldSize;
	private int[] pitLoc;

	protected double[][] transitionProbs;

	public PitStateModel(double prob, int[] worldSize, int[] pitLoc) {
		this.worldSize = worldSize;
		this.pitLoc = pitLoc;

		this.transitionProbs = new double[4][4];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				double p = i != j ? (1 - prob) / 3 : prob;
				transitionProbs[i][j] = p;
			}
		}
	}

	@Override
	public State sample(State s, Action a) {
		// s = s.copy();
		PitState ps = (PitState) s.copy();
		int cur_dx = ps.pit_dx;
		int cur_dy = ps.pit_dy;

		int adir = actionDir(a);

		// sample direction with random roll
		double r = Math.random();
		double sumProb = 0.;
		int dir = 0;
		for (int i = 0; i < 4; i++) {
			sumProb += this.transitionProbs[adir][i];
			if (r < sumProb) {
				dir = i;
				break; // found direction
			}
		}

		// get resulting position
		int[] newPos = this.moveResult(cur_dx, cur_dy, dir);

		// set the new position
		ps.pit_dx = newPos[0];
		ps.pit_dy = newPos[1];

		// return the state we just modified
		return ps;
	}

	@Override
	public List<StateTransitionProb> stateTransitions(State s, Action a) {
		// get agent current position
		PitState ps = (PitState) s;

		int cur_dx = ps.pit_dx;
		int cur_dy = ps.pit_dy;

		int adir = actionDir(a);

		List<StateTransitionProb> tps = new ArrayList<StateTransitionProb>(4);
		StateTransitionProb noChange = null;
		for (int i = 0; i < 4; i++) {

			int[] newPos = this.moveResult(cur_dx, cur_dy, i);
			if (newPos[0] != cur_dx || newPos[1] != cur_dy) {
				// new possible outcome
				PitState ns = (PitState) ps.copy();
				ns.pit_dx = newPos[0];
				ns.pit_dy = newPos[1];

				// create transition probability object and add to our list of outcomes
				tps.add(new StateTransitionProb(ns, this.transitionProbs[adir][i]));
			} else {
				// this direction didn't lead anywhere new
				// if there are existing possible directions
				// that wouldn't lead anywhere, aggregate with them
				if (noChange != null) {
					noChange.p += this.transitionProbs[adir][i];
				} else {
					// otherwise create this new state and transition
					noChange = new StateTransitionProb(s.copy(), this.transitionProbs[adir][i]);
					tps.add(noChange);
				}
			}
		}

		return tps;
	}

	protected int actionDir(Action a) {
		int adir = -1;
		if (a.actionName().equals(ACTION_NORTH)) {
			adir = 0;
		} else if (a.actionName().equals(ACTION_SOUTH)) {
			adir = 1;
		} else if (a.actionName().equals(ACTION_EAST)) {
			adir = 2;
		} else if (a.actionName().equals(ACTION_WEST)) {
			adir = 3;
		}
		return adir;
	}

	protected int[] moveResult(int cur_dx, int cur_dy, int direction) {

		// first get change in x and y from direction using 0: north; 1: south; 2:east; 3: west
		int xdelta = 0;
		int ydelta = 0;
		if (direction == 0) {
			ydelta = 1;
		} else if (direction == 1) {
			ydelta = -1;
		} else if (direction == 2) {
			xdelta = 1;
		} else {
			xdelta = -1;
		}

		int n_dx = cur_dx + xdelta;
		int n_dy = cur_dy + ydelta;

		// make sure new position is valid (not a wall or off bounds)
		if (n_dx < 0 - this.pitLoc[0] || n_dx >= this.worldSize[0] - this.pitLoc[0] || n_dy < 0 - this.pitLoc[1]
				|| n_dy >= this.worldSize[1] - this.pitLoc[1]) {
			n_dx = cur_dx;
			n_dy = cur_dy;
		}

		return new int[] { n_dx, n_dy };
	}
}
