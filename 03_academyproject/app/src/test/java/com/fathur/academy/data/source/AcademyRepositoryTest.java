package com.fathur.academy.data.source;

import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.arch.core.executor.TaskExecutor;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.PagedList;

import com.fathur.academy.data.source.local.LocalDataSource;
import com.fathur.academy.data.source.local.entity.CourseEntity;
import com.fathur.academy.data.source.local.entity.CourseWithModule;
import com.fathur.academy.data.source.local.entity.ModuleEntity;
import com.fathur.academy.data.source.remote.RemoteDataSource;
import com.fathur.academy.data.source.remote.response.ContentResponse;
import com.fathur.academy.data.source.remote.response.CourseResponse;
import com.fathur.academy.data.source.remote.response.ModuleResponse;
import com.fathur.academy.utils.AppExecutors;
import com.fathur.academy.utils.DataDummy;
import com.fathur.academy.utils.LiveDataTestUtil;
import com.fathur.academy.utils.PagedListUtil;
import com.fathur.academy.vo.Resource;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AcademyRepositoryTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();


    private RemoteDataSource remote = mock(RemoteDataSource.class);

    private LocalDataSource local = mock(LocalDataSource.class);
    private AppExecutors appExecutors = mock(AppExecutors.class);


    private FakeAcademyRepository academyRepository = new FakeAcademyRepository(remote, local, appExecutors);

    private List<CourseResponse> courseResponses = DataDummy.generateRemoteDummyCourses();
    private String courseId = courseResponses.get(0).getId();
    private List<ModuleResponse> moduleResponses = DataDummy.generateRemoteDummyModules(courseId);
    private String moduleId = moduleResponses.get(0).getModuleId();
    private ContentResponse content = DataDummy.generateRemoteDummyContent(moduleId);

    @Test
    public void getAllCourses() {
        /*MutableLiveData<List<CourseEntity>> dummyCourses = new MutableLiveData<>();
        dummyCourses.setValue(DataDummy.generateDummyCourses());
        when(local.getAllCourses()).thenReturn(dummyCourses);
        Resource<List<CourseEntity>> courseEntities = LiveDataTestUtil.getValue(academyRepository.getAllCourses());*/

        DataSource.Factory<Integer, CourseEntity> dataSourceFactory = mock(DataSource.Factory.class);
        when(local.getAllCourses()).thenReturn(dataSourceFactory);
        academyRepository.getAllCourses();
        Resource<PagedList<CourseEntity>> courseEntities = Resource.success(PagedListUtil.mockPagedList(DataDummy.generateDummyCourses()));

        verify(local).getAllCourses();
        assertNotNull(courseEntities.data);
        assertEquals(courseResponses.size(), courseEntities.data.size());
    }

    @Test
    public void getAllModulesByCourse() {
        MutableLiveData<List<ModuleEntity>> dummyModules = new MutableLiveData<>();
        dummyModules.setValue(DataDummy.generateDummyModules(courseId));
        when(local.getAllModulesByCourse(courseId)).thenReturn(dummyModules);

        Resource<List<ModuleEntity>> courseEntities = LiveDataTestUtil.getValue(academyRepository.getAllModulesByCourse(courseId));
        verify(local).getAllModulesByCourse(courseId);
        assertNotNull(courseEntities.data);
        assertEquals(moduleResponses.size(), courseEntities.data.size());
    }

    @Test
    public void getBookmarkedCourses() {
        DataSource.Factory<Integer, CourseEntity> dataSourceFactory = mock(DataSource.Factory.class);
        when(local.getBookmarkedCourses()).thenReturn(dataSourceFactory);
        academyRepository.getBookmarkedCourses();

        Resource<PagedList<CourseEntity>> courseEntities = Resource.success(PagedListUtil.mockPagedList(DataDummy.generateDummyCourses()));
        verify(local).getBookmarkedCourses();
        assertNotNull(courseEntities);
        assertEquals(courseResponses.size(), courseEntities.data.size());
    }

    @Test
    public void getContent() {
        MutableLiveData<ModuleEntity> dummyEntity = new MutableLiveData<>();
        dummyEntity.setValue(DataDummy.generateDummyModuleWithContent(moduleId));
        when(local.getModuleWithContent(courseId)).thenReturn(dummyEntity);

        Resource<ModuleEntity> courseEntitiesContent = LiveDataTestUtil.getValue(academyRepository.getContent(courseId));
        verify(local).getModuleWithContent(courseId);
        assertNotNull(courseEntitiesContent);
        assertNotNull(courseEntitiesContent.data.contentEntity);
        assertNotNull(courseEntitiesContent.data.contentEntity.getmContent());
        assertEquals(content.getContent(), courseEntitiesContent.data.contentEntity.getmContent());
    }

    @Test
    public void getCourseWithModules() {
        MutableLiveData<CourseWithModule> dummyEntity = new MutableLiveData<>();
        dummyEntity.setValue(DataDummy.generateDummyCourseWithModules(DataDummy.generateDummyCourses().get(0), false));
        when(local.getCourseWithModules(courseId)).thenReturn(dummyEntity);

        Resource<CourseWithModule> courseEntities = LiveDataTestUtil.getValue(academyRepository.getCourseWithModules(courseId));
        verify(local).getCourseWithModules(courseId);
        assertNotNull(courseEntities.data);
        assertNotNull(courseEntities.data.mCourse.getTitle());
        assertEquals(courseResponses.get(0).getTitle(), courseEntities.data.mCourse.getTitle());
    }

    public class InstantTaskExecutorRule extends TestWatcher {
        @Override
        protected void starting(Description description) {
            super.starting(description);
            ArchTaskExecutor.getInstance().setDelegate(new TaskExecutor() {
                @Override
                public void executeOnDiskIO(Runnable runnable) {
                    runnable.run();
                }

                @Override
                public void postToMainThread(Runnable runnable) {
                    runnable.run();
                }

                @Override
                public boolean isMainThread() {
                    return true;
                }
            });
        }

        @Override
        protected void finished(Description description) {
            super.finished(description);
            ArchTaskExecutor.getInstance().setDelegate(null);
        }
    }
}