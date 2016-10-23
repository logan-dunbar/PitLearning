package pit;

import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.action.UniversalActionType;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.model.FactoredModel;
import burlap.mdp.singleagent.model.RewardFunction;
import burlap.visualizer.StateRenderLayer;
import burlap.visualizer.Visualizer;

public class PitDomainGenerator implements DomainGenerator {

	public static final String PIT_DX = "pit_dx";
	public static final String PIT_DY = "pit_dy";

	public static final String ACTION_NORTH = "north";
	public static final String ACTION_SOUTH = "south";
	public static final String ACTION_EAST = "east";
	public static final String ACTION_WEST = "west";

	private double prob;
	private int[] worldSize;
	private int[] pitLoc;

	public PitDomainGenerator(double prob, int[] worldSize, int[] pitLoc) {
		this.prob = prob;
		this.worldSize = worldSize;
		this.pitLoc = pitLoc;
	}

	@Override
	public SADomain generateDomain() {
		SADomain domain = new SADomain();

		domain.addActionTypes(new UniversalActionType(ACTION_NORTH), new UniversalActionType(ACTION_SOUTH),
				new UniversalActionType(ACTION_EAST), new UniversalActionType(ACTION_WEST));

		PitStateModel model = new PitStateModel(this.prob, this.worldSize, this.pitLoc);
		RewardFunction rf = new PitReward();
		TerminalFunction tf = new PitTerminal();

		domain.setModel(new FactoredModel(model, rf, tf));

		return domain;
	}

	public StateRenderLayer getStateRenderLayer() {
		StateRenderLayer rl = new StateRenderLayer();
		rl.addStatePainter(new PitPainter(this.worldSize, this.pitLoc));
		rl.addStatePainter(new PitAgentPainter(this.worldSize, this.pitLoc));
		return rl;
	}

	public Visualizer getVisualizer() {
		return new Visualizer(this.getStateRenderLayer());
	}

}
