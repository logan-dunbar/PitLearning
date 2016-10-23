package common;

public class ResultData {

	private int episode;
	private int steps;
	private double reward;

	public ResultData(int episode, int steps, double reward) {
		this.setEpisode(episode);
		this.setSteps(steps);
		this.setReward(reward);
	}

	public int getEpisode() {
		return episode;
	}

	public void setEpisode(int episode) {
		this.episode = episode;
	}

	public int getSteps() {
		return steps;
	}

	public void setSteps(int steps) {
		this.steps = steps;
	}

	public double getReward() {
		return reward;
	}

	public void setReward(double reward) {
		this.reward = reward;
	}
}
