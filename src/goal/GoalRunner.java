package goal;

import static goal.GoalDomainGenerator.ACTION_EAST;
import static goal.GoalDomainGenerator.ACTION_NORTH;
import static goal.GoalDomainGenerator.ACTION_SOUTH;
import static goal.GoalDomainGenerator.ACTION_WEST;
import static goal.GoalDomainGenerator.GOAL_DX;
import static goal.GoalDomainGenerator.GOAL_DY;

import java.awt.Color;
import java.util.List;

import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.ValueFunctionVisualizerGUI;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.ArrowActionGlyph;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.LandmarkColorBlendInterpolation;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.PolicyGlyphPainter2D;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.common.StateValuePainter2D;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.mdp.core.state.State;
import burlap.mdp.core.state.vardomain.VariableDomain;

public class GoalRunner {
	public static void main(String[] args) {
		new GoalRunner();

		System.exit(0);
	}

	private GoalLearner learner;
	private int[] worldSize;
	private int[] goalLoc;

	public GoalRunner() {
		this.worldSize = new int[] { 11, 11 };
		this.goalLoc = new int[] { 8, 8 };

		this.learner = new GoalLearner(0.8, this.worldSize, this.goalLoc, 500000);
		ValueFunction vf = this.learner.runLearning();

		manualValueFunctionVis(vf, null);
	}

	public void manualValueFunctionVis(ValueFunction vf, Policy p) {
		List<State> allStates = this.learner.getAllStates();

		// define colour function
		LandmarkColorBlendInterpolation cb = new LandmarkColorBlendInterpolation();
		cb.addNextLandMark(-50., Color.RED);
		cb.addNextLandMark(0., Color.BLUE);

		VariableDomain xRange = new VariableDomain(-this.worldSize[0] / 2, this.worldSize[0] / 2 + 1);
		VariableDomain yRange = new VariableDomain(-this.worldSize[1] / 2, this.worldSize[1] / 2 + 1);

		// define a 2D painter of state values, specifying which variables correspond to the x and y coordinates of the canvas
		StateValuePainter2D svp = new StateValuePainter2D(cb);
		svp.setXYKeys(GOAL_DX, GOAL_DY, xRange, yRange, 1, 1);

		// define a policy painter that uses arrow glyphs for each of the grid world actions
		PolicyGlyphPainter2D spp = new PolicyGlyphPainter2D();
		spp.setXYKeys(GOAL_DX, GOAL_DY, xRange, yRange, 1, 1);

		spp.setActionNameGlyphPainter(ACTION_NORTH, new ArrowActionGlyph(0));
		spp.setActionNameGlyphPainter(ACTION_SOUTH, new ArrowActionGlyph(1));
		spp.setActionNameGlyphPainter(ACTION_EAST, new ArrowActionGlyph(2));
		spp.setActionNameGlyphPainter(ACTION_WEST, new ArrowActionGlyph(3));
		spp.setRenderStyle(PolicyGlyphPainter2D.PolicyGlyphRenderStyle.DISTSCALED);

		// create our ValueFunctionVisualizer that paints for all states using the ValueFunction source and the state value painter we defined
		ValueFunctionVisualizerGUI gui = new ValueFunctionVisualizerGUI(allStates, svp, vf);

		// add our policy renderer to it
		gui.setSpp(spp);
		gui.setPolicy(p);

		// set the background colour for places where states are not rendered to grey
		gui.setBgColor(Color.GRAY);

		// start it
		gui.initGUI();
	}
}
