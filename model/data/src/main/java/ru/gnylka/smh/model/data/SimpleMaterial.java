package ru.gnylka.smh.model.data;

import java.util.Arrays;
import java.util.Objects;

// no values accept null
public final class SimpleMaterial {

    public final int[] ambient;     // [0; 255]
    public final int[] diffuse;     //
    public final int[] specular;    //
    public final int opacity;       //
    public final float shininess;
    public final String diffuseTexture;     // Empty if nothing
    public final String specularTexture;    // Empty if nothing

    public SimpleMaterial(int[] ambient,
                          int[] diffuse,
                          int[] specular,
                          int opacity,
                          float shininess,
                          String diffuseTexture,
                          String specularTexture) {
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.opacity = opacity;
        this.shininess = shininess;
        this.diffuseTexture = diffuseTexture;
        this.specularTexture = specularTexture;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleMaterial that = (SimpleMaterial) o;
        return opacity == that.opacity &&
                Float.compare(that.shininess, shininess) == 0 &&
                Arrays.equals(ambient, that.ambient) &&
                Arrays.equals(diffuse, that.diffuse) &&
                Arrays.equals(specular, that.specular) &&
                diffuseTexture.equals(that.diffuseTexture) &&
                specularTexture.equals(that.specularTexture);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(opacity, shininess, diffuseTexture, specularTexture);
        result = 31 * result + Arrays.hashCode(ambient);
        result = 31 * result + Arrays.hashCode(diffuse);
        result = 31 * result + Arrays.hashCode(specular);
        return result;
    }

    @Override
    public String toString() {
        return "SimpleMaterial{" +
                "ambient=" + Arrays.toString(ambient) +
                ", diffuse=" + Arrays.toString(diffuse) +
                ", specular=" + Arrays.toString(specular) +
                ", opacity=" + opacity +
                ", shininess=" + shininess +
                ", diffuseTexture='" + diffuseTexture + '\'' +
                ", specularTexture='" + specularTexture + '\'' +
                '}';
    }

}
