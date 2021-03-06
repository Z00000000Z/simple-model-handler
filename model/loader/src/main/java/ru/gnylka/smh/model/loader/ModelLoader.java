package ru.gnylka.smh.model.loader;

import ru.gnylka.smh.model.data.SimpleMaterial;
import ru.gnylka.smh.model.data.SimpleModel;
import ru.gnylka.smh.model.data.SimpleNode;
import ru.gnylka.smh.model.data.SimpleNodePart;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static java.lang.Math.abs;

public final class ModelLoader {

    private static final int POINT_SIZE = 3;
    private static final int NORMAL_SIZE = 3;
    private static final int TEX_COORD_SIZE = 2;
    private static final int FACE_SIZE = 3;

    private ModelLoader(DataInputStream input) {
        this.input = input;
    }

    private final DataInputStream input;

    private float[] points;
    private float[] pointsKeys;
    private float[] pointsIndices;

    private float[] normals;
    private float[] normalsIndices;

    private float[] texCoords;
    private float[] texCoordsKeys;
    private float[] texCoordsIndices;

    private short[][] parts;
    private int[] facesCount;
    private SimpleMaterial[] materials;
    private SimpleNode[] nodes;
    private String[] globalProperties;

    /**
     * Loads model from specified input stream
     * <br>
     * Note that any optimizations applied to the model are resolved
     *
     * @param inputStream stream to load model from
     *
     * @return model object
     *
     * @throws IOException if IOException occurs
     */
    public static SimpleModel load(InputStream inputStream) throws IOException {
        var input = new DataInputStream(inputStream);

        var loader = new ModelLoader(input);
        loader.globalProperties = loader.readProperties();
        loader.readPointsKeys();
        loader.readPointsIndices();
        loader.readNormalsIndices();
        loader.readTexCoordsKeys();
        loader.readTexCoordsIndices();
        loader.readPoints();
        loader.readNormals();
        loader.readTexCoords();
        loader.readFaces();
        loader.readMaterials();
        loader.readNodes();

        float[] emptyArray = new float[0];
        return new SimpleModel(
                loader.points, emptyArray, emptyArray, loader.points.length / POINT_SIZE,
                loader.normals, emptyArray, loader.normals.length / NORMAL_SIZE,
                loader.texCoords, emptyArray, emptyArray, loader.texCoords.length / TEX_COORD_SIZE,
                loader.parts, loader.facesCount,
                loader.materials, loader.nodes, loader.globalProperties
        );
    }

    private String[] readProperties() throws IOException {
        int propertiesSize = readUnsignedByte();
        var properties = new String[propertiesSize];

        for (int i = 0; i < propertiesSize; i++)
            properties[i] = input.readUTF();

        return properties;
    }

    private void readPointsKeys() throws IOException {
        pointsKeys = readFloatArrayWithSize(1);
    }

    private void readPointsIndices() throws IOException {
        pointsIndices = readFloatArray(pointsKeys.length * POINT_SIZE);
    }

    private void readNormalsIndices() throws IOException {
        normalsIndices = readFloatArrayWithSize(NORMAL_SIZE);
    }

    private void readTexCoordsKeys() throws IOException {
        texCoordsKeys = readFloatArrayWithSize(1);
    }

    private void readTexCoordsIndices() throws IOException {
        texCoordsIndices = readFloatArray(texCoordsKeys.length * TEX_COORD_SIZE);
    }

    private void readPoints() throws IOException {
        int pointsSize = readUnsignedShort() * POINT_SIZE;
        points = new float[pointsSize];

        for (int i = 0; i < pointsSize; i++) {
            var value = input.readFloat();

            if (Float.isNaN(value)) {
                if (i < POINT_SIZE) throw new IllegalStateException(
                        "Unexpected Float.NaN before " + POINT_SIZE + " values were read"
                );

                System.arraycopy(
                        points, i - POINT_SIZE,
                        points, i,
                        POINT_SIZE);

                i += POINT_SIZE - 1;
            } else {
                int pointsIndex = Arrays.binarySearch(pointsKeys, value);
                if (pointsIndex >= 0) {
                    System.arraycopy(
                            pointsIndices, pointsIndex * POINT_SIZE,
                            points, i,
                            POINT_SIZE);
                    i += POINT_SIZE - 1;
                } else points[i] = value;
            }
        }
    }

    private void readNormals() throws IOException {
        int normalsSize = readUnsignedShort() * NORMAL_SIZE;
        normals = new float[normalsSize];

        for (int i = 0; i < normalsSize; i++) {
            var value = input.readFloat();

            if (abs(value) <= 1.0f) normals[i] = value;
            else if (value <= -2.0f) {
                int normalsIndex = ((int) -value) - 2;
                System.arraycopy(
                        normalsIndices, normalsIndex * NORMAL_SIZE,
                        normals, i,
                        NORMAL_SIZE);
                i += NORMAL_SIZE - 1;
            } else {
                if (i < NORMAL_SIZE) throw new IllegalStateException(
                        "Unexpected value >1 before " + NORMAL_SIZE + " values were read");

                int normalsToCopy = Float.isNaN(value) ? 1 : (int) value;
                for (int j = 0; j < normalsToCopy; j++)
                    System.arraycopy(
                            normals, i - NORMAL_SIZE,
                            normals, i + j * NORMAL_SIZE,
                            NORMAL_SIZE);

                i += normalsToCopy * NORMAL_SIZE - 1;
            }
        }
    }

    private void readTexCoords() throws IOException {
        int texCoordsSize = readUnsignedShort() * TEX_COORD_SIZE;
        texCoords = new float[texCoordsSize];

        for (int i = 0; i < texCoordsSize; i++) {
            var value = input.readFloat();

            if (Float.isNaN(value)) {
                if (i < TEX_COORD_SIZE) throw new IllegalStateException(
                        "Unexpected Float.NaN before " + TEX_COORD_SIZE + " values were read"
                );

                System.arraycopy(
                        texCoords, i - TEX_COORD_SIZE,
                        texCoords, i,
                        TEX_COORD_SIZE);

                i += TEX_COORD_SIZE - 1;
            } else {
                int texCoordsIndex = Arrays.binarySearch(texCoordsKeys, value);
                if (texCoordsIndex >= 0) {
                    System.arraycopy(
                            texCoordsIndices, texCoordsIndex * TEX_COORD_SIZE,
                            texCoords, i,
                            TEX_COORD_SIZE);
                    i += TEX_COORD_SIZE - 1;
                } else texCoords[i] = value;
            }
        }
    }

    private void readFaces() throws IOException {
        int facesSize = readUnsignedByte();
        parts = new short[facesSize][];

        for (int i = 0; i < facesSize; i++)
            parts[i] = readIndices();
    }

    private short[] readIndices() throws IOException {
        int indicesSize = readUnsignedShort() * FACE_SIZE;
        var indices = new short[indicesSize];

        short previousValue = -1;

        for (int i = 0; i < indicesSize; i++) {
            short index = input.readShort();

            // index may contain negative number due to integer overflow
            boolean isOverflowed = abs(index) + i > indicesSize;
            if (index < 0 && !isOverflowed) {
                int valuesToCopy = abs(index);

                for (int j = 0; j < valuesToCopy; j++)
                    indices[i++] = ++previousValue;
                i--;
            } else {
                indices[i] = index;
                previousValue = index;
            }
        }

        return indices;
    }

    private void readMaterials() throws IOException {
        int materialsSize = readUnsignedByte();
        materials = new SimpleMaterial[materialsSize];

        for (int i = 0; i < materialsSize; i++) {
            var ambient = readColor();
            var diffuse = readColor();
            var specular = readColor();

            int opacity = readUnsignedByte();
            float shininess = input.readFloat();

            String diffuseTexture = input.readUTF();
            if (diffuseTexture.isBlank()) diffuseTexture = "";
            String specularTexture = input.readUTF();
            if (specularTexture.isBlank()) specularTexture = "";

            materials[i] = new SimpleMaterial(ambient, diffuse, specular,
                    opacity, shininess, diffuseTexture, specularTexture);
        }
    }

    private int[] readColor() throws IOException {
        return new int[] {
                readUnsignedByte(),
                readUnsignedByte(),
                readUnsignedByte()
        };
    }

    private int readUnsignedByte() throws IOException {
        return Byte.toUnsignedInt(input.readByte());
    }

    private int readUnsignedShort() throws IOException {
        return Short.toUnsignedInt(input.readShort());
    }

    private void readNodes() throws IOException {
        int nodesSize = readUnsignedByte();
        nodes = new SimpleNode[nodesSize];

        for (int i = 0; i < nodesSize; i++)
            nodes[i] = readNode();
    }

    private SimpleNode readNode() throws IOException {
        String id = input.readUTF();

        var translation = readVector();
        var rotation = readQuaternion();
        var scale = readVector();

        var properties = readProperties();

        var nodeParts = readNodeParts();

        int childrenLength = readUnsignedByte();
        var children = new SimpleNode[childrenLength];
        for (int i = 0; i < childrenLength; i++)
            children[i] = readNode();

        return new SimpleNode(id, translation, rotation, scale, nodeParts, children, properties);
    }

    private float[] readVector() throws IOException {
        return new float[] {
                input.readFloat(),
                input.readFloat(),
                input.readFloat()
        };
    }

    private float[] readQuaternion() throws IOException {
        return new float[] {
                input.readFloat(),
                input.readFloat(),
                input.readFloat(),
                input.readFloat()
        };
    }

    private SimpleNodePart[] readNodeParts() throws IOException {
        int nodePartsSize = readUnsignedByte();
        var nodeParts = new SimpleNodePart[nodePartsSize];

        for (int i = 0; i < nodePartsSize; i++)
            nodeParts[i] = new SimpleNodePart(
                    readUnsignedByte(),
                    readUnsignedByte()
            );

        return nodeParts;
    }

    private float[] readFloatArrayWithSize(int multiplier) throws IOException {
        int size = readUnsignedShort() * multiplier;
        return readFloatArray(size);
    }

    private float[] readFloatArray(int size) throws IOException {
        float[] elements = new float[size];

        for (int i = 0; i < size; i++)
            elements[i] = input.readFloat();

        return elements;
    }

}
