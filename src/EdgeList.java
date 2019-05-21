/**
 * EdgeList should store the data for the edge list of a single polygon in your
 * scene. A few method stubs have been provided so that it can be tested, but
 * you'll need to fill in all the details.
 *
 * You'll probably want to add some setters as well as getters or, for example,
 * an addRow(y, xLeft, xRight, zLeft, zRight) method.
 */
public class EdgeList {
	private int yMin, yMax;
	private float[] leftX, leftZ, rightX, rightZ;

	public EdgeList(int yMin, int yMax) {
		this.yMin = yMin;
		this.yMax = yMax;
		this.leftX = new float[yMax - yMin + 1];
		this.leftZ = new float[yMax - yMin + 1];
		this.rightX = new float[yMax - yMin + 1];
		this.rightZ = new float[yMax - yMin + 1];
	}

	public void setLeftRow(int y, float x, float z) {
		this.leftX[y - yMin] = x;
		this.leftZ[y - yMin] = z;

	}

	public void setRightRow(int y, float x, float z) {
		this.rightX[y - yMin] = x;
		this.rightZ[y - yMin] = z;
	}

	public int getYMin() {
		return this.yMin;
	}

	public int getYMax() {
		return this.yMax;
	}

	public float getLeftX(int y) {
		return this.leftX[y - yMin];
	}

	public float getRightX(int y) {
		return this.rightX[y - yMin];
	}

	public float getLeftZ(int y) {
		return this.leftZ[y - yMin];
	}

	public float getRightZ(int y) {
		return this.rightZ[y - yMin];
	}
}

// code for comp261 assignments
