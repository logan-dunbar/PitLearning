package threePitGoal;

import burlap.behavior.valuefunction.ValueFunction;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.model.RewardFunction;
import goal.GoalState;
import pit.PitState;

public class TPGReward implements RewardFunction {
	private double gamma;
	private ValueFunction pitVF;
	private ValueFunction goalVF;

	public TPGReward(double gamma, ValueFunction pitVF, ValueFunction goalVF) {
		this.gamma = gamma;
		this.pitVF = pitVF;
		this.goalVF = goalVF;
	}

	@Override
	public double reward(State s, Action a, State sprime) {
		TPGState ps = (TPGState) s;
		TPGState psprime = (TPGState) sprime;

		double reward = 0.;
		if (psprime.pit1_dx == 0 && psprime.pit1_dy == 0) {
			reward = -50.;
		} else if (psprime.pit2_dx == 0 && psprime.pit2_dy == 0) {
			reward = -50.;
		} else if (psprime.pit3_dx == 0 && psprime.pit3_dy == 0) {
			reward = -50.;
		} else if (psprime.goal_dx == 0 && psprime.goal_dy == 0) {
			reward = 100.;
		} else {
			reward = -0.5;

			double pit1Shaping = 0.;
			double pit2Shaping = 0.;
			double pit3Shaping = 0.;
			double goalShaping = 0.;
			if (this.pitVF != null) {
				pit1Shaping = this.gamma * this.pitVF.value(new PitState(psprime.pit1_dx, psprime.pit1_dy))
						- this.pitVF.value(new PitState(ps.pit1_dx, ps.pit1_dy));
			}
			if (this.pitVF != null) {
				pit2Shaping = this.gamma * this.pitVF.value(new PitState(psprime.pit2_dx, psprime.pit2_dy))
						- this.pitVF.value(new PitState(ps.pit2_dx, ps.pit2_dy));
			}
			if (this.pitVF != null) {
				pit3Shaping = this.gamma * this.pitVF.value(new PitState(psprime.pit3_dx, psprime.pit3_dy))
						- this.pitVF.value(new PitState(ps.pit3_dx, ps.pit3_dy));
			}
			if (this.goalVF != null) {
				goalShaping = this.gamma * this.goalVF.value(new GoalState(psprime.goal_dx, psprime.goal_dy))
						- this.goalVF.value(new GoalState(ps.goal_dx, ps.goal_dy));
			}

			reward = reward + pit1Shaping + pit2Shaping + pit3Shaping + goalShaping;
		}

		return reward;
	}
}
