package com.fathur.academy.ui.bookmark;

import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.arch.core.executor.TaskExecutor;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.paging.PagedList;

import com.fathur.academy.data.source.AcademyRepository;
import com.fathur.academy.data.source.local.entity.CourseEntity;

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
public class BookmarkViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    private BookmarkViewModel viewModel;
    @Mock
    private AcademyRepository academyRepository;

    @Mock
    private Observer<PagedList<CourseEntity>> observer;

    @Mock
    private PagedList<CourseEntity> pagedList;

    @Before
    public void setUp() {
        viewModel = new BookmarkViewModel(academyRepository);
    }

    @Test
    public void getBookmark() {
        PagedList<CourseEntity> dummyCourses = pagedList;
        when(dummyCourses.size()).thenReturn(5);
        MutableLiveData<PagedList<CourseEntity>> courses = new MutableLiveData<>();
        courses.setValue(dummyCourses);

        when(academyRepository.getBookmarkedCourses()).thenReturn(courses);
        List<CourseEntity> courseEntities = viewModel.getBookmarks().getValue();
        verify(academyRepository).getBookmarkedCourses();
        assertNotNull(courseEntities);
        assertEquals(5, courseEntities.size());

        viewModel.getBookmarks().observeForever(observer);
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