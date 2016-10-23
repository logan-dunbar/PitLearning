package pitAndGoal;

import static pit.PitDomainGenerator.PIT_DX;
import static pit.PitDomainGenerator.PIT_DY;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import burlap.mdp.core.state.State;
import burlap.visualizer.StatePainter;

public class PnGAgentPainter implements StatePainter {
	private int[] worldSize;
	private int[] pitLoc;

	public PnGAgentPainter(int[] worldSize, int[] pitLoc) {
		this.worldSize = worldSize;
		this.pitLoc = pitLoc;
	}

	@Override
	public void paint(Graphics2D g2, State s, float cWidth, float cHeight) {
		g2.setColor(Color.BLUE);

		// width, height of grid world
		float gWidth = this.worldSize[0];
		float gHeight = this.worldSize[1];

		// cell width, height for display
		float width = cWidth / gWidth;
		float height = cHeight / gHeight;

		int x = (int) s.get(PIT_DX) + this.pitLoc[0];
		int y = (int) s.get(PIT_DY) + this.pitLoc[1];

		float rx = x * width;
		float ry = cHeight - (1 + y) * height; // java coord adjustment: top-to-bottom

		g2.fill(new Ellipse2D.Float(rx, ry, width, height));
	}
}
