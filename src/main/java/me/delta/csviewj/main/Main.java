package me.delta.csviewj.main;

public class Main {

    //The main class can not extend Application because it leads to an error (https://stackoverflow.com/questions/52653836/maven-shade-javafx-runtime-components-are-missing)
    public static void main(String[] args) {
        CSViewJ.main(args);
    }
}
