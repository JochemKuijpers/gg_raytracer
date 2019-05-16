package nl.jochemkuijpers.app.scenes;

import nl.jochemkuijpers.math.Color;
import nl.jochemkuijpers.math.Vector3;
import nl.jochemkuijpers.raytrace.materials.*;
import nl.jochemkuijpers.raytrace.shapes.with_material.Box;
import nl.jochemkuijpers.raytrace.shapes.with_material.Sphere;

public class StackedShapeScene extends SimpleScene {

    @Override
    protected void createScene() {
        // just a bunch of colored shapes :)
        for (int j = 0; j < 6; j++) {
            for (int i = 0; i < 6; i++) {
                boolean transparent = (i + j) % 2 == 0;
                float absorbtion = transparent ? 0.15f : 0.7f;
                Material material = new ComplexMaterial(
                        transparent, absorbtion, 1.56f,
                        Color.createHSL((i * 6 + j) * 10f, 0.75f, 0.5f),
                        sunVector
                );

                sceneObjects.add(new Box(new Vector3(-10f + i * 4 , 1.01f, -10f + j * 4), 1f, material));
                sceneObjects.add(new Sphere(new Vector3(-10f + i * 4 , 3.5f, -10f + j * 4), 1f, material));
            }
        }
    }

}
