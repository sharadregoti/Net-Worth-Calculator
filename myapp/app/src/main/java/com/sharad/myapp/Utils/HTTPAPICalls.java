package com.sharad.myapp.Utils;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface HTTPAPICalls {
    @GET("mf/holdings")
    Call<MutualFundSummary> getMutualFundSummary(@Header("Authorization") String authHeader);

    @GET("portfolio/holdings")
    Call<StockSummary> getPortfolioHoldings(@Header("Authorization") String authHeader);

    @GET("user/margins")
    Call<StocksFundsAndMargins> getFundsAndMargins(@Header("Authorization") String authHeader);
    //
    // @GET("comments")
    // Call<List<Comment>> getComments(@Query("postId") int postId);
    //
    // @POST("posts")
    // Call<Post> createPost(@Body Post post);
    //
    // @FormUrlEncoded
    // @POST("posts")
    // Call<Post> createPost(@Field("userId") String userId , @Field("title") String title , @Field("body") String text);
    //
    // @PUT("posts/{id}")
    // Call<Post> putPost(@Path("id") int id , @Body Post post);
    //
    // @PATCH("posts/{id}")
    // Call<Post> patchPost(@Path("id") int id , @Body Post post);
    //
    // @DELETE("posts/{id}")
    // Call<Void> deletePost(@Path("id") int id);
}