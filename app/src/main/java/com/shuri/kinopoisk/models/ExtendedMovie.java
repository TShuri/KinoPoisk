package com.shuri.kinopoisk.models;

public class ExtendedMovie extends Movie{

String slogan;
String description;

    public ExtendedMovie() {
        super();
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
