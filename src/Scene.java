import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The Scene class is where we store data about a 3D model and light source
 * inside our renderer. It also contains a static inner class that represents one
 * single polygon.
 * 
 * Method stubs have been provided, but you'll need to fill them in.
 * 
 * If you were to implement more fancy rendering, e.g. Phong shading, you'd want
 * to store more information in this class.
 */
public class Scene {
	private List<Polygon> polygons;
	private ArrayList<Vector3D> lightPos = new ArrayList<>();
	private float[] imageBoundary;
	private float[] imageCentre;

	public Scene(List<Polygon> polygons, ArrayList<Vector3D> lightPos) {
		this.polygons = polygons;
		this.lightPos = lightPos;
		setImageBoundary();
		setImageCentre(this.imageBoundary);
	}

	public ArrayList<Vector3D> getLight() {
          return this.lightPos;
	}

	public List<Polygon> getPolygons() {
          return this.polygons;
	}

	public float[] getImageBoundary() {
		return this.imageBoundary;
	}

	public float[] getImageCentre() {
		return this.imageCentre;
	}

	public void setImageBoundary() {
		float[] boundary = new float[4];
		boundary[0] = Float.POSITIVE_INFINITY; // xMin
		boundary[1] = Float.NEGATIVE_INFINITY; // xMax
		boundary[2] = Float.POSITIVE_INFINITY; // yMin
		boundary[3] = Float.NEGATIVE_INFINITY; // yMax

		for (Polygon polygon : polygons) {
			for (Vector3D vertex : polygon.getVertices()) {
				if (vertex.x < boundary[0]) {
					boundary[0] = vertex.x;
				}
				if (vertex.x > boundary[1]) {
					boundary[1] = vertex.x;
				}
				if (vertex.y < boundary[2]) {
					boundary[2] = vertex.y;
				}
				if (vertex.y > boundary[3]) {
					boundary[3] = vertex.y;
				}
			}
		}

		this.imageBoundary = boundary;
	}

	public void setImageCentre(float[] imageBoundary) {
		float[] imageCentre = new float[2];
		imageCentre[0] = (imageBoundary[1] - imageBoundary[0] / 2) + imageBoundary[0];
		imageCentre[1] = (imageBoundary[3] - imageBoundary[2] / 2) + imageBoundary[2];

		this.imageCentre = imageCentre;
	}

	public void addLightSource() {
		Random randomNumber = new Random();
		int x = -100 + randomNumber.nextInt(100);
		int y = -100 + randomNumber.nextInt(100);
		int z = -100 + randomNumber.nextInt(100);

		Vector3D newLight = new Vector3D(x, y, z);
		this.lightPos.add(newLight);

	}

	public void removeLightsource(int index) {
		this.lightPos.remove(index);
	}

	/**
	 * Polygon stores data about a single polygon in a scene, keeping track of
	 * (at least!) its three vertices and its reflectance.
         *
         * This class has been done for you.
	 */
	public static class Polygon {
		Vector3D[] vertices;
		Color reflectance;

		/**
		 * @param points
		 *            An array of floats with 9 elements, corresponding to the
		 *            (x,y,z) coordinates of the three vertices that make up
		 *            this polygon. If the three vertices are A, B, C then the
		 *            array should be [A_x, A_y, A_z, B_x, B_y, B_z, C_x, C_y,
		 *            C_z].
		 * @param color
		 *            An array of three ints corresponding to the RGB values of
		 *            the polygon, i.e. [r, g, b] where all values are between 0
		 *            and 255.
		 */
		public Polygon(float[] points, int[] color) {
			this.vertices = new Vector3D[3];

			float x, y, z;
			for (int i = 0; i < 3; i++) {
				x = points[i * 3];
				y = points[i * 3 + 1];
				z = points[i * 3 + 2];
				this.vertices[i] = new Vector3D(x, y, z);
			}

			int r = color[0];
			int g = color[1];
			int b = color[2];
			this.reflectance = new Color(r, g, b);
		}

		/**
		 * An alternative constructor that directly takes three Vector3D objects
		 * and a Color object.
		 */
		public Polygon(Vector3D a, Vector3D b, Vector3D c, Color color) {
			this.vertices = new Vector3D[] { a, b, c };
			this.reflectance = color;
		}

		public Vector3D[] getVertices() {
			return vertices;
		}

		public Color getReflectance() {
			return reflectance;
		}

		public Vector3D findNormal() {
			Vector3D pointA = this.vertices[0];
			Vector3D pointB = this.vertices[1];
			Vector3D pointC = this.vertices[2];

			Vector3D firstEdge = pointB.minus(pointA);
			Vector3D secondEdge = pointC.minus(pointB);

			return firstEdge.crossProduct(secondEdge);
		}

		@Override
		public String toString() {
			String str = "polygon:";

			for (Vector3D p : vertices)
				str += "\n  " + p.toString();

			str += "\n  " + reflectance.toString();

			return str;
		}
	}
}

// code for COMP261 assignments
