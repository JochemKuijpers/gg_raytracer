package nl.jochemkuijpers.app.scenes;

import nl.jochemkuijpers.math.Color;
import nl.jochemkuijpers.math.Vector3;
import nl.jochemkuijpers.raytrace.Ray;
import nl.jochemkuijpers.raytrace.Scene;
import nl.jochemkuijpers.raytrace.materials.ComplexMaterial;
import nl.jochemkuijpers.raytrace.materials.Material;
import nl.jochemkuijpers.raytrace.materials.SimpleMaterial;
import nl.jochemkuijpers.raytrace.shapes.Shape;
import nl.jochemkuijpers.raytrace.shapes.with_material.Plane;
import nl.jochemkuijpers.raytrace.shapes.with_material.Sphere;

import java.util.ArrayList;
import java.util.List;

public abstract class SimpleScene implements Scene {
    protected final List<Shape> sceneObjects;
    protected final Vector3 sunVector;

    SimpleScene() {
        sceneObjects = new ArrayList<>();
        sunVector = new Vector3(-0.2f, 1, 0.4f);
        Vector3.normalize(sunVector, sunVector);

        createScene();

        // a dark floor on y = 0
        Material floor = new ComplexMaterial(false, 1f, 1f, new Color(0.03f, 0.03f, 0.03f), sunVector);
        sceneObjects.add(new Plane(new Vector3(0, 0, 0), new Vector3(0, 1, 0), floor));

        // a gray sphere around the entire scene
        sceneObjects.add(new Sphere(new Vector3(0, 0, 0), 1000000, new SimpleMaterial(new Color(0.7f, 0.7f, 0.7f))));
    }

    protected abstract void createScene();

    @Override
    public float query(Ray ray, Color out) {
        if (ray.getDepth() > RECURSIVE_MAX_DEPTH) { return -1f; }
        Vector3 closestIntersection = new Vector3(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
        Vector3 intersection = new Vector3();
        Color color = new Color();

        // naively iterate over all objects.
        // there can be much improvement here with a proper spatial data structure as about 90% of rendering time is
        // spent in this method.
        for (Shape object : sceneObjects) {

            if (!object.query(ray, this, intersection, color)) {
                continue;
            }

            if (Vector3.distSqr(ray.getOrigin(), intersection) <
                    Vector3.distSqr(ray.getOrigin(), closestIntersection)) {
                closestIntersection.set(intersection);
                out.set(color);
            }
        }

        if (closestIntersection.x != Float.MAX_VALUE) {
            return Vector3.distSqr(ray.getOrigin(), closestIntersection);
        } else {
            return -1f;
        }
    }
}
