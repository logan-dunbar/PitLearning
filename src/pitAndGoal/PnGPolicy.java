package pitAndGoal;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import burlap.behavior.policy.EnumerablePolicy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.policy.support.ActionProb;
import burlap.behavior.valuefunction.QProvider;
import burlap.behavior.valuefunction.QValue;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;

public class PnGPolicy implements EnumerablePolicy {

	protected QProvider qplanner;
	protected double epsilon;
	protected Random rand;

	private ValueFunction pitVF = null;
	private ValueFunction goalVF = null;

	public PnGPolicy() {

	}

	@Override
	public Action action(State s) {
		List<QValue> qValues = this.qplanner.qValues(s);

		double roll = rand.nextDouble();
		if (roll <= epsilon) {
			int selected = rand.nextInt(qValues.size());
			Action ga = qValues.get(selected).a;
			return ga;
		}

		List<QValue> maxActions = new ArrayList<QValue>();
		maxActions.add(qValues.get(0));
		double maxQ = qValues.get(0).q;
		for (int i = 1; i < qValues.size(); i++) {
			QValue q = qValues.get(i);
			if (q.q == maxQ) {
				maxActions.add(q);
			} else if (q.q > maxQ) {
				maxActions.clear();
				maxActions.add(q);
				maxQ = q.q;
			}
		}
		int selected = rand.nextInt(maxActions.size());
		// return translated action parameters if the action is parameterized with objects in a object identifier indepdent domain
		Action ga = maxActions.get(selected).a;
		return ga;
	}

	@Override
	public double actionProb(State s, Action a) {
		return PolicyUtils.actionProbFromEnum(this, s, a);
	}

	@Override
	public boolean definedFor(State s) {
		return true; // can always find q-values with default value
	}

	@Override
	public List<ActionProb> policyDistribution(State s) {
		List<QValue> qValues = this.qplanner.qValues(s);

		List<ActionProb> dist = new ArrayList<ActionProb>(qValues.size());
		double maxQ = Double.NEGATIVE_INFINITY;
		int nMax = 0;
		for (QValue q : qValues) {
			if (q.q > maxQ) {
				maxQ = q.q;
				nMax = 1;
			} else if (q.q == maxQ) {
				nMax++;
			}
			ActionProb ap = new ActionProb(q.a, this.epsilon * (1. / qValues.size()));
			dist.add(ap);
		}
		for (int i = 0; i < dist.size(); i++) {
			QValue q = qValues.get(i);
			if (q.q == maxQ) {
				dist.get(i).pSelection += (1. - this.epsilon) / nMax;
			}
		}

		return dist;
	}

}