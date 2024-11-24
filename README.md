# race-data-generator
## To set up your environment:

1) Open Eclipse and go to `help > Eclipse Marketplace...` and then search `javafx` and install `e(fx)clipse 3.0.0`

2) Clone this repository using Eclipse (or just use Github Desktop/Git CLI and then import the project into Eclipse)

3) Install [JavaFX Scene Builder](https://www.oracle.com/technetwork/java/javase/downloads/javafxscenebuilder-1x-archive-2199384.html) (I'm using version 1.1) to create and modify `.fxml` files.

4) In Eclipse go to `Window > Preferences` then click on JavaFX and set the SceneBuilder executable to the downloaded Scenebuilder executable. I used the default install location and my path is `C:\Program Files (x86)\Oracle\JavaFX Scene Builder 1.1\JavaFX Scene Builder 1.1.exe`.

5) Now right click on `.fxml` files and click on `Open with SceneBuilder` to edit them, and you should be able to just run `Main.java`.

Let me know if it doesn't work.

## To set up headless nix environment with fluxbox

I did this as an experiment to see how easy it would be using a Nix dev container configuration I wrote to get a old java javafx app running. I remember java paths being a pain especially getting scenebuilder to work, now this is a completely reproducible environment.

1. Use vscode
1. Download vs code dev containers extension
1. Reopen in container (dialog will pop up or `ctrl+shift+p` reopen in container)
1. wait a few minutes (on low power machines or slow internet connections it may take 10 or longer)
1. open fluxbox desktop environment by navigating to `localhost:6080`
1. run `mvn clean install && mvn exec:java`
1. the race generator will show up in the fluxbox window


1. to edit the scene using scenebuilder run scenebuilder ./src/main/java/view/Controller.fxml
