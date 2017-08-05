package fs.explorer;

import fs.explorer.views.Application;

import java.io.IOException;

public class Main {
    // TODO better to catch this and report error user friendly way
    public static void main(String[] args) throws IOException {
        Application app = new Application();
        app.run();
    }
}
