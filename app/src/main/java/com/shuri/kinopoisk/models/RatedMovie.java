package com.shuri.kinopoisk.models;

public class RatedMovie extends Movie{
    double myRating;
    String myReview;

    RatedMovie() {
        super();
    }

    public double getMyRating() {
        return myRating;
    }

    public void setMyRating(double myRating) {
        this.myRating = myRating;
    }

    public String getMyReview() {
        return myReview;
    }

    public void setMyReview(String myReview) {
        this.myReview = myReview;
    }
}
