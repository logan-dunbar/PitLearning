package threePitGoal;

import static threePitGoal.TPGDomainGenerator.ACTION_EAST;
import static threePitGoal.TPGDomainGenerator.ACTION_NORTH;
import static threePitGoal.TPGDomainGenerator.ACTION_SOUTH;
import static threePitGoal.TPGDomainGenerator.ACTION_WEST;

import java.util.ArrayList;
import java.util.List;

import burlap.mdp.core.StateTransitionProb;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.model.statemodel.FullStateModel;

public class TPGStateModel implements FullStateModel {

	private int[] worldSize;
	private int[] goalLoc;

	protected double[][] transitionProbs;

	public TPGStateModel(double prob, int[] worldSize, int[] goalLoc) {
		this.worldSize = worldSize;
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
		TPGState ps = (TPGState) s.copy();
		int cur_p1dx = ps.pit1_dx;
		int cur_p1dy = ps.pit1_dy;
		int cur_p2dx = ps.pit2_dx;
		int cur_p2dy = ps.pit2_dy;
		int cur_p3dx = ps.pit3_dx;
		int cur_p3dy = ps.pit3_dy;
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
		int[] newPos = this.moveResult(cur_p1dx, cur_p1dy, cur_p2dx, cur_p2dy, cur_p3dx, cur_p3dy, cur_gdx, cur_gdy,
				dir);

		// set the new position
		ps.pit1_dx = newPos[0];
		ps.pit1_dy = newPos[1];
		ps.pit2_dx = newPos[2];
		ps.pit2_dy = newPos[3];
		ps.pit3_dx = newPos[4];
		ps.pit3_dy = newPos[5];
		ps.goal_dx = newPos[6];
		ps.goal_dy = newPos[7];

		// return the state we just modified
		return ps;
	}

	@Override
	public List<StateTransitionProb> stateTransitions(State s, Action a) {
		// get agent current position
		TPGState ps = (TPGState) s;

		int cur_p1dx = ps.pit1_dx;
		int cur_p1dy = ps.pit1_dy;
		int cur_p2dx = ps.pit2_dx;
		int cur_p2dy = ps.pit2_dy;
		int cur_p3dx = ps.pit3_dx;
		int cur_p3dy = ps.pit3_dy;
		int cur_gdx = ps.goal_dx;
		int cur_gdy = ps.goal_dy;

		int adir = actionDir(a);

		List<StateTransitionProb> tps = new ArrayList<StateTransitionProb>(4);
		StateTransitionProb noChange = null;
		for (int i = 0; i < 4; i++) {

			int[] newPos = this.moveResult(cur_p1dx, cur_p1dy, cur_p2dx, cur_p2dy, cur_p3dx, cur_p3dy, cur_gdx, cur_gdy,
					i);
			if (newPos[0] != cur_p1dx || newPos[1] != cur_p1dy || newPos[2] != cur_p2dx || newPos[3] != cur_p2dy
					|| newPos[4] != cur_p3dx || newPos[5] != cur_p3dy || newPos[6] != cur_gdx || newPos[7] != cur_gdy) {
				// new possible outcome
				TPGState ns = (TPGState) ps.copy();
				ns.pit1_dx = newPos[0];
				ns.pit1_dy = newPos[1];
				ns.pit2_dx = newPos[2];
				ns.pit2_dy = newPos[3];
				ns.pit3_dx = newPos[4];
				ns.pit3_dy = newPos[5];
				ns.goal_dx = newPos[6];
				ns.goal_dy = newPos[7];

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

	protected int[] moveResult(int cur_p1dx, int cur_p1dy, int cur_p2dx, int cur_p2dy, int cur_p3dx, int cur_p3dy,
			int cur_gdx, int cur_gdy, int direction) {

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

		int n_p1dx = cur_p1dx + xdelta;
		int n_p1dy = cur_p1dy + ydelta;
		int n_p2dx = cur_p2dx + xdelta;
		int n_p2dy = cur_p2dy + ydelta;
		int n_p3dx = cur_p3dx + xdelta;
		int n_p3dy = cur_p3dy + ydelta;
		int n_gdx = cur_gdx + xdelta;
		int n_gdy = cur_gdy + ydelta;

		// make sure new position is valid (only need to use pit or goal)
		if (n_gdx < 0 - this.goalLoc[0] || n_gdx >= this.worldSize[0] - this.goalLoc[0] || n_gdy < 0 - this.goalLoc[1]
				|| n_gdy >= this.worldSize[1] - this.goalLoc[1]) {
			n_p1dx = cur_p1dx;
			n_p1dy = cur_p1dy;
			n_p2dx = cur_p2dx;
			n_p2dy = cur_p2dy;
			n_p3dx = cur_p3dx;
			n_p3dy = cur_p3dy;
			n_gdx = cur_gdx;
			n_gdy = cur_gdy;
		}

		return new int[] { n_p1dx, n_p1dy, n_p2dx, n_p2dy, n_p3dx, n_p3dy, n_gdx, n_gdy };
	}
}
