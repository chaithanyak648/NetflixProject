package com.example.netflix.Retrofit;

import com.example.netflix.Model.AllCategory;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface Apiinterface {
    @GET("netflix_project.json")
    Observable<List<AllCategory>> getAllCategoryMovies();
}
