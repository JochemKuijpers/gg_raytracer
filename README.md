# Interactive Raytrace Renderer

This project is an interactive raytrace renderer. It was made as a Java code sample and demo as part of my application at Guerrilla.

It has the following features:

- Multithreaded CPU rendering.
- Three render passes of increasing quality to allow for responsive interactions.
- Recursive ray tracing.
- Diffuse materials with hard shadows by a directional light.
- Reflective materials.
- Refractive materials.
- Fresnel reflections (reflections on transparent objects).
- Semi-translucent objects that absorbs some light that travels through it.
- Gamma correction and dithering to avoid color-banding.
- GUI / resizable window that displays the frame buffer.

# How to build

This project is easily build using Maven. Make sure Maven and a Java 8 SDK are installed, open a command line on this directory and run

    $ mvn clean package

This will create an executable JAR file `target/gg_raytracer-1.0-SNAPSHOT-jar-with-dependencies.jar`

## How to run

From a command-line, run:

    $ java -jar target/gg_raytracer-1.0-SNAPSHOT-jar-with-dependencies.jar

# How does it work?

Rays are cast into a scene of geometric objects. Intersections with these objects are computed and based on the geometry of the object, and the material assigned to the object, successive rays may be cast for reflection, refraction and light visibility computations.

## In more detail

The Camera object partitions the framebuffer into small RenderTiles. New Jobs are created to render these tiles in three render phases of increasing quality. These jobs are added to a queue and worker threads pick up these jobs to execute them.

When a RenderTile is being rendered, it computes which rays to cast into the scene. It then queries the Scene object which colors are associated with the rays. The Scene object in turn queries Shape objects what their intersection and color is. The Scene object returns the color of the closest object.

A Shape object might have a material assigned to it, in that case it will ask the material to compute the color given the ray, intersection point and normal vector. The material in turn can choose to cast additional rays for reflections, etc.

Finally, when the RenderTile knows which color to put on which pixel, the color is gamma corrected and dithered and written to the frame buffer. The job is finished and the worker is released to work on a new job.

When the camera position is changed or the scene is changed due to user input, all queued jobs are discarded and workers are asked to interrupt their current job (or are left to finish it). The three render passes are then started anew.

### (Future work)

Currently the Scene object has no spatial data structure. By using one, ray/object intersections could be computed much faster. Currently about 90% of the running time of the most complex scene is lost on missing rays. Still, the renderer is quite fast.
