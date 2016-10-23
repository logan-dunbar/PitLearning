package goal;

import java.util.List;
import java.util.Random;

import burlap.behavior.learningrate.ExponentialDecayLR;
import burlap.behavior.learningrate.LearningRate;
import burlap.behavior.singleagent.auxiliary.StateReachability;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.statehashing.HashableStateFactory;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import common.PrintHelper;

public class GoalLearner {

	private int[] worldSize;
	private int[] goalLoc;
	private int episodes;

	private GoalDomainGenerator generator;
	private SADomain domain;
	private SimulatedEnvironment env;
	private HashableStateFactory hashingFactory;

	public GoalLearner(double prob, int[] worldSize, int[] goalLoc, int episodes) {
		this.worldSize = worldSize;
		this.goalLoc = goalLoc;

		this.episodes = episodes;

		this.generator = new GoalDomainGenerator(prob, worldSize, goalLoc);
		this.domain = this.generator.generateDomain();
		this.env = new SimulatedEnvironment(this.domain, getInitialState());
		this.hashingFactory = new SimpleHashableStateFactory();
	}

	public QLearning runLearningLookAhead() {
		System.out.println("Begin Goal Learning:");
		// LearningAgent agent = new SarsaLam(domain, 0.99, hashingFactory, 0., 0.1, 0.3);
		QLearning agent = new QLearning(this.domain, 0.99, this.hashingFactory, 0., 0.1);

		int maxSteps = this.worldSize[0] * this.worldSize[1] * 2;
		double decayRate = Math.pow(0.0001, (1.0 / (this.episodes * maxSteps * 2)));
		LearningRate lr = new ExponentialDecayLR(0.1, decayRate);
		((QLearning) agent).setLearningRateFunction(lr);

		for (int ep = 0; ep < this.episodes; ep++) {
			agent.runLearningEpisode(this.env, maxSteps);

			String text = ep / (this.episodes / 100) + "%";
			PrintHelper.printInterval(ep, this.episodes, 100, text);

			this.env.resetEnvironment();
			this.env.setCurStateTo(getInitialState());
		}

		return agent;
	}

	public ValueFunction runLearning() {
		return (ValueFunction) this.runLearningLookAhead();
	}

	public List<State> getAllStates() {
		return StateReachability.getReachableStates(getInitialState(), this.domain, this.hashingFactory);
	}

	private GoalState getInitialState() {
		int x = new Random().nextInt(2);
		int y = new Random().nextInt(2);

		int initialDx = x == 0 ? 0 - this.goalLoc[0] : (this.worldSize[0] - 1) - this.goalLoc[0];
		int initialDy = y == 0 ? 0 - this.goalLoc[1] : (this.worldSize[1] - 1) - this.goalLoc[1];

		return new GoalState(initialDx, initialDy);
	}
}
