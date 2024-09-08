package com.example.givemeacat;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitInterface {

    @GET("https://cataas.com/cat{tag}")
    Call<ImageData> getACat(@Path("tag") String tag,
                                 @Query("json") String json);
}
