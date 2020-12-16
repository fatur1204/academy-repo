package com.fathur.academy.ui.detail;

import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.arch.core.executor.TaskExecutor;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.fathur.academy.data.source.AcademyRepository;
import com.fathur.academy.data.source.local.entity.CourseEntity;
import com.fathur.academy.data.source.local.entity.CourseWithModule;
import com.fathur.academy.data.source.local.entity.ModuleEntity;
import com.fathur.academy.utils.DataDummy;
import com.fathur.academy.vo.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DetailCourseViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    private DetailCourseViewModel viewModel;
    private CourseEntity dummyCourse = DataDummy.generateDummyCourses().get(0);
    private String courseId = dummyCourse.getCourseId();
    private ArrayList<ModuleEntity> dummyModules = DataDummy.generateDummyModules(courseId);
    @Mock
    private AcademyRepository academyRepository;

    @Mock
    private Observer<CourseEntity> courseObserver;
    @Mock
    private Observer<Resource<CourseWithModule>> observer;

    @Before
    public void setUp() {
        viewModel = new DetailCourseViewModel(academyRepository);
        viewModel.setCourseId(courseId);
    }

    @Test
    public void getCourseWithModule() {
        Resource<CourseWithModule> dummyCourseWithModule = Resource.success(DataDummy.generateDummyCourseWithModules(dummyCourse, true));
        MutableLiveData<Resource<CourseWithModule>> course = new MutableLiveData<>();
        course.setValue(dummyCourseWithModule);
        when(academyRepository.getCourseWithModules(courseId)).thenReturn(course);
        viewModel.courseModule.observeForever(observer);
        verify(observer).onChanged(dummyCourseWithModule);
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