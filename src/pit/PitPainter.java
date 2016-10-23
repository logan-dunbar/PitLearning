package pit;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import burlap.mdp.core.state.State;
import burlap.visualizer.StatePainter;

public class PitPainter implements StatePainter {

	private int[] worldSize;
	private int[] pitLoc;

	public PitPainter(int[] worldSize, int[] pitLoc) {
		this.worldSize = worldSize;
		this.pitLoc = pitLoc;
	}

	@Override
	public void paint(Graphics2D g2, State s, float cWidth, float cHeight) {
		g2.setColor(Color.BLACK);

		// width, height of grid world
		float gWidth = this.worldSize[0];
		float gHeight = this.worldSize[1];

		// cell width, height for display
		float width = cWidth / gWidth;
		float height = cHeight / gHeight;

		float rx = this.pitLoc[0] * width;
		float ry = cHeight - (1 + this.pitLoc[1]) * height; // java coord adjustment: top-to-bottom

		g2.fill(new Rectangle2D.Float(rx, ry, width, height));
	}
}
