package com.fathur.academy.ui.academy;

import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.arch.core.executor.TaskExecutor;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.paging.PagedList;

import com.fathur.academy.data.source.AcademyRepository;
import com.fathur.academy.data.source.local.entity.CourseEntity;
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

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AcademyViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    private AcademyViewModel viewModel;
    @Mock
    private AcademyRepository academyRepository;

    @Mock
    private Observer<Resource<PagedList<CourseEntity>>> observer;

    @Mock
    private PagedList<CourseEntity> pagedList;


    @Before
    public void setUp() {
        viewModel = new AcademyViewModel(academyRepository);
    }

    @Test
    public void getCourses() {
        //ArrayList<CourseEntity> dummyCourses = DataDummy.generateDummyCourses();
        Resource<PagedList<CourseEntity>> dummyCourses = Resource.success(pagedList);
        when(dummyCourses.data.size()).thenReturn(5);
        MutableLiveData<Resource<PagedList<CourseEntity>>> courses = new MutableLiveData<>();
        courses.setValue(dummyCourses);

        when(academyRepository.getAllCourses()).thenReturn(courses);
        List<CourseEntity> courseEntities = viewModel.getCourses().getValue().data;
        verify(academyRepository).getAllCourses();
        assertNotNull(courseEntities);
        assertEquals(5, courseEntities.size());

        viewModel.getCourses().observeForever(observer);
        verify(observer).onChanged(dummyCourses);
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