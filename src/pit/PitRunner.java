package pit;

import static pit.PitDomainGenerator.ACTION_EAST;
import static pit.PitDomainGenerator.ACTION_NORTH;
import static pit.PitDomainGenerator.ACTION_SOUTH;
import static pit.PitDomainGenerator.ACTION_WEST;
import static pit.PitDomainGenerator.PIT_DX;
import static pit.PitDomainGenerator.PIT_DY;

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

public class PitRunner {

	public static void main(String[] args) {
		new PitRunner();

		System.exit(0);
	}

	private PitLearner learner;
	private int[] worldSize;
	private int[] pitLoc;

	public PitRunner() {
		this.worldSize = new int[] { 5, 5 };
		this.pitLoc = new int[] { 2, 2 };

		this.learner = new PitLearner(0.9, this.worldSize, this.pitLoc, 500000);
		ValueFunction vf = this.learner.runLearning();

		manualValueFunctionVis(vf, null);
	}

	// public void visualize(String outputPath) {
	// Visualizer v = pdg.getVisualizer();
	// new EpisodeSequenceVisualizer(v, domain, outputPath);
	// }

	// public void sarsaLearningExample(String outputPath) {
	//
	// // LearningAgent agent = new SarsaLam(domain, 0.99, hashingFactory, 0., 0.1, 0.15);
	// LearningAgent agent = new QLearning(domain, 0.9, hashingFactory, 0., 0.2);
	//
	// // run learning for 50 episodes
	// int n = 500000;
	// for (int i = 0; i < n; i++) {
	// Episode e = agent.runLearningEpisode(env, 50);
	//
	// // if (Math.floorMod(i, 100) == 0) {
	// // System.out.println(i + ": " + e.maxTimeStep());
	// // }
	// // e.write(outputPath + "sarsa_" + i);
	// System.out.println(i + ": " + e.maxTimeStep());
	//
	// // reset environment for next learning episode
	// env.resetEnvironment();
	//
	// if (Math.floorMod(i, n / 15) == 0) {
	// // Policy p = ((SarsaLam) agent).planFromState(initialState);
	// // PolicyUtils.rollout(p, initialState, domain.getModel()).write(outputPath + "sarsa_500");
	// // manualValueFunctionVis(((SarsaLam) agent), null);
	// manualValueFunctionVis(((QLearning) agent), null);
	//
	// // Policy p = ((QLearning) agent).planFromState(initialState);
	// // manualValueFunctionVis((ValueFunction) agent, p);
	// }
	//
	// int[] startPos = getStartPos(world_x, world_y, pit_x, pit_y);
	// initialState = new PitState(startPos[0], startPos[1]);
	// env.setCurStateTo(initialState);
	// // System.out.println("x: " + startPos[0] + ", y: " + startPos[1]);
	// }
	//
	// }

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
		svp.setXYKeys(PIT_DX, PIT_DY, xRange, yRange, 1, 1);

		// define a policy painter that uses arrow glyphs for each of the grid world actions
		PolicyGlyphPainter2D spp = new PolicyGlyphPainter2D();
		spp.setXYKeys(PIT_DX, PIT_DY, xRange, yRange, 1, 1);

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

	// private static int[] getStartPos(int world_x, int world_y, int pit_x, int pit_y) {
	// int pos = new Random().nextInt(5) + 1;
	//
	// if (pos == 1) {
	// return new int[] { 0 - pit_x, 0 - pit_y };
	// } else if (pos == 2) {
	// return new int[] { world_x - 1 - pit_x, 0 - pit_y };
	// } else if (pos == 3) {
	// return new int[] { 0 - pit_x, world_y - 1 - pit_y };
	// } else {
	// return new int[] { world_x - 1 - pit_x, world_y - 1 - pit_y };
	// }
	// }
}
