package pitAndGoal;

import java.util.List;

import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.mdp.core.state.State;
import common.FolderCreator;
import common.MyFileWriter;
import goal.GoalLearner;
import goal.GoalState;
import pit.PitLearner;
import pit.PitState;

public class PnGRunner {
	public static void main(String[] args) {
		String outputFolder = FolderCreator.createFolder("PitAndGoal");
		new PnGRunner(outputFolder, false);
		new PnGRunner(outputFolder, true);

		System.exit(0);
	}

	private PnGLearner pngLearner;
	private PitLearner pitLearner;
	private GoalLearner goalLearner;

	private int[] pngWorldSize = new int[] { 11, 11 };
	private int[] pngPitLoc = new int[] { 5, 5 };
	private int[] pngGoalLoc = new int[] { 10, 10 };

	private int[] pitWorldSize = new int[] { 5, 5 };
	private int[] pitLoc = new int[] { 2, 2 };

	private int[] goalWorldSize = new int[] { 9, 9 };
	private int[] goalLoc = new int[] { 7, 7 };

	private ValueFunction pitVF = null;
	private ValueFunction goalVF = null;

	private QLearning pitQL = null;
	private QLearning goalQL = null;

	public PnGRunner(String outputFolder, boolean withTransfer) {
		this.pitLearner = new PitLearner(0.8, this.pitWorldSize, this.pitLoc, 200000);
		this.goalLearner = new GoalLearner(0.8, this.goalWorldSize, this.goalLoc, 200000);
		// if (withTransfer) {
		// this.pitVF = this.pitLearner.runLearning();
		// this.goalVF = this.goalLearner.runLearning();
		// }

		if (withTransfer) {
			this.pitQL = this.pitLearner.runLearningLookAhead();
			this.goalQL = this.goalLearner.runLearningLookAhead();
		}

		this.pngLearner = new PnGLearner(outputFolder, 0.99, 0.8, this.pngWorldSize, this.pngPitLoc, this.pngGoalLoc,
				5000, this.pitVF, this.goalVF, this.pitQL, this.goalQL);
		ValueFunction vf = this.pngLearner.runLearning(withTransfer);

		manualValueFunctionVis(outputFolder, withTransfer, vf, null);
		// this.pngLearner.visualize();
	}

	public void manualValueFunctionVis(String folder, boolean withTransfer, ValueFunction vf, Policy p) {
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

			if (this.pitQL != null) {
				map[xInd][yInd] = map[xInd][yInd] + this.pitQL.value(new PitState(ps.pit_dx, ps.pit_dy));
			}

			if (this.goalQL != null) {
				map[xInd][yInd] = map[xInd][yInd] + this.goalQL.value(new GoalState(ps.goal_dx, ps.goal_dy));
			}
		}

		String filename = withTransfer ? "pngValueFunction_transfer" : "pngValueFunction";
		MyFileWriter.writeExcelValueFunction(folder, filename, map);

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
