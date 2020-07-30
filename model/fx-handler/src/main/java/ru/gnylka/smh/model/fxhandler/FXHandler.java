package ru.gnylka.smh.model.fxhandler;

import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Mesh;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.shape.VertexFormat;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import org.joml.AxisAngle4d;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import ru.gnylka.smh.model.data.SimpleMaterial;
import ru.gnylka.smh.model.data.SimpleModel;
import ru.gnylka.smh.model.data.SimpleNode;

import java.util.ArrayList;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public final class FXHandler {

    private final SimpleModel model;
    private final Function<? super String, ? extends Image> imageLoader;
    private final boolean saveProperties;

    private final ArrayList<Mesh> parts = new ArrayList<>();
    private final ArrayList<Material> materials = new ArrayList<>();
    private final ArrayList<AmbientLight> lights = new ArrayList<>();

    private FXHandler(SimpleModel model,
                      Function<? super String, ? extends Image> imageLoader,
                      boolean saveProperties) {
        this.model = model;
        this.imageLoader = imageLoader;
        this.saveProperties = saveProperties;
    }

    /**
     * Creates nodes hierarchy forming the model using information from {@link SimpleModel} object
     *
     * @param model model object
     * @param imageLoader a function used to load textures by passing texture name to it<br>
     *                    If null textures will be ignored
     *
     * @return {@link Group} containing all root nodes
     */
    public static Group load(SimpleModel model,
                             Function<? super String, ? extends Image> imageLoader) {
        return load(model, imageLoader, false);
    }

    /**
     * Creates nodes hierarchy forming the model using information from {@link SimpleModel} object
     *
     * @param model model object
     * @param imageLoader a function used to load textures by passing texture name to it<br>
     *                    If null textures will be ignored
     * @param saveProperties if model or any node contains properties,
     *                       they will be stored as array of strings in {@link Node#setUserData}
     *
     * @return {@link Group} containing all root nodes
     */
    public static Group load(SimpleModel model,
                             Function<? super String, ? extends Image> imageLoader,
                             boolean saveProperties) {
        requireNonNull(model);

        return new FXHandler(model, imageLoader, saveProperties).createModel();
    }

    private Group createModel() {
        for (var part : model.parts)
            createPart(part);

        for (var material : model.materials)
            createMaterial(material);

        var modelGroup = new Group();

        for (var node : model.nodes)
            modelGroup.getChildren().add(createNode(node));

        if (saveProperties)
            modelGroup.setUserData(model.globalProperties);

        return modelGroup;
    }

    private void createPart(short[] part) {
        boolean noNormals = model.normalsCount == 0;
        boolean noTex = Short.toUnsignedInt(part[0]) >= model.texCoordsCount;
        int partSize = part.length;

        var mesh = new TriangleMesh(noNormals ?
                VertexFormat.POINT_TEXCOORD :
                VertexFormat.POINT_NORMAL_TEXCOORD);

        var meshPoints = mesh.getPoints();
        int pointSize = mesh.getPointElementSize();
        meshPoints.ensureCapacity(partSize * pointSize);

        var meshNormals = mesh.getNormals();
        int normalSize = mesh.getNormalElementSize();
        meshNormals.ensureCapacity(partSize * normalSize);

        var meshTex = mesh.getTexCoords();
        int texCoordSize = mesh.getTexCoordElementSize();
        meshTex.ensureCapacity(partSize * texCoordSize);

        var points = model.points;
        var normals = model.normals;
        var texCoords = model.texCoords;

        var meshFaces = mesh.getFaces();
        meshFaces.ensureCapacity(partSize * (noNormals ? 2 : 3));

        if (noTex) meshTex.addAll(0, 0);

        int lastFaceIndex = 0;
        var holder = new int[1];    // to avoid allocation of arrays on varargs
        for (var index : part) {
            // due to integer overflow, a negative number may be presented in index
            int unsignedIndex = Short.toUnsignedInt(index);

            meshPoints.addAll(points, unsignedIndex * pointSize, pointSize);
            holder[0] = lastFaceIndex;
            meshFaces.addAll(holder);

            if (!noNormals) {
                meshNormals.addAll(normals, unsignedIndex * normalSize, normalSize);
                holder[0] = lastFaceIndex;
                meshFaces.addAll(holder);
            }

            if (noTex) holder[0] = 0;
            else {
                meshTex.addAll(texCoords, unsignedIndex * texCoordSize, texCoordSize);
                holder[0] = lastFaceIndex;
            }
            meshFaces.addAll(holder);

            lastFaceIndex++;
        }
        parts.add(mesh);
    }

    private void createMaterial(SimpleMaterial material) {
        Color ambient = createColor(material.ambient, material.opacity),
                diffuse = createColor(material.diffuse, material.opacity),
                specular = createColor(material.specular, material.opacity);

        var light = new AmbientLight(ambient);

        var phong = new PhongMaterial(diffuse);
        phong.setSpecularColor(specular);
        phong.setSpecularPower(material.shininess);

        if (!material.diffuseTexture.isEmpty())
            if (imageLoader != null)
                phong.setDiffuseMap(imageLoader.apply(material.diffuseTexture));

        if (!material.specularTexture.isEmpty())
            if (imageLoader != null)
                phong.setSpecularMap(imageLoader.apply(material.specularTexture));

        materials.add(phong);
        lights.add(light);
    }

    private Group createNode(SimpleNode node) {
        var nodeGroup = new Group();
        nodeGroup.setId(node.id);

        var transforms = nodeGroup.getTransforms();
        var translation = createVector(node.translation);
        var rotation = createQuaternion(node.rotation);
        var scale = createVector(node.scale);

        var axisAngle = rotation.get(new AxisAngle4d());
        transforms.add(new Translate(translation.x, translation.y, translation.z));
        transforms.add(new Rotate(
                Math.toDegrees(axisAngle.angle),
                new Point3D(axisAngle.x, axisAngle.y, axisAngle.z)
        ));
        transforms.add(new Scale(scale.x, scale.y, scale.z));

        var nodePartsGroup = createNodeParts(node);
        nodeGroup.getChildren().add(nodePartsGroup);

        var childrenGroup = new Group();
        for (var child : node.children)
            childrenGroup.getChildren().add(createNode(child));
        nodeGroup.getChildren().add(childrenGroup);

        if (saveProperties)
            nodeGroup.setUserData(node.properties);

        return nodeGroup;
    }

    private Group createNodeParts(SimpleNode node) {
        var nodePartsGroup = new Group();
        var nodeParts = nodePartsGroup.getChildren();
        for (var nodePart : node.nodeParts) {
            var meshView = new MeshView(parts.get(nodePart.partIndex));
            meshView.setMaterial(materials.get(nodePart.materialIndex));

            var light = lights.get(nodePart.materialIndex);
            light.getScope().add(meshView);

            nodeParts.add(meshView);
            nodeParts.add(light);
        }

        return nodePartsGroup;
    }

    private Color createColor(int[] colorArray, int opacity) {
        return Color.rgb(
                colorArray[0],
                colorArray[1],
                colorArray[2],
                opacity / 255f
        );
    }

    private Vector3d createVector(float[] vectorArray) {
        return new Vector3d(
                vectorArray[0],
                vectorArray[1],
                vectorArray[2]
        );
    }

    private Quaterniond createQuaternion(float[] quaternionArray) {
        return new Quaterniond(
                quaternionArray[0],
                quaternionArray[1],
                quaternionArray[2],
                quaternionArray[3]
        );
    }

}
