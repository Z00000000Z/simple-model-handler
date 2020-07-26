module ru.gnylka.smh.model.converter.cli {

    requires java.logging;

    requires kotlin.stdlib;

    requires info.picocli;

    requires ru.gnylka.smh.loader;
    requires ru.gnylka.smh.model.converter;
    requires ru.gnylka.smh.model.data;
    requires ru.gnylka.smh.processing;
    requires ru.gnylka.smh.utils;

    opens ru.gnylka.smh.model.converter.cli.arguments to info.picocli;

}
