module ru.gnylka.smh.testjfx {

    requires java.logging;

    requires kotlin.stdlib;

    requires info.picocli;

    requires org.joml;

    requires javafx.base;
    requires javafx.graphics;
    requires javafx.controls;

    requires ru.gnylka.smh.model.loader;
    requires ru.gnylka.smh.model.fxhandler;

    opens ru.gnylka.smh.testjfx to javafx.graphics;
    opens ru.gnylka.smh.testjfx.arguments to info.picocli;

}
