package ru.gnylka.smh.model.data;

import java.util.Objects;

public final class SimpleNodePart {

    public final int partIndex;
    public final int materialIndex;

    public SimpleNodePart(int partIndex, int materialIndex) {
        this.partIndex = partIndex;
        this.materialIndex = materialIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleNodePart that = (SimpleNodePart) o;
        return partIndex == that.partIndex &&
                materialIndex == that.materialIndex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(partIndex, materialIndex);
    }

    @Override
    public String toString() {
        return "SimpleNodePart{" +
                "partIndex=" + partIndex +
                ", materialIndex=" + materialIndex +
                '}';
    }

}
