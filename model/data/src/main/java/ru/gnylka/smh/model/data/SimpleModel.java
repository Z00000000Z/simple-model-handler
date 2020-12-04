package ru.gnylka.smh.model.data;

import java.util.Arrays;
import java.util.Objects;

// no values accept null
public final class SimpleModel {

    private static final float[] ZERO_FLOAT_ARRAY = new float[0];

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
    public final float[] texCoordsKeys;
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
        this(points, ZERO_FLOAT_ARRAY, ZERO_FLOAT_ARRAY, points.length / 3,
                normals, ZERO_FLOAT_ARRAY, normals.length / 3,
                texCoords, ZERO_FLOAT_ARRAY, ZERO_FLOAT_ARRAY, texCoords.length / 2,
                parts, getFacesCount(parts),
                materials, nodes, globalProperties);

        assert points.length % 3 == 0 :    "Points length must be divisible by 3";
        assert normals.length % 3 == 0 :   "Normals length must be divisible by 3";
        assert texCoords.length % 2 == 0 : "Texture coordinates length must be divisible by 2";
    }

    public SimpleModel(float[] points,
                       float[] pointsKeys,
                       float[] pointsIndices,
                       int pointsCount,
                       float[] normals,
                       float[] normalsIndices,
                       int normalsCount,
                       float[] texCoords,
                       float[] texCoordsKeys,
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
        this.texCoordsKeys = texCoordsKeys;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleModel that = (SimpleModel) o;
        return pointsCount == that.pointsCount &&
                normalsCount == that.normalsCount &&
                texCoordsCount == that.texCoordsCount &&
                Arrays.equals(points, that.points) &&
                Arrays.equals(pointsKeys, that.pointsKeys) &&
                Arrays.equals(pointsIndices, that.pointsIndices) &&
                Arrays.equals(normals, that.normals) &&
                Arrays.equals(normalsIndices, that.normalsIndices) &&
                Arrays.equals(texCoords, that.texCoords) &&
                Arrays.equals(texCoordsKeys, that.texCoordsKeys) &&
                Arrays.equals(texCoordsIndices, that.texCoordsIndices) &&
                Arrays.equals(parts, that.parts) &&
                Arrays.equals(facesCount, that.facesCount) &&
                Arrays.equals(materials, that.materials) &&
                Arrays.equals(nodes, that.nodes) &&
                Arrays.equals(globalProperties, that.globalProperties);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(pointsCount, normalsCount, texCoordsCount);
        result = 31 * result + Arrays.hashCode(points);
        result = 31 * result + Arrays.hashCode(pointsKeys);
        result = 31 * result + Arrays.hashCode(pointsIndices);
        result = 31 * result + Arrays.hashCode(normals);
        result = 31 * result + Arrays.hashCode(normalsIndices);
        result = 31 * result + Arrays.hashCode(texCoords);
        result = 31 * result + Arrays.hashCode(texCoordsKeys);
        result = 31 * result + Arrays.hashCode(texCoordsIndices);
        result = 31 * result + Arrays.hashCode(parts);
        result = 31 * result + Arrays.hashCode(facesCount);
        result = 31 * result + Arrays.hashCode(materials);
        result = 31 * result + Arrays.hashCode(nodes);
        result = 31 * result + Arrays.hashCode(globalProperties);
        return result;
    }

    @Override
    public String toString() {
        return "SimpleModel{" +
                "points=" + Arrays.toString(points) +
                ", pointsKeys=" + Arrays.toString(pointsKeys) +
                ", pointsIndices=" + Arrays.toString(pointsIndices) +
                ", pointsCount=" + pointsCount +
                ", normals=" + Arrays.toString(normals) +
                ", normalsIndices=" + Arrays.toString(normalsIndices) +
                ", normalsCount=" + normalsCount +
                ", texCoords=" + Arrays.toString(texCoords) +
                ", texCoordsKeys=" + Arrays.toString(texCoordsKeys) +
                ", texCoordsIndices=" + Arrays.toString(texCoordsIndices) +
                ", texCoordsCount=" + texCoordsCount +
                ", parts=" + Arrays.toString(parts) +
                ", facesCount=" + Arrays.toString(facesCount) +
                ", materials=" + Arrays.toString(materials) +
                ", nodes=" + Arrays.toString(nodes) +
                ", globalProperties=" + Arrays.toString(globalProperties) +
                '}';
    }

}
