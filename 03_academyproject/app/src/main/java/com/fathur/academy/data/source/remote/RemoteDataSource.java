package com.fathur.academy.data.source.remote;

import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fathur.academy.data.source.remote.response.ContentResponse;
import com.fathur.academy.data.source.remote.response.CourseResponse;
import com.fathur.academy.data.source.remote.response.ModuleResponse;
import com.fathur.academy.utils.EspressoIdlingResource;
import com.fathur.academy.utils.JsonHelper;

import java.util.List;

public class RemoteDataSource {
    private static RemoteDataSource INSTANCE;
    private final long SERVICE_LATENCY_IN_MILLIS = 1000;
    private JsonHelper jsonHelper;
    private Handler handler = new Handler();

    private RemoteDataSource(JsonHelper jsonHelper) {
        this.jsonHelper = jsonHelper;
    }

    public static RemoteDataSource getInstance(JsonHelper helper) {
        if (INSTANCE == null) {
            INSTANCE = new RemoteDataSource(helper);
        }
        return INSTANCE;
    }

    public LiveData<ApiResponse<List<CourseResponse>>> getAllCourses() {
        EspressoIdlingResource.increment();
        MutableLiveData<ApiResponse<List<CourseResponse>>> resultCourse = new MutableLiveData<>();
        handler.postDelayed(() -> {
            resultCourse.setValue(ApiResponse.success(jsonHelper.loadCourses()));
            EspressoIdlingResource.decrement();
        }, SERVICE_LATENCY_IN_MILLIS);
        return resultCourse;
    }

    public LiveData<ApiResponse<List<ModuleResponse>>> getModules(String courseId) {
        EspressoIdlingResource.increment();
        MutableLiveData<ApiResponse<List<ModuleResponse>>> resultModules = new MutableLiveData<>();
        handler.postDelayed(() -> {
            resultModules.setValue(ApiResponse.success(jsonHelper.loadModule(courseId)));
            EspressoIdlingResource.decrement();
        }, SERVICE_LATENCY_IN_MILLIS);
        return resultModules;
    }

    public LiveData<ApiResponse<ContentResponse>> getContent(String moduleId) {
        EspressoIdlingResource.increment();
        MutableLiveData<ApiResponse<ContentResponse>> resultContent = new MutableLiveData<>();
        handler.postDelayed(() -> {
            resultContent.setValue(ApiResponse.success(jsonHelper.loadContent(moduleId)));
            EspressoIdlingResource.decrement();
        }, SERVICE_LATENCY_IN_MILLIS);
        return resultContent;
    }
}
