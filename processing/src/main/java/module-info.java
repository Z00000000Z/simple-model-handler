module ru.gnylka.smh.processing {

    requires kotlin.stdlib;

    requires info.picocli;

    requires org.joml;

    requires ru.gnylka.smh.utils;

    exports ru.gnylka.smh.processing;
    exports ru.gnylka.smh.processing.data;
    exports ru.gnylka.smh.processing.input;

    opens ru.gnylka.smh.processing.normalization to info.picocli;
    opens ru.gnylka.smh.processing.resetcolor to info.picocli;

    uses ru.gnylka.smh.processing.ProcessingPluginProvider;

    provides ru.gnylka.smh.processing.ProcessingPluginProvider with
            ru.gnylka.smh.processing.normalization.NormalizationPluginProvider,
            ru.gnylka.smh.processing.resetcolor.ResetColorPluginProvider;

}
