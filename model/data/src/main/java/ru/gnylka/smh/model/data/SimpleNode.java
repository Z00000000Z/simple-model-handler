package ru.gnylka.smh.model.data;

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

}
