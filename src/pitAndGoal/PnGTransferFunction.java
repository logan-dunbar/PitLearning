package pitAndGoal;

import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.behavior.valuefunction.QFunction;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import goal.GoalState;
import pit.PitState;

public class PnGTransferFunction implements QFunction {

	private QLearning pitLearner;
	private QLearning goalLearner;

	public PnGTransferFunction(QLearning pitLearner, QLearning goalLearner) {
		this.pitLearner = pitLearner;
		this.goalLearner = goalLearner;
	}

	@Override
	public double value(State s) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double qValue(State s, Action a) {
		PnGState png_s = (PnGState) s;
		PitState p_s = new PitState(png_s.pit_dx, png_s.pit_dy);
		GoalState g_s = new GoalState(png_s.goal_dx, png_s.goal_dy);

		double pitVal = this.pitLearner.qValue(p_s, a);
		double goalVal = this.goalLearner.qValue(g_s, a);

		return pitVal + goalVal;
	}

}
