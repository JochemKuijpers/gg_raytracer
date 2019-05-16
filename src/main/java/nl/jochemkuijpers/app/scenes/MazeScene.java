package nl.jochemkuijpers.app.scenes;

import nl.jochemkuijpers.math.Color;
import nl.jochemkuijpers.math.Vector3;
import nl.jochemkuijpers.raytrace.materials.ComplexMaterial;
import nl.jochemkuijpers.raytrace.materials.Material;
import nl.jochemkuijpers.raytrace.shapes.with_material.Box;
import nl.jochemkuijpers.raytrace.shapes.with_material.Sphere;

public class MazeScene extends SimpleScene {
    @Override
    protected void createScene() {
        // a maze with a golden shrine in the middle
        int[] maze = new int[] {
                1, 1, 1, 1, 1, 1, 0, 0, 1, 1,
                1, 0, 0, 1, 0, 0, 0, 0, 0, 1,
                1, 0, 1, 1, 1, 1, 1, 1, 0, 1,
                1, 0, 1, 0, 0, 0, 0, 1, 0, 1,
                0, 0, 1, 0, 0, 0, 0, 1, 0, 1,
                0, 0, 1, 0, 0, 0, 0, 1, 0, 1,
                1, 0, 1, 0, 0, 0, 0, 0, 0, 1,
                1, 0, 1, 1, 0, 1, 1, 1, 0, 1,
                1, 0, 0, 0, 0, 0, 1, 0, 0, 1,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
        };

        Material hedge = new ComplexMaterial(false, 1f, 1f, Color.GREEN, sunVector);

        for (int j = 0; j < 10; j++) {
            for (int i = 0; i < 10; i++) {
                if (maze[j*10 + i] == 0) { continue; }

                sceneObjects.add(new Box(
                        new Vector3(3 * i - 15, 0, 3 * j - 15),
                        new Vector3(3 * (i + 1) - 15, 4, 3 * (j + 1) - 15),
                        hedge
                ));
            }
        }

        Material gold = new ComplexMaterial(false, 0.5f, 1f, Color.YELLOW, sunVector);
        sceneObjects.add(new Sphere(new Vector3(0, 3, 0), 2, gold));
        sceneObjects.add(new Box(new Vector3(0, -2, 0), 3, gold));
    }
}
