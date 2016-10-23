package pitAndGoal;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.mdp.core.state.State;
import goal.GoalLearner;
import goal.GoalState;
import pit.PitLearner;
import pit.PitState;

public class PnGRunner {
	public static void main(String[] args) {
		new PnGRunner(false);
		new PnGRunner(true);

		System.exit(0);
	}

	private PnGLearner pngLearner;
	private PitLearner pitLearner;
	private GoalLearner goalLearner;
	private int[] pngWorldSize;
	private int[] pngPitLoc;
	private int[] pngGoalLoc;

	private int[] pitWorldSize;
	private int[] pitLoc;

	private int[] goalWorldSize;
	private int[] goalLoc;

	private ValueFunction pitVF = null;
	private ValueFunction goalVF = null;

	private QLearning pitQL = null;
	private QLearning goalQL = null;

	public PnGRunner(boolean withTransfer) {

		this.pngWorldSize = new int[] { 11, 11 };
		this.pngPitLoc = new int[] { 5, 5 };
		this.pngGoalLoc = new int[] { 10, 10 };

		this.pitWorldSize = new int[] { 5, 5 };
		this.pitLoc = new int[] { 2, 2 };

		this.goalWorldSize = new int[] { 9, 9 };
		this.goalLoc = new int[] { 7, 7 };

		this.pitLearner = new PitLearner(0.8, this.pitWorldSize, this.pitLoc, 500000);
		this.goalLearner = new GoalLearner(0.8, this.goalWorldSize, this.goalLoc, 500000);
		// if (withTransfer) {
		// this.pitVF = this.pitLearner.runLearning();
		// this.goalVF = this.goalLearner.runLearning();
		// }

		if (withTransfer) {
			this.pitQL = this.pitLearner.runLearningLookAhead();
			this.goalQL = this.goalLearner.runLearningLookAhead();
		}

		this.pngLearner = new PnGLearner(0.99, 0.8, this.pngWorldSize, this.pngPitLoc, this.pngGoalLoc, 5000,
				this.pitVF, this.goalVF);
		ValueFunction vf = this.pngLearner.runLearning(withTransfer);

		manualValueFunctionVis(withTransfer, vf, null);
		// this.pngLearner.visualize();
	}

	public void manualValueFunctionVis(boolean withTransfer, ValueFunction vf, Policy p) {
		List<State> allStates = this.pngLearner.getAllStates();

		double[][] map = new double[this.pngWorldSize[0]][this.pngWorldSize[1]];
		for (State s : allStates) {
			PnGState ps = (PnGState) s;

			int xInd = this.pngGoalLoc[0] + ps.goal_dx;
			int yInd = this.pngGoalLoc[1] + ps.goal_dy;
			map[xInd][yInd] = vf.value(ps);

			if (this.pitVF != null) {
				map[xInd][yInd] = map[xInd][yInd] + this.pitVF.value(new PitState(ps.pit_dx, ps.pit_dy));
			}

			if (this.goalVF != null) {
				map[xInd][yInd] = map[xInd][yInd] + this.goalVF.value(new GoalState(ps.goal_dx, ps.goal_dy));
			}
		}

		StringBuilder sb = new StringBuilder();
		for (int y = this.pngWorldSize[1] - 1; y >= 0; y--) {
			for (int x = 0; x < this.pngWorldSize[0]; x++) {
				sb.append(map[x][y]);
				if (x < this.pngWorldSize[0] - 1) {
					sb.append(",");
				}
			}

			sb.append("\n");
		}

		FileWriter fileWriter = null;
		String filename = withTransfer ? "pngValueFunction_transfer" : "pngValueFunction";

		try {
			fileWriter = new FileWriter("output/" + filename + ".csv");
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

		// define colour function
		// LandmarkColorBlendInterpolation cb = new LandmarkColorBlendInterpolation();
		// cb.addNextLandMark(-100., Color.RED);
		// cb.addNextLandMark(200., Color.GREEN);
		//
		// VariableDomain xRange = new VariableDomain(-this.worldSize[0] / 2, this.worldSize[0] / 2 + 1);
		// VariableDomain yRange = new VariableDomain(-this.worldSize[1] / 2, this.worldSize[1] / 2 + 1);
		//
		// // define a 2D painter of state values, specifying which variables correspond to the x and y coordinates of the canvas
		// StateValuePainter2D svp = new StateValuePainter2D(cb);
		// svp.setXYKeys(GOAL_DX, GOAL_DY, xRange, yRange, 1, 1);
		//
		// // define a policy painter that uses arrow glyphs for each of the grid world actions
		// PolicyGlyphPainter2D spp = new PolicyGlyphPainter2D();
		// spp.setXYKeys(GOAL_DX, GOAL_DY, xRange, yRange, 1, 1);
		//
		// spp.setActionNameGlyphPainter(ACTION_NORTH, new ArrowActionGlyph(0));
		// spp.setActionNameGlyphPainter(ACTION_SOUTH, new ArrowActionGlyph(1));
		// spp.setActionNameGlyphPainter(ACTION_EAST, new ArrowActionGlyph(2));
		// spp.setActionNameGlyphPainter(ACTION_WEST, new ArrowActionGlyph(3));
		// spp.setRenderStyle(PolicyGlyphPainter2D.PolicyGlyphRenderStyle.DISTSCALED);
		//
		// // create our ValueFunctionVisualizer that paints for all states using the ValueFunction source and the state value painter we defined
		// ValueFunctionVisualizerGUI gui = new ValueFunctionVisualizerGUI(allStates, svp, vf);
		//
		// // add our policy renderer to it
		// gui.setSpp(spp);
		// gui.setPolicy(p);
		//
		// // set the background colour for places where states are not rendered to grey
		// gui.setBgColor(Color.GRAY);
		//
		// // start it
		// gui.initGUI();
	}
}
