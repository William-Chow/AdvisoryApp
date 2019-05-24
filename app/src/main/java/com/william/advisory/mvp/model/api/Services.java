package com.william.advisory.mvp.model.api;

import com.william.advisory.mvp.model.entity.list.Listing;
import com.william.advisory.mvp.model.entity.UserProfile;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

import static com.william.advisory.mvp.model.api.Api.APP_REST_NAME;
import static me.jessyan.retrofiturlmanager.RetrofitUrlManager.DOMAIN_NAME_HEADER;

/**
 * Created by William Chow on 2019-05-23.
 */
public interface Services {

    @Headers({DOMAIN_NAME_HEADER + APP_REST_NAME})
    @FormUrlEncoded
    @POST("/login")
    Observable<Response<UserProfile>> login(@Field("email") String email, @Field("password") String password);

    @Headers({DOMAIN_NAME_HEADER + APP_REST_NAME})
    @GET("/listing")
    Observable<Response<Listing>> listing(@Query("id") int id, @Query("token") String token);

    @Headers({DOMAIN_NAME_HEADER + APP_REST_NAME})
    @FormUrlEncoded
    @POST("/listing/update")
    Observable<Response<UserProfile>> update(@Field("id") int id, @Field("token") String token, @Field("listing_id") int listingID, @Field("listing_name") String listingName, @Field("distance") Double distance);
}
