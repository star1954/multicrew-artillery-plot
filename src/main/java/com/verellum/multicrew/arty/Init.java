package com.verellum.multicrew.arty;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Init {

    public static void init(){
        extractResources();
    }
    /**
     * Attempts to create temporary directory
     */
    private static void extractResources(){
        try {
            // TODO remove this SuppressWarnings annotation once tempDir is used
            @SuppressWarnings("unused")
			Path tempDir = Files.createTempDirectory("artyTemp");
        } catch (IOException e) {
            System.err.println("!! Failed to create temporary directory !!");
            e.printStackTrace();
        }
    }
}
