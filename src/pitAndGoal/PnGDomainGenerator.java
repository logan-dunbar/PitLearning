package pitAndGoal;

import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.action.UniversalActionType;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.model.FactoredModel;
import burlap.mdp.singleagent.model.RewardFunction;
import burlap.visualizer.StateRenderLayer;
import burlap.visualizer.Visualizer;

public class PnGDomainGenerator implements DomainGenerator {

	public static final String PIT_DX = "pit_dx";
	public static final String PIT_DY = "pit_dy";

	public static final String GOAL_DX = "goal_dx";
	public static final String GOAL_DY = "goal_dy";

	public static final String ACTION_NORTH = "north";
	public static final String ACTION_SOUTH = "south";
	public static final String ACTION_EAST = "east";
	public static final String ACTION_WEST = "west";

	private double gamma;
	private double prob;
	private int[] worldSize;
	private int[] pitLoc;
	private int[] goalLoc;

	private ValueFunction pitVF;
	private ValueFunction goalVF;

	private QLearning pitQL = null;
	private QLearning goalQL = null;

	public PnGDomainGenerator(double gamma, double prob, int[] worldSize, int[] pitLoc, int[] goalLoc,
			ValueFunction pitVF, ValueFunction goalVF, QLearning pitQL, QLearning goalQL) {
		this.gamma = gamma;
		this.prob = prob;
		this.worldSize = worldSize;
		this.pitLoc = pitLoc;
		this.goalLoc = goalLoc;

		this.pitVF = pitVF;
		this.goalVF = goalVF;

		this.pitQL = pitQL;
		this.goalQL = goalQL;
	}

	@Override
	public SADomain generateDomain() {
		SADomain domain = new SADomain();

		domain.addActionTypes(new UniversalActionType(ACTION_NORTH), new UniversalActionType(ACTION_SOUTH),
				new UniversalActionType(ACTION_EAST), new UniversalActionType(ACTION_WEST));

		PnGStateModel model = new PnGStateModel(this.prob, this.worldSize, this.pitLoc, this.goalLoc);
		RewardFunction rf = new PnGReward(this.gamma, this.pitVF, this.goalVF);
		TerminalFunction tf = new PnGTerminal();

		domain.setModel(new FactoredModel(model, rf, tf));

		return domain;
	}

	public StateRenderLayer getStateRenderLayer() {
		StateRenderLayer rl = new StateRenderLayer();
		rl.addStatePainter(new PnGPitPainter(this.worldSize, this.pitLoc));
		rl.addStatePainter(new PnGAgentPainter(this.worldSize, this.pitLoc));
		return rl;
	}

	public Visualizer getVisualizer() {
		return new Visualizer(this.getStateRenderLayer());
	}

}
