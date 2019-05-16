package nl.jochemkuijpers.app.scenes;

import nl.jochemkuijpers.math.Color;
import nl.jochemkuijpers.math.Vector3;
import nl.jochemkuijpers.raytrace.materials.ComplexMaterial;
import nl.jochemkuijpers.raytrace.materials.Material;
import nl.jochemkuijpers.raytrace.shapes.with_material.Box;
import nl.jochemkuijpers.raytrace.shapes.with_material.Sphere;

public class MaterialTestScene extends SimpleScene {
    @Override
    protected void createScene() {
        for (int i = 0; i < 10; i++) {
            Material material = new ComplexMaterial(false, i / 9f, 1f, Color.WHITE, sunVector);

            sceneObjects.add(
                    new Sphere(new Vector3(
                        (float) Math.cos(i / 9f * Math.PI) * 12f,
                        2f,
                        (float) Math.sin(i / 9f * Math.PI) * 12f
                    ),
                    1.5f,
                    material
            ));
        }
        Material boxMaterial = new ComplexMaterial(false, 0.98f, 1.53f, Color.WHITE, sunVector);
        sceneObjects.add(new Box(new Vector3(0, 4, 0), 4, boxMaterial));
    }
}
