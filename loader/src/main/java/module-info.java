module ru.gnylka.smh.loader {

    requires kotlin.stdlib;

    requires com.google.gson;

    requires ru.gnylka.smh.processing;

    exports ru.gnylka.smh.loader;

    opens ru.gnylka.smh.loader.g3dj to com.google.gson;

    uses ru.gnylka.smh.loader.LoaderProvider;

    provides ru.gnylka.smh.loader.LoaderProvider with
            ru.gnylka.smh.loader.g3dj.G3djLoaderProvider,
            ru.gnylka.smh.loader.fbxconv.FbxConvLoaderProvider;

}
