package threePitGoal;

import burlap.behavior.valuefunction.ValueFunction;
import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.action.UniversalActionType;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.model.FactoredModel;
import burlap.mdp.singleagent.model.RewardFunction;

public class TPGDomainGenerator implements DomainGenerator {

	public static final String PIT1_DX = "pit1_dx";
	public static final String PIT1_DY = "pit1_dy";
	public static final String PIT2_DX = "pit2_dx";
	public static final String PIT2_DY = "pit2_dy";
	public static final String PIT3_DX = "pit3_dx";
	public static final String PIT3_DY = "pit3_dy";

	public static final String GOAL_DX = "goal_dx";
	public static final String GOAL_DY = "goal_dy";

	public static final String ACTION_NORTH = "north";
	public static final String ACTION_SOUTH = "south";
	public static final String ACTION_EAST = "east";
	public static final String ACTION_WEST = "west";

	private double gamma;
	private double prob;
	private int[] worldSize;
	private int[] goalLoc;

	private ValueFunction pitVF;
	private ValueFunction goalVF;

	public TPGDomainGenerator(double gamma, double prob, int[] worldSize, int[] goalLoc, ValueFunction pitVF,
			ValueFunction goalVF) {
		this.gamma = gamma;
		this.prob = prob;
		this.worldSize = worldSize;
		this.goalLoc = goalLoc;

		this.pitVF = pitVF;
		this.goalVF = goalVF;
	}

	@Override
	public SADomain generateDomain() {
		SADomain domain = new SADomain();

		domain.addActionTypes(new UniversalActionType(ACTION_NORTH), new UniversalActionType(ACTION_SOUTH),
				new UniversalActionType(ACTION_EAST), new UniversalActionType(ACTION_WEST));

		TPGStateModel model = new TPGStateModel(this.prob, this.worldSize, this.goalLoc);
		RewardFunction rf = new TPGReward(this.gamma, this.pitVF, this.goalVF);
		TerminalFunction tf = new TPGTerminal();

		domain.setModel(new FactoredModel(model, rf, tf));

		return domain;
	}
}
