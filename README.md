# simple-model-handler
A fbx-conv-based tool to handle models

This tool allows you to convert models from .obj, .dae, .fbx, .g3dj formats into .smhmb and .smhmt formats

# Running

First of all, make sure there is at least Java 11 installed in your system by running the following:
> java --version

### Running smh-model
> ./gradlew :model:cli:run --args="/path/to/model.fbx"

### Running smh-model-viewer
> ./gradlew :test-apps:test-jfx:run --args="/path/to/model.smhmb"

Note that this tool uses [fbx-conv](https://github.com/libgdx/fbx-conv) to convert .obj, .dae and .fbx files, which means you need to have fbx-conv installed in your system:
  1. Download fbx-conv here: https://libgdx.badlogicgames.com/fbx-conv/fbx-conv.zip
  2. Rename from fbx-conv-lin64 or fbx-conv-win32.exe to fbx-conv or fbx-conv.exe
  3. On Linux: move fbx-conv to /usr/bin/; move libfbxsdk.so to /usr/lib/
  4. On Windows: add fbx-conv.exe to path

When downloading from releases, fbx-conv is automatically bundled with the program

# Building

> ./gradlew build

# Using in your application

After building, you can use the following jar files in your application:
  - model/data/build/libs/*.jar
  - model/loader/build/libs/*.jar
  - model/fx-handler/build/libs/*.jar

A brief description of jar files:
  - data contains classes that represent the model
  - loader loads model from specified InputStream as objects from data
  - fx-handler converts these objects into javafx-specific nodes suitable for adding to the scene

You may also include other jars if you find them useful

# Common CLI arguments

### smh-model

* **--type**              When set to text, data will be written to .smhmt file (which is suitable for debugging), otherwise to .smhmb
* **--list-loaders**      List all available loaders
* **--list-plugins**      List all available plugins and their usage
* **--compress**          Use gzip compression
* **--optimize**          Apply different optimizations methods to the model
* **--parallel**          Convert models using multiple threads
* **--use-plugins**       Specify a comma-separated list of plugins to use
* **--plugin**            Specify a *key*=*value* pair, where *key* is plugin name and *value* is space-separated plugin arguments

### smh-model-viewer

* **--compress**          Specify if model was converted with --compress flag
* **--background**        Specify custom background web color
* **--light**             Add light with specified web color to the scene. Light will be bound to camera position
* **--planes**            Create planes for ease of orientation
* **--face-culling**      Change model face culling to front, back or none
* **--info**              Display helpful information about the model

### Example

> smh-model /path/to/file.fbx --type binary --compress --use-plugins normalization,reset-color --plugin reset-color="--ambient 1 1 1" --optimize

> smh-model-viewer /path/to/file.smhmb --light white --background #FAE7B5 --compress --planes 30 --face-culling none --scale 2 --info

Unknown issues:
--info flag creates weird white canvas in scene
