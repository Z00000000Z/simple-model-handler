package ru.gnylka.smh.model.data;

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

}
