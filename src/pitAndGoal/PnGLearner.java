package pitAndGoal;

import java.util.ArrayList;
import java.util.List;

import burlap.behavior.learningrate.ExponentialDecayLR;
import burlap.behavior.learningrate.LearningRate;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.auxiliary.EpisodeSequenceVisualizer;
import burlap.behavior.singleagent.auxiliary.StateReachability;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.statehashing.HashableStateFactory;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import burlap.visualizer.Visualizer;
import common.MyFileWriter;
import common.PrintHelper;
import common.ResultData;

public class PnGLearner {
	private String outputFolder;

	private double gamma;
	private int[] worldSize;
	private int[] pitLoc;
	private int[] goalLoc;
	private int episodes;

	private PnGDomainGenerator generator;
	private SADomain domain;
	private SimulatedEnvironment env;
	private HashableStateFactory hashingFactory;

	private ValueFunction pitVF = null;
	private ValueFunction goalVF = null;

	private QLearning pitQL = null;
	private QLearning goalQL = null;

	public PnGLearner(String outputFolder, double gamma, double prob, int[] worldSize, int[] pitLoc, int[] goalLoc,
			int episodes, ValueFunction pitVF, ValueFunction goalVF, QLearning pitQL, QLearning goalQL) {

		this.outputFolder = outputFolder;

		this.gamma = gamma;
		this.worldSize = worldSize;
		this.pitLoc = pitLoc;
		this.goalLoc = goalLoc;

		this.episodes = episodes;

		this.pitVF = pitVF;
		this.goalVF = goalVF;

		this.pitQL = pitQL;
		this.goalQL = goalQL;

		this.generator = new PnGDomainGenerator(gamma, prob, worldSize, pitLoc, goalLoc, pitVF, goalVF, pitQL, goalQL);
		this.domain = this.generator.generateDomain();
		this.env = new SimulatedEnvironment(this.domain, getInitialState());
		this.hashingFactory = new SimpleHashableStateFactory();
	}

	public ValueFunction runLearning(boolean withTransfer) {
		System.out.println(withTransfer ? "Begin PnGLearning with Transfer:" : "Begin PnG Learning:");
		// LearningAgent agent = new SarsaLam(this.domain, this.gamma, this.hashingFactory, 0., 0.1, 0.3);
		QLearning agent = new QLearning(this.domain, this.gamma, this.hashingFactory, 0., 0.1);

		int maxSteps = this.worldSize[0] * this.worldSize[1] * 2;
		double decayRate = Math.pow(0.0001, (1.0 / (this.episodes * maxSteps * 2)));
		LearningRate lr = new ExponentialDecayLR(0.1, decayRate);
		agent.setLearningRateFunction(lr);

		if (withTransfer) {
			agent.setQInitFunction(new PnGTransferFunction(this.pitQL, this.goalQL));
		}

		List<ResultData> dataList = new ArrayList<ResultData>();

		for (int ep = 0; ep < this.episodes; ep++) {
			Episode e = agent.runLearningEpisode(this.env);

			double reward = e.reward(e.maxTimeStep());
			// PnGState fs = (PnGState) e.state(e.maxTimeStep() - 1);
			// if (this.pitVF != null) {
			// reward = reward + this.pitVF.value(new PitState(fs.pit_dx, fs.pit_dy));
			// }
			// if (this.goalVF != null) {
			// reward = reward + this.goalVF.value(new GoalState(fs.goal_dx, fs.goal_dy));
			// }

			if (ep < 500) {
				dataList.add(new ResultData(ep, e.maxTimeStep(), reward));
			}

			String text = ep / (this.episodes / 100) + "%";
			PrintHelper.printInterval(ep, this.episodes, 100, text);

			this.env.resetEnvironment();
		}

		String filename = withTransfer ? "pngLearner_transfer" : "pngLearner";
		String[] headings = new String[] { "Episode", "Steps", "Reward" };
		MyFileWriter.writeExcelFile(this.outputFolder, filename, headings, dataList);

		return (ValueFunction) agent;
	}

	public List<State> getAllStates() {
		return StateReachability.getReachableStates(getInitialState(), this.domain, this.hashingFactory);
	}

	public void visualize() {
		Visualizer v = this.generator.getVisualizer();
		new EpisodeSequenceVisualizer(v, this.domain, "output/pitAndGoal/");
	}

	private PnGState getInitialState() {
		int initialPDx = 0 - this.pitLoc[0];
		int initialPDy = 0 - this.pitLoc[1];
		int initialGDx = 0 - this.goalLoc[0];
		int initialGDy = 0 - this.goalLoc[1];

		return new PnGState(initialPDx, initialPDy, initialGDx, initialGDy);
	}
}
