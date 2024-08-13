package com.verellum.multicrew.arty;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Scanner;

import nu.pattern.OpenCV;

/**
 * Hello world!
 *
 */
public class App 
{

    public static void main( String[] args ) throws URISyntaxException, FileNotFoundException
    {
        OpenCV.loadLocally();
        CV_Interface cvo = new CV_Interface();
        File file = new File(App.class.getResource("maps/test.txt").toURI());
        Scanner sc = new Scanner(file);
        
        System.out.println(sc.nextLine());
        sc.close();
    }
    
    
}
