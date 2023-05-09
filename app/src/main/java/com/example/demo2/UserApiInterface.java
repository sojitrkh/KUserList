package com.example.demo2;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface UserApiInterface {

    @GET("api/user/getAllUsers")
    Call<ApiResultModel> getAllUsers();

    @POST("api/user/addUser")
    Call<ApiRootModel> addUser(@Body UserModel userModels);

    @POST("api/user/updateUser")
    Call<ApiRootModel> updateUser(@Body UserModel userModels);

    @POST("api/user/deleteUser")
    Call<ApiRootModel> deleteUser(@Body UserModel userModel);
}
