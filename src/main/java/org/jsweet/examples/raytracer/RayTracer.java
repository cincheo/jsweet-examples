package org.jsweet.examples.raytracer;

import static jsweet.dom.Globals.console;
import static jsweet.dom.Globals.document;
import static jsweet.dom.Globals.window;
import static jsweet.lang.Globals.Infinity;
import static jsweet.util.Globals.array;
import static jsweet.util.Globals.union;

import java.util.function.BiFunction;
import java.util.function.Function;

import jsweet.dom.CanvasRenderingContext2D;
import jsweet.dom.HTMLCanvasElement;
import jsweet.lang.Math;
import jsweet.lang.String;
import jsweet.util.StringTypes;
import jsweet.util.function.TriFunction;

class Vector {
	public double x;
	public double y;
	public double z;

	public Vector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	static Vector times(double k, Vector v) {
		return new Vector(k * v.x, k * v.y, k * v.z);
	}

	static Vector minus(Vector v1, Vector v2) {
		return new Vector(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
	}

	static Vector plus(Vector v1, Vector v2) {
		return new Vector(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
	}

	static double dot(Vector v1, Vector v2) {
		return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
	}

	static double mag(Vector v) {
		return Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
	}

	static Vector norm(Vector v) {
		double mag = Vector.mag(v);
		double div = (mag == 0) ? Infinity : 1.0 / mag;
		return Vector.times(div, v);
	}

	static Vector cross(Vector v1, Vector v2) {
		return new Vector(v1.y * v2.z - v1.z * v2.y, v1.z * v2.x - v1.x * v2.z, v1.x * v2.y - v1.y * v2.x);
	}

}

class Color {
	public double r;
	public double g;
	public double b;

	public Color(double r, double g, double b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	static Color scale(double k, Color v) {
		return new Color(k * v.r, k * v.g, k * v.b);
	}

	static Color plus(Color v1, Color v2) {
		return new Color(v1.r + v2.r, v1.g + v2.g, v1.b + v2.b);
	}

	static Color times(Color v1, Color v2) {
		return new Color(v1.r * v2.r, v1.g * v2.g, v1.b * v2.b);
	}

	static Color white = new Color(1.0, 1.0, 1.0);
	static Color grey = new Color(0.5, 0.5, 0.5);
	static Color black = new Color(0.0, 0.0, 0.0);
	static Color background = Color.black;
	static Color defaultColor = Color.black;

	static Color toDrawingColor(Color c) {
		Function<Double, Double> legalize = d -> d > 1 ? 1 : d;
		return new Color(Math.floor(legalize.apply(c.r) * 255), Math.floor(legalize.apply(c.g) * 255),
				Math.floor(legalize.apply(c.b) * 255));
	}
}

class Camera {
	public Vector forward;
	public Vector right;
	public Vector up;
	public Vector pos;

	Camera(Vector pos, Vector lookAt) {
		this.pos = pos;
		Vector down = new Vector(0.0, -1.0, 0.0);
		this.forward = Vector.norm(Vector.minus(lookAt, this.pos));
		this.right = Vector.times(1.5, Vector.norm(Vector.cross(this.forward, down)));
		this.up = Vector.times(1.5, Vector.norm(Vector.cross(this.forward, this.right)));
	}
}

class Ray {
	public Ray(Vector start, Vector dir) {
		super();
		this.start = start;
		this.dir = dir;
	}

	Vector start;
	Vector dir;
}

class Intersection {
	public Intersection(Thing thing, Ray ray, double dist) {
		this.thing = thing;
		this.ray = ray;
		this.dist = dist;
	}

	Thing thing;
	Ray ray;
	double dist;
}

class Surface {

	public Surface(Function<Vector, Color> diffuse, Function<Vector, Color> specular, Function<Vector, Double> reflect,
			double roughness) {
		this.diffuse = diffuse;
		this.specular = specular;
		this.reflect = reflect;
		this.roughness = roughness;
	}

	Function<Vector, Color> diffuse;
	Function<Vector, Color> specular;
	Function<Vector, Double> reflect;
	double roughness;
}

abstract class Thing {
	abstract Intersection intersect(Ray ray);

	abstract Vector normal(Vector vector);

	Surface surface;
}

class Light {
	public Light(Vector pos, Color color) {
		super();
		this.pos = pos;
		this.color = color;
	}

	Vector pos;
	Color color;
}

class Scene {
	public Scene(Thing[] things, Light[] lights, Camera camera) {
		super();
		this.things = things;
		this.lights = lights;
		this.camera = camera;
	}

	Thing[] things;
	Light[] lights;
	Camera camera;
}

class Sphere extends Thing {
	double radius2;
	Vector center;
	Surface surface;

	Sphere(Vector center, double radius, Surface surface) {
		this.center = center;
		this.radius2 = radius * radius;
		this.surface = surface;
	}

	Vector normal(Vector pos) {
		return Vector.norm(Vector.minus(pos, this.center));
	}

	Intersection intersect(Ray ray) {
		Vector eo = Vector.minus(this.center, ray.start);
		double v = Vector.dot(eo, ray.dir);
		double dist = 0;
		if (v >= 0) {
			double disc = this.radius2 - (Vector.dot(eo, eo) - v * v);
			if (disc >= 0) {
				dist = v - Math.sqrt(disc);
			}
		}
		if (dist == 0) {
			return null;
		} else {
			return new Intersection(this, ray, dist);
		}
	}
}

class Plane extends Thing {
	Surface surface;
	Vector norm;
	double offset;

	Plane(Vector norm, double offset, Surface surface) {
		this.norm = norm;
		this.offset = offset;
		this.surface = surface;
	}

	@Override
	Intersection intersect(Ray ray) {
		double denom = Vector.dot(norm, ray.dir);
		if (denom > 0) {
			return null;
		} else {
			double dist = (Vector.dot(norm, ray.start) + offset) / (-denom);
			return new Intersection(this, ray, dist);
		}
	}

	@Override
	Vector normal(Vector pos) {
		return norm;
	}

}

class Surfaces {

	static Surface shiny = new Surface((pos) -> {
		return Color.white;
	} , (pos) -> {
		return Color.grey;
	} , (pos) -> {
		return 0.7;
	} , 250);

	static Surface checkerboard = new Surface((pos) -> {
		if ((Math.floor(pos.z) + Math.floor(pos.x)) % 2 != 0) {
			return Color.white;
		} else {
			return Color.black;
		}
	} , (pos) -> {
		return Color.white;
	} , (pos) -> {
		if ((Math.floor(pos.z) + Math.floor(pos.x)) % 2 != 0) {
			return 0.1;
		} else {
			return 0.7;
		}
	} , 150);

}

class RayTracer {
	private int maxDepth = 5;

	private Intersection intersections(Ray ray, Scene scene) {
		double closest = +Infinity;
		Intersection closestInter = null;
		for (int i = 0; i < scene.things.length; i++) {
			Intersection inter = scene.things[i].intersect(ray);
			if (inter != null && inter.dist < closest) {
				closestInter = inter;
				closest = inter.dist;
			}
		}
		return closestInter;
	}

	private Double testRay(Ray ray, Scene scene) {
		Intersection isect = this.intersections(ray, scene);
		if (isect != null) {
			return isect.dist;
		} else {
			return null;
		}
	}

	private Color traceRay(Ray ray, Scene scene, int depth) {
		Intersection isect = this.intersections(ray, scene);
		if (isect == null) {
			return Color.background;
		} else {
			return this.shade(isect, scene, depth);
		}
	}

	private Color shade(Intersection isect, Scene scene, int depth) {
		Vector d = isect.ray.dir;
		Vector pos = Vector.plus(Vector.times(isect.dist, d), isect.ray.start);
		Vector normal = isect.thing.normal(pos);
		Vector reflectDir = Vector.minus(d, Vector.times(2, Vector.times(Vector.dot(normal, d), normal)));
		Color naturalColor = Color.plus(Color.background,
				this.getNaturalColor(isect.thing, pos, normal, reflectDir, scene));
		Color reflectedColor = (depth >= this.maxDepth) ? Color.grey
				: this.getReflectionColor(isect.thing, pos, normal, reflectDir, scene, depth);
		return Color.plus(naturalColor, reflectedColor);
	}

	private Color getReflectionColor(Thing thing, Vector pos, Vector normal, Vector rd, Scene scene, int depth) {
		return Color.scale(thing.surface.reflect.apply(pos), this.traceRay(new Ray(pos, rd), scene, depth + 1));
	}

	private Color getNaturalColor(Thing thing, Vector pos, Vector norm, Vector rd, Scene scene) {
		BiFunction<Color, Light, Color> addLight = (col, light) -> {
			Vector ldis = Vector.minus(light.pos, pos);
			Vector livec = Vector.norm(ldis);
			Double neatIsect = this.testRay(new Ray(pos, livec), scene);
			boolean isInShadow = (neatIsect == null) ? false : (neatIsect <= Vector.mag(ldis));
			if (isInShadow) {
				return col;
			} else {
				double illum = Vector.dot(livec, norm);
				Color lcolor = (illum > 0) ? Color.scale(illum, light.color) : Color.defaultColor;
				double specular = Vector.dot(livec, Vector.norm(rd));
				Color scolor = (specular > 0) ? Color.scale(Math.pow(specular, thing.surface.roughness), light.color)
						: Color.defaultColor;
				return Color.plus(col, Color.plus(Color.times(thing.surface.diffuse.apply(pos), lcolor),
						Color.times(thing.surface.specular.apply(pos), scolor)));
			}
		};
		return array(scene.lights).reduce(addLight, Color.defaultColor);
	}

	void render(Scene scene, CanvasRenderingContext2D ctx, int screenWidth, int screenHeight) {
		TriFunction<Double, Double, Camera, Vector> getPoint = (x, y, camera) -> {
			Function<Double, Double> recenterX = (x2) -> {
				return ((x2 - (screenWidth / 2.0)) / 2.0 / screenWidth);
			};
			Function<Double, Double> recenterY = (y2) -> {
				return -((y2 - (screenHeight / 2.0)) / 2.0 / screenHeight);
			};
			return Vector.norm(Vector.plus(camera.forward, Vector.plus(Vector.times(recenterX.apply(x), camera.right),
					Vector.times(recenterY.apply(y), camera.up))));
		};
		for (double y = 0; y < screenHeight; y++) {
			for (double x = 0; x < screenWidth; x++) {
				Color color = this.traceRay(new Ray(scene.camera.pos, getPoint.apply(x, y, scene.camera)), scene, 0);
				Color c = Color.toDrawingColor(color);
				ctx.fillStyle = union("rgb(" + new String(c.r) + ", " + new String(c.g) + ", " + new String(c.b) + ")");
				ctx.fillRect(x, y, x + 1, y + 1);
			}
		}
	}
}

class Globals {

	static Scene defaultScene() {
		return new Scene(
				new Thing[] { new Plane(new Vector(0.0, 1.0, 0.0), 0.0, Surfaces.checkerboard),
						new Sphere(new Vector(0.0, 1.0, -0.25), 1.0, Surfaces.shiny),
						new Sphere(new Vector(-1.0, 0.5, 1.5), 0.5, Surfaces.shiny) },
				new Light[] { new Light(new Vector(-2.0, 2.5, 0.0), new Color(0.49, 0.07, 0.07)),
						new Light(new Vector(1.5, 2.5, 1.5), new Color(0.07, 0.07, 0.49)),
						new Light(new Vector(1.5, 2.5, -1.5), new Color(0.07, 0.49, 0.071)),
						new Light(new Vector(0.0, 3.5, 0.0), new Color(0.21, 0.21, 0.35)) },
				new Camera(new Vector(3.0, 2.0, 4.0), new Vector(-1.0, 0.5, 0.0)));
	}

	static void main() {
		int size = 256;
		double windowSize = Math.min(window.innerWidth, window.innerHeight);
		HTMLCanvasElement canv = document.createElement(StringTypes.canvas);
		canv.width = size;
		canv.height = size;
		canv.style.transform = "scale(" + (windowSize / size) + "," + (windowSize / size) + ")";
		canv.style.transformOrigin = "0 0";
		console.log("transform=" + canv.style.transform);
		document.body.appendChild(canv);
		CanvasRenderingContext2D ctx = canv.getContext(StringTypes._2d);
		RayTracer rayTracer = new RayTracer();
		rayTracer.render(defaultScene(), ctx, size, size);
	}

}