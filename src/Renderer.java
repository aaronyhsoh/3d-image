import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Renderer extends GUI {

	private Scene scene;
	private float xTranslate = 0.0f;
	private float yTranslate = 0.0f;
	private ArrayList<Color> lightColor = new ArrayList<>();

	@Override
	protected void onLoad(File file) {
		BufferedReader reader;
		Color color = new Color(255, 255, 255);

		lightColor.clear();
		lightColor.add(color);

		try {
			reader = new BufferedReader(new FileReader(file));
			String numRows = reader.readLine();
			List<Scene.Polygon> polygons = new ArrayList<>();

			for (int i = 0; i < Integer.parseInt(numRows); i++) {
				String line = reader.readLine();
				String[] values = line.split(",");

				int[] colour = new int[3];
				for (int j = 0; j < 3; j++) {
					colour[j] = Integer.parseInt(values[j]);
				}

				float[] vertices = new float[9];
				for (int k = 3; k < 12; k++) {
					vertices[k - 3] = Float.parseFloat(values[k]);
				}

				Scene.Polygon polygon = new Scene.Polygon(vertices, colour);
				polygons.add(polygon);
			}

			String[] light = reader.readLine().split(",");
			Vector3D lightPos = new Vector3D(Float.parseFloat(light[0]), Float.parseFloat(light[1]), Float.parseFloat(light[2]));
			ArrayList<Vector3D> lightSource = new ArrayList<>();
			lightSource.add(lightPos);
			scene = new Scene(polygons, lightSource);

			float[] imageBoundary = scene.getImageBoundary();
			float[] imageCentre = scene.getImageCentre();
			float xScale = GUI.CANVAS_WIDTH / (imageBoundary[1] - imageBoundary[0]) / 3;
			float yScale = GUI.CANVAS_HEIGHT / (imageBoundary[3] - imageBoundary[2]) / 3;

			scene = Pipeline.scaleScene(scene, Math.min(xScale, yScale));
			float[] newImageCentre = scene.getImageCentre();
			xTranslate = 300.0f - newImageCentre[0];
			yTranslate = 300.0f - newImageCentre[1];
		}
		catch(IOException e) {
			e.printStackTrace();
		}

		/*
		 * This method should parse the given file into a Scene object, which
		 * you store and use to render an image.
		 */
	}

	@Override
	protected void onKeyPress(KeyEvent ev) {
		int keyPressed = ev.getKeyCode();
		switch (keyPressed) {
			case KeyEvent.VK_W :
				yTranslate -= 10.0f;
				break;
			case KeyEvent.VK_A :
				xTranslate -= 10.0f;
				break;
			case KeyEvent.VK_S :
				yTranslate += 10.0f;
				break;
			case KeyEvent.VK_D :
				xTranslate += 10.0f;
				break;
			case KeyEvent.VK_UP :
				Pipeline.rotateScene(scene, -0.1f, 0.0f);
				break;
			case KeyEvent.VK_LEFT :
				Pipeline.rotateScene(scene, 0.0f, 0.1f);
				break;
			case KeyEvent.VK_DOWN :
				Pipeline.rotateScene(scene, 0.1f, 0.0f);
				break;
			case KeyEvent.VK_RIGHT :
				Pipeline.rotateScene(scene, 0.0f, -0.1f);
				break;
		}

	}

	@Override
	protected void addNewLight() {
		Random randomNumber = new Random();
		int red = randomNumber.nextInt(255);
		int green = randomNumber.nextInt(255);
		int blue = randomNumber.nextInt(255);

		Color color = new Color(red, green, blue);
		lightColor.add(color);

		scene.addLightSource();
	}

	@Override
	protected void removePrevLight() {
		int lastAddedIndex = lightColor.size() - 1;
		lightColor.remove(lastAddedIndex);
		scene.removeLightsource(lastAddedIndex);
	}

	@Override
	protected BufferedImage render() {

		Color[][] zbuffer = new Color[CANVAS_WIDTH][CANVAS_HEIGHT];
		float[][] zdepth = new float[CANVAS_WIDTH][CANVAS_HEIGHT];
		Color ambientLight = new Color(getAmbientLight()[0], getAmbientLight()[1], getAmbientLight()[2]);

		for (int i = 0; i < CANVAS_WIDTH; i++) {
			for (int j = 0; j < CANVAS_HEIGHT; j++) {
				zbuffer[i][j] = Color.GRAY;
				zdepth[i][j] = Float.POSITIVE_INFINITY;
			}
		}

		if (scene == null) {
			return null;
		}

		scene = Pipeline.translateScene(scene, -scene.getImageBoundary()[0], -scene.getImageBoundary()[2]);
		scene = Pipeline.translateScene(scene, xTranslate, yTranslate);

		for (Scene.Polygon polygon : scene.getPolygons()) {
			if (!Pipeline.isHidden(polygon)) {
				EdgeList edgeList = Pipeline.computeEdgeList(polygon);
				Color color = Pipeline.getShading(polygon, scene.getLight(), lightColor, ambientLight);
				Pipeline.computeZBuffer(zbuffer, zdepth, edgeList, color);
			}
		}

		return convertBitmapToImage(zbuffer);
	}

	/**
	 * Converts a 2D array of Colors to a BufferedImage. Assumes that bitmap is
	 * indexed by column then row and has imageHeight rows and imageWidth
	 * columns. Note that image.setRGB requires x (col) and y (row) are given in
	 * that order.
	 */
	private BufferedImage convertBitmapToImage(Color[][] bitmap) {
		BufferedImage image = new BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < CANVAS_WIDTH; x++) {
			for (int y = 0; y < CANVAS_HEIGHT; y++) {
				image.setRGB(x, y, bitmap[x][y].getRGB());
			}
		}
		return image;
	}

	public static void main(String[] args) {
		new Renderer();
	}
}

// code for comp261 assignments
