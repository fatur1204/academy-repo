package com.fathur.academy.data.source;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.fathur.academy.data.source.local.LocalDataSource;
import com.fathur.academy.data.source.local.entity.CourseEntity;
import com.fathur.academy.data.source.local.entity.CourseWithModule;
import com.fathur.academy.data.source.local.entity.ModuleEntity;
import com.fathur.academy.data.source.remote.ApiResponse;
import com.fathur.academy.data.source.remote.RemoteDataSource;
import com.fathur.academy.data.source.remote.response.ContentResponse;
import com.fathur.academy.data.source.remote.response.CourseResponse;
import com.fathur.academy.data.source.remote.response.ModuleResponse;
import com.fathur.academy.utils.AppExecutors;
import com.fathur.academy.vo.Resource;

import java.util.ArrayList;
import java.util.List;

public class FakeAcademyRepository implements AcademyDataSource {
    private static final String TAG = AcademyRepository.class.getSimpleName();
    private final RemoteDataSource remoteDataSource;
    private final LocalDataSource localDataSource;
    private final AppExecutors appExecutors;

    public FakeAcademyRepository(@NonNull RemoteDataSource remoteDataSource, @NonNull LocalDataSource localDataSource, AppExecutors appExecutors) {
        this.remoteDataSource = remoteDataSource;
        this.localDataSource = localDataSource;
        this.appExecutors = appExecutors;
    }

    @Override
    public LiveData<Resource<PagedList<CourseEntity>>> getAllCourses() {
        return new NetworkBoundResource<PagedList<CourseEntity>, List<CourseResponse>>(appExecutors) {
            @Override
            public LiveData<PagedList<CourseEntity>> loadFromDB() {
                PagedList.Config config = new PagedList.Config.Builder()
                        .setEnablePlaceholders(false)
                        .setInitialLoadSizeHint(4)
                        .setPageSize(4)
                        .build();
                return new LivePagedListBuilder<>(localDataSource.getAllCourses(), config).build();
            }

            @Override
            public Boolean shouldFetch(PagedList<CourseEntity> data) {
                return (data == null) || (data.size() == 0);
            }

            @Override
            public LiveData<ApiResponse<List<CourseResponse>>> createCall() {
                return remoteDataSource.getAllCourses();
            }

            @Override
            public void saveCallResult(List<CourseResponse> courseResponses) {
                ArrayList<CourseEntity> courseList = new ArrayList<>();
                for (int i = 0; i < courseResponses.size(); i++) {
                    CourseResponse response = courseResponses.get(i);
                    CourseEntity course = new CourseEntity(response.getId(),
                            response.getTitle(),
                            response.getDescription(),
                            response.getDate(),
                            false,
                            response.getImagePath());
                    courseList.add(course);
                }
                localDataSource.insertCourses(courseList);
            }
        }.asLiveData();
    }

    @Override
    public LiveData<Resource<CourseWithModule>> getCourseWithModules(final String courseId) {
        return new NetworkBoundResource<CourseWithModule, List<ModuleResponse>>(appExecutors) {
            @Override
            protected LiveData<CourseWithModule> loadFromDB() {
                return localDataSource.getCourseWithModules(courseId);
            }

            @Override
            protected Boolean shouldFetch(CourseWithModule courseWithModule) {
                return (courseWithModule == null || courseWithModule.mModules == null) || (courseWithModule.mModules.size() == 0);
            }

            @Override
            protected LiveData<ApiResponse<List<ModuleResponse>>> createCall() {
                return remoteDataSource.getModules(courseId);
            }

            @Override
            protected void saveCallResult(List<ModuleResponse> moduleResponses) {
                ArrayList<ModuleEntity> moduleList = new ArrayList<>();
                for (ModuleResponse response : moduleResponses) {
                    ModuleEntity course = new ModuleEntity(response.getModuleId(),
                            response.getCourseId(),
                            response.getTitle(),
                            response.getPosition(),
                            false);
                    moduleList.add(course);
                }

                localDataSource.insertModules(moduleList);
            }
        }.asLiveData();
    }

    @Override
    public LiveData<Resource<List<ModuleEntity>>> getAllModulesByCourse(String courseId) {
        return new NetworkBoundResource<List<ModuleEntity>, List<ModuleResponse>>(appExecutors) {
            @Override
            protected LiveData<List<ModuleEntity>> loadFromDB() {
                return localDataSource.getAllModulesByCourse(courseId);
            }

            @Override
            protected Boolean shouldFetch(List<ModuleEntity> modules) {
                return (modules == null) || (modules.size() == 0);
            }

            @Override
            protected LiveData<ApiResponse<List<ModuleResponse>>> createCall() {
                return remoteDataSource.getModules(courseId);
            }

            @Override
            protected void saveCallResult(List<ModuleResponse> moduleResponses) {
                ArrayList<ModuleEntity> moduleList = new ArrayList<>();
                for (ModuleResponse response : moduleResponses) {
                    ModuleEntity course = new ModuleEntity(response.getModuleId(),
                            response.getCourseId(),
                            response.getTitle(),
                            response.getPosition(),
                            false);

                    moduleList.add(course);
                }

                localDataSource.insertModules(moduleList);
            }
        }.asLiveData();
    }

    @Override
    public LiveData<PagedList<CourseEntity>> getBookmarkedCourses() {
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(4)
                .setPageSize(4)
                .build();
        return new LivePagedListBuilder<>(localDataSource.getBookmarkedCourses(), config).build();
    }

    @Override
    public LiveData<Resource<ModuleEntity>> getContent(String moduleId) {
        return new NetworkBoundResource<ModuleEntity, ContentResponse>(appExecutors) {
            @Override
            protected LiveData<ModuleEntity> loadFromDB() {
                return localDataSource.getModuleWithContent(moduleId);
            }

            @Override
            protected Boolean shouldFetch(ModuleEntity moduleEntity) {
                return (moduleEntity.contentEntity == null);
            }

            @Override
            protected LiveData<ApiResponse<ContentResponse>> createCall() {
                return remoteDataSource.getContent(moduleId);
            }

            @Override
            protected void saveCallResult(ContentResponse contentResponse) {
                localDataSource.updateContent(contentResponse.getContent(), moduleId);
            }
        }.asLiveData();
    }

    @Override
    public void setCourseBookmark(CourseEntity course, boolean state) {
        appExecutors.diskIO().execute(() -> localDataSource.setCourseBookmark(course, state));
    }

    @Override
    public void setReadModule(ModuleEntity module) {
        appExecutors.diskIO().execute(() -> localDataSource.setReadModule(module));
    }
}
