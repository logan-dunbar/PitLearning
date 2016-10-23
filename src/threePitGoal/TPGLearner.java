package threePitGoal;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import burlap.behavior.learningrate.ExponentialDecayLR;
import burlap.behavior.learningrate.LearningRate;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.auxiliary.StateReachability;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.statehashing.HashableStateFactory;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import goal.GoalState;
import pit.PitState;

public class TPGLearner {

	private double gamma;
	private int[] worldSize;
	private int[] pit1Loc;
	private int[] pit2Loc;
	private int[] pit3Loc;
	private int[] goalLoc;
	private int episodes;

	private TPGDomainGenerator generator;
	private SADomain domain;
	private SimulatedEnvironment env;
	private HashableStateFactory hashingFactory;

	private ValueFunction pitVF = null;
	private ValueFunction goalVF = null;

	public TPGLearner(double gamma, double prob, int[] worldSize, int[] pit1Loc, int[] pit2Loc, int[] pit3Loc,
			int[] goalLoc, int episodes, ValueFunction pitVF, ValueFunction goalVF) {
		this.gamma = gamma;
		this.worldSize = worldSize;
		this.pit1Loc = pit1Loc;
		this.pit2Loc = pit2Loc;
		this.pit3Loc = pit3Loc;
		this.goalLoc = goalLoc;

		this.episodes = episodes;

		this.pitVF = pitVF;
		this.goalVF = goalVF;

		this.generator = new TPGDomainGenerator(gamma, prob, worldSize, goalLoc, pitVF, goalVF);
		this.domain = this.generator.generateDomain();
		this.env = new SimulatedEnvironment(this.domain, getInitialState());
		this.hashingFactory = new SimpleHashableStateFactory();
	}

	public ValueFunction runLearning(boolean withTransfer) {
		// LearningAgent agent = new SarsaLam(this.domain, this.gamma, this.hashingFactory, 0., 0.1, 0.3);
		LearningAgent agent = new QLearning(this.domain, this.gamma, this.hashingFactory, 0., 0.1);

		int maxSteps = this.worldSize[0] * this.worldSize[1] * 2;
		double decayRate = Math.pow(0.0001, (1.0 / (this.episodes * maxSteps * 2)));
		LearningRate lr = new ExponentialDecayLR(0.1, decayRate);
		((QLearning) agent).setLearningRateFunction(lr);

		FileWriter fileWriter = null;

		try {
			String filename = withTransfer ? "tpgLearner_transfer" : "tpgLearner";
			fileWriter = new FileWriter("output/threePitGoal/" + filename + ".csv");
			fileWriter.append("ep,maxSteps,finalReward\n");

			for (int ep = 0; ep < this.episodes; ep++) {
				Episode e = agent.runLearningEpisode(this.env);

				double reward = e.reward(e.maxTimeStep());
				TPGState fs = (TPGState) e.state(e.maxTimeStep() - 1);
				if (this.pitVF != null) {
					reward = reward + this.pitVF.value(new PitState(fs.pit1_dx, fs.pit1_dy));
				}
				if (this.pitVF != null) {
					reward = reward + this.pitVF.value(new PitState(fs.pit2_dx, fs.pit2_dy));
				}
				if (this.pitVF != null) {
					reward = reward + this.pitVF.value(new PitState(fs.pit3_dx, fs.pit3_dy));
				}
				if (this.goalVF != null) {
					reward = reward + this.goalVF.value(new GoalState(fs.goal_dx, fs.goal_dy));
				}

				fileWriter.append(ep + "," + e.maxTimeStep() + "," + reward + "\n");

				if (Math.floorMod(ep, this.episodes / 100) == 0) {
					String epFilename = withTransfer ? "qlearning_transfer" : "qlearning";
					e.write("output/threePitGoal/" + epFilename + "_" + ep);
					System.out.println(ep / (this.episodes / 100) + "%");
				}

				this.env.resetEnvironment();
				// this.env.setCurStateTo(getInitialState());
			}
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

		return (ValueFunction) agent;
	}

	public List<State> getAllStates() {
		return StateReachability.getReachableStates(getInitialState(), this.domain, this.hashingFactory);
	}

	private TPGState getInitialState() {
		int initialP1Dx = 0 - this.pit1Loc[0];
		int initialP1Dy = 0 - this.pit1Loc[1];
		int initialP2Dx = 0 - this.pit2Loc[0];
		int initialP2Dy = 0 - this.pit2Loc[1];
		int initialP3Dx = 0 - this.pit3Loc[0];
		int initialP3Dy = 0 - this.pit3Loc[1];
		int initialGDx = 0 - this.goalLoc[0];
		int initialGDy = 0 - this.goalLoc[1];

		return new TPGState(initialP1Dx, initialP1Dy, initialP2Dx, initialP2Dy, initialP3Dx, initialP3Dy, initialGDx,
				initialGDy);
	}
}
