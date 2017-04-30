package com.example.shaleya.myapplication;


//pair is class for shoe url and value for each url returned
public class Pair{
    //url is the image of shoe
    String imageurl;
    String shoplink;
    //dist is the float value for each shoe, minimum the value maximum the mathching with captured image
    Float dist;

    public Pair(String imageurl, String shoplink, Float dist) {
        this.imageurl = imageurl;
        this.shoplink = shoplink;
        this.dist = dist;
    }

    public Pair(String nothing, String shoplink, double v) {
    }
}

