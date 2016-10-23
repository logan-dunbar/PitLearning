package pitAndGoal;

import static pitAndGoal.PnGDomainGenerator.ACTION_EAST;
import static pitAndGoal.PnGDomainGenerator.ACTION_NORTH;
import static pitAndGoal.PnGDomainGenerator.ACTION_SOUTH;
import static pitAndGoal.PnGDomainGenerator.ACTION_WEST;

import java.util.ArrayList;
import java.util.List;

import burlap.mdp.core.StateTransitionProb;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.model.statemodel.FullStateModel;

public class PnGStateModel implements FullStateModel {

	private int[] worldSize;
	// private int[] pitLoc;
	private int[] goalLoc;

	protected double[][] transitionProbs;

	public PnGStateModel(double prob, int[] worldSize, int[] pitLoc, int[] goalLoc) {
		this.worldSize = worldSize;
		// this.pitLoc = pitLoc;
		this.goalLoc = goalLoc;

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
		PnGState ps = (PnGState) s.copy();
		int cur_pdx = ps.pit_dx;
		int cur_pdy = ps.pit_dy;
		int cur_gdx = ps.goal_dx;
		int cur_gdy = ps.goal_dy;

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
		int[] newPos = this.moveResult(cur_pdx, cur_pdy, cur_gdx, cur_gdy, dir);

		// set the new position
		ps.pit_dx = newPos[0];
		ps.pit_dy = newPos[1];
		ps.goal_dx = newPos[2];
		ps.goal_dy = newPos[3];

		// return the state we just modified
		return ps;
	}

	@Override
	public List<StateTransitionProb> stateTransitions(State s, Action a) {
		// get agent current position
		PnGState ps = (PnGState) s;

		int cur_pdx = ps.pit_dx;
		int cur_pdy = ps.pit_dy;
		int cur_gdx = ps.goal_dx;
		int cur_gdy = ps.goal_dy;

		int adir = actionDir(a);

		List<StateTransitionProb> tps = new ArrayList<StateTransitionProb>(4);
		StateTransitionProb noChange = null;
		for (int i = 0; i < 4; i++) {

			int[] newPos = this.moveResult(cur_pdx, cur_pdy, cur_gdx, cur_gdy, i);
			if (newPos[0] != cur_pdx || newPos[1] != cur_pdy || newPos[2] != cur_gdx || newPos[3] != cur_gdy) {
				// new possible outcome
				PnGState ns = (PnGState) ps.copy();
				ns.pit_dx = newPos[0];
				ns.pit_dy = newPos[1];
				ns.goal_dx = newPos[2];
				ns.goal_dy = newPos[3];

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

	protected int[] moveResult(int cur_pdx, int cur_pdy, int cur_gdx, int cur_gdy, int direction) {

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

		int n_pdx = cur_pdx + xdelta;
		int n_pdy = cur_pdy + ydelta;
		int n_gdx = cur_gdx + xdelta;
		int n_gdy = cur_gdy + ydelta;

		// make sure new position is valid (only need to use pit or goal)
		if (n_gdx < 0 - this.goalLoc[0] || n_gdx >= this.worldSize[0] - this.goalLoc[0] || n_gdy < 0 - this.goalLoc[1]
				|| n_gdy >= this.worldSize[1] - this.goalLoc[1]) {
			n_pdx = cur_pdx;
			n_pdy = cur_pdy;
			n_gdx = cur_gdx;
			n_gdy = cur_gdy;
		}

		return new int[] { n_pdx, n_pdy, n_gdx, n_gdy };
	}

}
