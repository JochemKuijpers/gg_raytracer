package nl.jochemkuijpers.raytrace.materials;

import nl.jochemkuijpers.math.Color;
import nl.jochemkuijpers.math.Vector3;
import nl.jochemkuijpers.raytrace.Ray;
import nl.jochemkuijpers.raytrace.Scene;
import org.apache.commons.math3.util.FastMath;

/**
 * A quite complex material..
 *
 * It does diffuse color, refraction for transparent objects and reflection including fresnel reflections and
 * internal reflections.
 */
public class ComplexMaterial implements Material {
    private final boolean transparent;
    private final float absorption;
    private final float refractiveIndex;
    private final Color color;
    private final Vector3 sunVector;

    public ComplexMaterial(boolean transparent, float absorption, float refractiveIndex, Color color, Vector3 sunVector) {
        this.transparent = transparent;
        this.absorption = absorption;
        this.refractiveIndex = refractiveIndex;
        this.color = color;
        this.sunVector = new Vector3(sunVector);
        Vector3.normalize(this.sunVector, this.sunVector);
    }

    @Override
    public void queryColor(Ray ray, Scene world, Vector3 position, Vector3 normal, Color out) {
        if (ray.getDepth() > Scene.RECURSIVE_MAX_DEPTH) { return; }

        // since recursive computation isn't going to work anyway, we might as well just default to diffuse only.
        if (ray.getDepth() == Scene.RECURSIVE_MAX_DEPTH) {
            computeDiffuse(ray, world, position, normal, out);
            return;
        }

        float reflection;

        if (transparent) {
            reflection = computeRefraction(ray, world, position, normal, out);
        } else {
            if (absorption > 0f) {
                computeDiffuse(ray, world, position, normal, out);
                Vector3.mul(out, absorption, out);
            }
            reflection = 1 - absorption;
        }

        if (reflection > 0) {
            Color reflectColor = new Color();
            computeReflection(ray, world, position, normal, reflectColor);
            Vector3.addMul(out, reflectColor, reflection, out);
        }
    }

    /** Computes the sun-lit diffuse color */
    private void computeDiffuse(Ray ray, Scene world, Vector3 position, Vector3 normal, Color out) {
        float light = Vector3.dot(normal, sunVector);

        if (ray.getDepth() < Scene.RECURSIVE_MAX_DEPTH && !transparent) {
            Vector3.addMul(position, normal, 1e-3f, position);

            // We don't want recursive lookups for these sun visibility checks, so we'll give the sun-ray maximal depth.
            Ray sunRay = new Ray(Scene.RECURSIVE_MAX_DEPTH, position, sunVector);
            if (light > 0 && world.query(sunRay, new Color()) < 500) {
                // the scene is blocked in, but any ray that travels more than 500 units towards the sun
                // is considered in the open sky.
                light = 0;
            }
        }
        if (transparent) {
            light = Math.abs(light);
        } else {
            final float minLight = 0.01f;
            if (light < minLight) { light = minLight; }
        }

        Vector3.addMul(Vector3.ZERO, color, light, out);
    }

    /** Computes external reflection color */
    private void computeReflection(Ray ray, Scene world, Vector3 position, Vector3 normal, Color out) {
        Ray outRay = new Ray(ray.getDepth() + 1);
        Vector3 heading = outRay.getHeading();

        float cosi = Vector3.dot(ray.getHeading(), normal);
        Vector3.addMul(ray.getHeading(), normal, -2f * cosi, heading);
        Vector3.addMul(position, normal, 1e-3f, outRay.getOrigin());

        world.query(outRay, out);
    }

    /**
     * Computes reflection color and computes reflection amount
     * @return amount of reflection to add
     */
    private float computeRefraction(Ray ray, Scene world, Vector3 position, Vector3 normal, Color out) {
        float cosi = Vector3.dot(ray.getHeading(), normal);
        float etai = 1f;
        float etat = refractiveIndex;
        Vector3 N = new Vector3(normal);

        if (cosi < 0f) {
            cosi = -cosi;
        } else {
            Vector3.mul(N, -1f, N);
            etai = etat;
            etat = 1f;
        }

        float eta = etai / etat;
        float k = 1 - eta * eta * (1 - cosi * cosi);

        Ray outRay = new Ray(ray.getDepth() + 1);
        Vector3 heading = outRay.getHeading();

        if (k < 0) {
            Vector3.addMul(ray.getHeading(), N, -2*cosi, heading);
            Vector3.addMul(position, N, -1e-3f, outRay.getOrigin());
        } else {
            Vector3.addMul(Vector3.ZERO, ray.getHeading(), eta, heading);
            Vector3.addMul(heading, N, eta * cosi - (float) FastMath.sqrt(k), heading);
            Vector3.addMul(position, N, -1e-3f, outRay.getOrigin());
            Vector3.normalize(heading, heading);
        }

        float distance = world.query(outRay, out);

        if (Vector3.dot(heading, normal) < 0f) {
            float absorbed = 1f - (float) Math.pow(1f - this.absorption, distance);
            Vector3.addMul(Vector3.ZERO, out, 1 - absorbed, out);
            out.set(
                    out.x + out.x * color.x * absorbed,
                    out.y + out.y * color.y * absorbed,
                    out.z + out.z * color.z * absorbed
            );

            // internal reflection is already done
            return 0f;
        }

        // compute fresnel value
        float sint = (float) (etai / etat * FastMath.sqrt(Math.max(0f, 1f - cosi * cosi)));
        float cost = (float) FastMath.sqrt(Math.max(0.f, 1f - sint * sint));
        cosi = Math.abs(cosi);
        float Rs = ((etat * cosi) - (etai * cost)) / ((etat * cosi) + (etai * cost));
        float Rp = ((etai * cosi) - (etat * cost)) / ((etai * cosi) + (etat * cost));
        return (Rs * Rs + Rp * Rp) / 2f;
    }
}
