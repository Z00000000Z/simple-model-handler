package ru.gnylka.smh.model.data;

// no values accept null
public final class SimpleModel {

    // XXXCount stores the actual size of data (data in array may be stripped)
    // XXXCount specifies vertices

    public final float[] points;
    public final float[] pointsKeys;
    public final float[] pointsIndices;
    public final int pointsCount;

    public final float[] normals;
    public final float[] normalsIndices;
    public final int normalsCount;

    public final float[] texCoords;
    public final float[] texCoordsIndices;
    public final int texCoordsCount;

    public final short[][] parts;
    public final int[] facesCount;
    public final SimpleMaterial[] materials;
    public final SimpleNode[] nodes;
    public final String[] globalProperties;

    public SimpleModel(float[] points,
                       float[] normals,
                       float[] texCoords,
                       short[][] parts,
                       SimpleMaterial[] materials,
                       SimpleNode[] nodes,
                       String[] globalProperties) {
        this(points, new float[0], new float[0], points.length / 3,
                normals, new float[0], normals.length / 3,
                texCoords, new float[0], texCoords.length / 2,
                parts, getFacesCount(parts),
                materials, nodes, globalProperties);

        assert points.length % 3 == 0 :    "Points length must be divisible by 3";
        assert normals.length % 3 == 0 :   "Normals length must be divisible by 3";
        assert texCoords.length % 2 == 0 : "Texture coordinates length must be divisible by 3";
    }

    public SimpleModel(float[] points,
                       float[] pointsKeys,
                       float[] pointsIndices,
                       int pointsCount,
                       float[] normals,
                       float[] normalsIndices,
                       int normalsCount,
                       float[] texCoords,
                       float[] texCoordsIndices,
                       int texCoordsCount,
                       short[][] parts,
                       int[] facesCount,
                       SimpleMaterial[] materials,
                       SimpleNode[] nodes,
                       String[] globalProperties) {
        this.points = points;
        this.pointsKeys = pointsKeys;
        this.pointsIndices = pointsIndices;
        this.pointsCount = pointsCount;

        this.normals = normals;
        this.normalsIndices = normalsIndices;
        this.normalsCount = normalsCount;

        this.texCoords = texCoords;
        this.texCoordsIndices = texCoordsIndices;
        this.texCoordsCount = texCoordsCount;

        this.parts = parts;
        this.facesCount = facesCount;

        this.materials = materials;
        this.nodes = nodes;
        this.globalProperties = globalProperties;
    }

    private static int[] getFacesCount(short[][] parts) {
        int[] facesCount = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            facesCount[i] = parts[i].length / 3;
            assert parts[i].length % 3 == 0 : "Faces indices length must be divisible by 3";
        }
        return facesCount;
    }

}
