package com.example.naveen.myapplication;

/**
 * Created by naveen on 22/4/17.
 */


//pair is class for shoe url and value for each url returned
public class Pair{
    //url is the image of shoe
    String url;
    //dist is the float value for each shoe, minimum the value maximum the mathching with captured image
    Float dist;

    public Pair(String url, Float dist) {
        this.url = url;
        this.dist = dist;
    }
}

