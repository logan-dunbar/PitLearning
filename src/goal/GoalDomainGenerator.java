package goal;

import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.action.UniversalActionType;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.model.FactoredModel;
import burlap.mdp.singleagent.model.RewardFunction;

public class GoalDomainGenerator implements DomainGenerator {

	public static final String GOAL_DX = "goal_dx";
	public static final String GOAL_DY = "goal_dy";

	public static final String ACTION_NORTH = "north";
	public static final String ACTION_SOUTH = "south";
	public static final String ACTION_EAST = "east";
	public static final String ACTION_WEST = "west";

	private double prob;
	private int[] worldSize;
	private int[] goalLoc;

	public GoalDomainGenerator(double prob, int[] worldSize, int[] goalLoc) {
		this.prob = prob;
		this.worldSize = worldSize;
		this.goalLoc = goalLoc;
	}

	@Override
	public SADomain generateDomain() {
		SADomain domain = new SADomain();

		domain.addActionTypes(new UniversalActionType(ACTION_NORTH), new UniversalActionType(ACTION_SOUTH),
				new UniversalActionType(ACTION_EAST), new UniversalActionType(ACTION_WEST));

		GoalStateModel model = new GoalStateModel(this.prob, this.worldSize, this.goalLoc);
		RewardFunction rf = new GoalReward();
		TerminalFunction tf = new GoalTerminal();

		domain.setModel(new FactoredModel(model, rf, tf));

		return domain;
	}

}
