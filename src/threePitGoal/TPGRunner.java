package threePitGoal;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import burlap.behavior.policy.Policy;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.mdp.core.state.State;
import goal.GoalLearner;
import goal.GoalState;
import pit.PitLearner;
import pit.PitState;

public class TPGRunner {
	public static void main(String[] args) {
		new TPGRunner(false);
		new TPGRunner(true);

		System.exit(0);
	}

	private TPGLearner tpgLearner;
	private PitLearner pitLearner;
	private GoalLearner goalLearner;
	private int[] tpgWorldSize;
	private int[] tpgPit1Loc;
	private int[] tpgPit2Loc;
	private int[] tpgPit3Loc;
	private int[] tpgGoalLoc;

	private int[] pitWorldSize;
	private int[] pitLoc;

	private int[] goalWorldSize;
	private int[] goalLoc;

	private ValueFunction pitVF = null;
	private ValueFunction goalVF = null;

	public TPGRunner(boolean withTransfer) {

		this.tpgWorldSize = new int[] { 11, 11 };
		this.tpgPit1Loc = new int[] { 3, 4 };
		this.tpgPit2Loc = new int[] { 5, 6 };
		this.tpgPit3Loc = new int[] { 7, 2 };
		this.tpgGoalLoc = new int[] { 10, 10 };

		this.pitWorldSize = new int[] { 5, 5 };
		this.pitLoc = new int[] { 2, 2 };

		this.goalWorldSize = new int[] { 9, 9 };
		this.goalLoc = new int[] { 7, 7 };

		this.pitLearner = new PitLearner(0.8, this.pitWorldSize, this.pitLoc, 500000);
		this.goalLearner = new GoalLearner(0.8, this.goalWorldSize, this.goalLoc, 500000);
		if (withTransfer) {
			this.pitVF = this.pitLearner.runLearning();
			this.goalVF = this.goalLearner.runLearning();
		}

		this.tpgLearner = new TPGLearner(0.99, 0.8, this.tpgWorldSize, this.tpgPit1Loc, this.tpgPit2Loc,
				this.tpgPit3Loc, this.tpgGoalLoc, 5000, this.pitVF, this.goalVF);
		ValueFunction vf = this.tpgLearner.runLearning(withTransfer);

		manualValueFunctionVis(withTransfer, vf, null);
		// this.tpgLearner.visualize();
	}

	public void manualValueFunctionVis(boolean withTransfer, ValueFunction vf, Policy p) {
		List<State> allStates = this.tpgLearner.getAllStates();

		double[][] map = new double[this.tpgWorldSize[0]][this.tpgWorldSize[1]];
		for (State s : allStates) {
			TPGState ps = (TPGState) s;

			int xInd = this.tpgGoalLoc[0] + ps.goal_dx;
			int yInd = this.tpgGoalLoc[1] + ps.goal_dy;
			map[xInd][yInd] = vf.value(ps);

			if (this.pitVF != null) {
				map[xInd][yInd] = map[xInd][yInd] + this.pitVF.value(new PitState(ps.pit1_dx, ps.pit1_dy));
			}

			if (this.pitVF != null) {
				map[xInd][yInd] = map[xInd][yInd] + this.pitVF.value(new PitState(ps.pit2_dx, ps.pit2_dy));
			}

			if (this.pitVF != null) {
				map[xInd][yInd] = map[xInd][yInd] + this.pitVF.value(new PitState(ps.pit3_dx, ps.pit3_dy));
			}

			if (this.goalVF != null) {
				map[xInd][yInd] = map[xInd][yInd] + this.goalVF.value(new GoalState(ps.goal_dx, ps.goal_dy));
			}
		}

		StringBuilder sb = new StringBuilder();
		for (int y = this.tpgWorldSize[1] - 1; y >= 0; y--) {
			for (int x = 0; x < this.tpgWorldSize[0]; x++) {
				sb.append(map[x][y]);
				if (x < this.tpgWorldSize[0] - 1) {
					sb.append(",");
				}
			}

			sb.append("\n");
		}

		FileWriter fileWriter = null;
		String filename = withTransfer ? "tpgValueFunction_transfer" : "tpgValueFunction";

		try {
			fileWriter = new FileWriter("output/threePitGoal/" + filename + ".csv");
			fileWriter.append(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
