package com.verellum.multicrew.arty;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;

/**
 * WORKAROUND FOR JAVAFX ISSUES
 * <p> please dont remove
 */
public class Main {
    
    public static void main(String[] args) throws FileNotFoundException, URISyntaxException, InterruptedException {
        Init.init();
        App.app(args);
    }

}
