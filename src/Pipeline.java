import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * The Pipeline class has method stubs for all the major components of the
 * rendering pipeline, for you to fill in.
 * 
 * Some of these methods can get quite long, in which case you should strongly
 * consider moving them out into their own file. You'll need to update the
 * imports in the test suite if you do.
 */
public class Pipeline {

	/**
	 * Returns true if the given polygon is facing away from the camera (and so
	 * should be hidden), and false otherwise.
	 */
	public static boolean isHidden(Scene.Polygon poly) {
		if (poly.findNormal().z > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Computes the colour of a polygon on the screen, once the lights, their
	 * angles relative to the polygon's face, and the reflectance of the polygon
	 * have been accounted for.
	 * 
	 * @param lightDirection
	 *            The Vector3D pointing to the directional light read in from
	 *            the file.
	 * @param lightColor
	 *            The color of that directional light.
	 * @param ambientLight
	 *            The ambient light in the scene, i.e. light that doesn't depend
	 *            on the direction.
	 */
	public static Color getShading(Scene.Polygon poly, ArrayList<Vector3D> lightDirection, ArrayList<Color> lightColor, Color ambientLight) {
		Vector3D normalUnitVector = poly.findNormal().unitVector();
		Color reflectance = poly.getReflectance();
		float cosTheta;
		float lightSourceRed = 0.0f, lightSourceGreen = 0.0f, lightSourceBlue = 0.0f;
		int red, green, blue;

		for (int i = 0; i < lightDirection.size(); i++) {
			Vector3D lightUnitVector = lightDirection.get(i).unitVector();
			Color color = lightColor.get(i);
			cosTheta = normalUnitVector.dotProduct(lightUnitVector);
			cosTheta = cosTheta > 0 ? cosTheta : 0;

			lightSourceRed += (color.getRed() * cosTheta);
			lightSourceGreen += (color.getGreen() * cosTheta);
			lightSourceBlue += (color.getBlue() * cosTheta);
		}

		red = Math.round((reflectance.getRed() / 255.0f) * (ambientLight.getRed() +  lightSourceRed)); //(lightColor.getRed() * cosTheta)));
		green = Math.round((reflectance.getGreen() / 255.0f) * (ambientLight.getGreen() + lightSourceGreen)); //(lightColor.getGreen() * cosTheta)));
		blue = Math.round((reflectance.getBlue() / 255.0f) * (ambientLight.getBlue() + lightSourceBlue)); //(lightColor.getBlue() * cosTheta)));

		red = red > 255 ? 255 : red;
		green = green > 255 ? 255 : green;
		blue = blue > 255 ? 255 : blue;

		Color shading = new Color(red, green, blue);

		return shading;
	}

	/**
	 * This method should rotate the polygons and light such that the viewer is
	 * looking down the Z-axis. The idea is that it returns an entirely new
	 * Scene object, filled with new Polygons, that have been rotated.
	 * 
	 * @param scene
	 *            The original Scene.
	 * @param xRot
	 *            An angle describing the viewer's rotation in the YZ-plane (i.e
	 *            around the X-axis).
	 * @param yRot
	 *            An angle describing the viewer's rotation in the XZ-plane (i.e
	 *            around the Y-axis).
	 * @return A new Scene where all the polygons and the light source have been
	 *         rotated accordingly.
	 */
	public static Scene rotateScene(Scene scene, float xRot, float yRot) {
		Transform xRotationMatrix = Transform.newXRotation(xRot);
		Transform yRotationMatrix = Transform.newYRotation(yRot);
		System.out.println("x: " + xRotationMatrix);
		System.out.println("y: " + yRotationMatrix);

		List<Scene.Polygon> rotatedPolygons = scene.getPolygons();

		for (Scene.Polygon polygon : rotatedPolygons) {
			Vector3D[] vertices = polygon.getVertices();
			for (int i = 0; i < vertices.length; i++) {
				if (xRot != 0.0f) {
					vertices[i] = xRotationMatrix.multiply(vertices[i]);
				}
				if (yRot != 0.0f) {
					vertices[i] = yRotationMatrix.multiply(vertices[i]);
				}
			}
		}

		scene.setImageBoundary();
		scene.setImageCentre(scene.getImageBoundary());
		ArrayList<Vector3D> lightSource = scene.getLight();

		return new Scene(rotatedPolygons, lightSource);
	}

	/**
	 * This should translate the scene by the appropriate amount.
	 * 
	 * @param scene
	 * @return
	 */
	public static Scene translateScene(Scene scene, float xTranslate, float yTranslate) {
		Transform translationMatrix = Transform.newTranslation(xTranslate, yTranslate, 0.0f);

		List<Scene.Polygon> translatedPolygons = scene.getPolygons();

		for (Scene.Polygon polygon : translatedPolygons) {
			Vector3D[] vertices = polygon.getVertices();
			for (int i = 0; i < vertices.length; i++) {
				vertices[i] = translationMatrix.multiply(vertices[i]);
			}
		}

		scene.setImageBoundary();
		scene.setImageCentre(scene.getImageBoundary());
		ArrayList<Vector3D> lightSource = scene.getLight();

		return new Scene(translatedPolygons, lightSource);
	}

	/**
	 * This should scale the scene.
	 * 
	 * @param scene
	 * @return
	 */
	public static Scene scaleScene(Scene scene, float scaleFactor) {
		Transform scaleMatrix = Transform.newScale(scaleFactor, scaleFactor, scaleFactor);

		List<Scene.Polygon> scaledPolygons = scene.getPolygons();

		for (Scene.Polygon polygon : scaledPolygons) {
			Vector3D[] vertices = polygon.getVertices();
			for (int i = 0; i < vertices.length; i++) {
				vertices[i] = scaleMatrix.multiply(vertices[i]);
			}
		}

		scene.setImageBoundary();
		scene.setImageCentre(scene.getImageBoundary());
		ArrayList<Vector3D> lightSource = scene.getLight();

		return new Scene(scaledPolygons, lightSource);
	}

	/**
	 * Computes the edgelist of a single provided polygon, as per the lecture
	 * slides.
	 */
	public static EdgeList computeEdgeList(Scene.Polygon poly) {
		Vector3D[] vertices = poly.vertices;
		int yMin = Math.round(Math.min(Math.min(vertices[0].y, vertices[1].y), vertices[2].y));
		int yMax = Math.round(Math.max(Math.max(vertices[0].y, vertices[1].y), vertices[2].y));

		EdgeList edgeList = new EdgeList(yMin, yMax);

		for (int i = 0; i < 3; i++) {
			Vector3D vertexA = vertices[i];
			Vector3D vertexB = vertices[(i + 1) % 3];
			//System.out.println(i);

			float xSlope = (vertexB.x - vertexA.x) / (vertexB.y - vertexA.y);
			float zSlope = (vertexB.z - vertexA.z) / (vertexB.y - vertexA.y);
			float x = vertexA.x;
			float z = vertexA.z;
			int y = Math.round(vertexA.y);

			if (vertexA.y == vertexB.y) {
				continue;
			}

			else if (vertexA.y < vertexB.y) {
				while (y <= Math.round(vertexB.y)) {
					edgeList.setLeftRow(y, x, z);
					x += xSlope;
					z += zSlope;
					y++;
				}
			}
			else {
				while (y >= Math.round(vertexB.y)) {
					edgeList.setRightRow(y, x, z);
					x -= xSlope;
					z -= zSlope;
					y--;
				}
			}
		}

		return edgeList;
	}

	/**
	 * Fills a zbuffer with the contents of a single edge list according to the
	 * lecture slides.
	 * 
	 * The idea here is to make zbuffer and zdepth arrays in your main loop, and
	 * pass them into the method to be modified.
	 * 
	 * @param zbuffer
	 *            A double array of colours representing the Color at each pixel
	 *            so far.
	 * @param zdepth
	 *            A double array of floats storing the z-value of each pixel
	 *            that has been coloured in so far.
	 * @param polyEdgeList
	 *            The edgelist of the polygon to add into the zbuffer.
	 * @param polyColor
	 *            The colour of the polygon to add into the zbuffer.
	 */
	public static void computeZBuffer(Color[][] zbuffer, float[][] zdepth, EdgeList polyEdgeList, Color polyColor) {
		int yMin = polyEdgeList.getYMin();
		int yMax = polyEdgeList.getYMax();

		for (int y = yMin; y < yMax; y++) {
			float slope = (polyEdgeList.getRightZ(y) - polyEdgeList.getLeftZ(y)) / (polyEdgeList.getRightX(y) - polyEdgeList.getLeftX(y));
			int x = Math.round(polyEdgeList.getLeftX(y));
			float z = polyEdgeList.getLeftZ(y);

			while (x <= Math.round(polyEdgeList.getRightX(y))) {
				if (!(x < 0 || y < 0 || x >= GUI.CANVAS_WIDTH || y >= GUI.CANVAS_HEIGHT)) {
					if (z < zdepth[x][y]) {
						zbuffer[x][y] = polyColor;
						zdepth[x][y] = z;
					}
				}
				z += slope;
				x++;
			}
		}
	}
}

// code for comp261 assignments
