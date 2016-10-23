package pitAndGoal;

import burlap.behavior.valuefunction.ValueFunction;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.model.RewardFunction;
import goal.GoalState;
import pit.PitState;

public class PnGReward implements RewardFunction {

	private double gamma;
	private ValueFunction pitVF;
	private ValueFunction goalVF;

	public PnGReward(double gamma, ValueFunction pitVF, ValueFunction goalVF) {
		this.gamma = gamma;
		this.pitVF = pitVF;
		this.goalVF = goalVF;
	}

	@Override
	public double reward(State s, Action a, State sprime) {
		PnGState ps = (PnGState) s;
		PnGState psprime = (PnGState) sprime;

		double reward = 0.;
		if (psprime.pit_dx == 0 && psprime.pit_dy == 0) {
			reward = -50.;
		} else if (psprime.goal_dx == 0 && psprime.goal_dy == 0) {
			reward = 100.;
		} else {
			reward = -0.5;

			double pitShaping = 0.;
			double goalShaping = 0.;
			if (this.pitVF != null) {
				pitShaping = this.gamma * this.pitVF.value(new PitState(psprime.pit_dx, psprime.pit_dy))
						- this.pitVF.value(new PitState(ps.pit_dx, ps.pit_dy));
			}
			if (this.goalVF != null) {
				goalShaping = this.gamma * this.goalVF.value(new GoalState(psprime.goal_dx, psprime.goal_dy))
						- this.goalVF.value(new GoalState(ps.goal_dx, ps.goal_dy));
			}

			reward = reward + pitShaping + goalShaping;
		}

		return reward;
	}

}
