package ru.gnylka.smh.model.data;

import java.util.Arrays;
import java.util.Objects;

// no values accept null
public final class SimpleNode {

    public final String id;
    public final float[] translation;
    public final float[] rotation;
    public final float[] scale;
    public final SimpleNodePart[] nodeParts;
    public final SimpleNode[] children;
    public final String[] properties;

    public SimpleNode(String id,
                      float[] translation,
                      float[] rotation,
                      float[] scale,
                      SimpleNodePart[] nodeParts,
                      SimpleNode[] children,
                      String[] properties) {
        this.id = id;
        this.translation = translation;
        this.rotation = rotation;
        this.scale = scale;
        this.nodeParts = nodeParts;
        this.children = children;
        this.properties = properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleNode that = (SimpleNode) o;
        return id.equals(that.id) &&
                Arrays.equals(translation, that.translation) &&
                Arrays.equals(rotation, that.rotation) &&
                Arrays.equals(scale, that.scale) &&
                Arrays.equals(nodeParts, that.nodeParts) &&
                Arrays.equals(children, that.children) &&
                Arrays.equals(properties, that.properties);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id);
        result = 31 * result + Arrays.hashCode(translation);
        result = 31 * result + Arrays.hashCode(rotation);
        result = 31 * result + Arrays.hashCode(scale);
        result = 31 * result + Arrays.hashCode(nodeParts);
        result = 31 * result + Arrays.hashCode(children);
        result = 31 * result + Arrays.hashCode(properties);
        return result;
    }

    @Override
    public String toString() {
        return "SimpleNode{" +
                "id='" + id + '\'' +
                ", translation=" + Arrays.toString(translation) +
                ", rotation=" + Arrays.toString(rotation) +
                ", scale=" + Arrays.toString(scale) +
                ", nodeParts=" + Arrays.toString(nodeParts) +
                ", children=" + Arrays.toString(children) +
                ", properties=" + Arrays.toString(properties) +
                '}';
    }

}
