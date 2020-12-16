package com.fathur.academy.ui;

import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.arch.core.executor.TaskExecutor;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.fathur.academy.data.source.AcademyRepository;
import com.fathur.academy.data.source.local.entity.ContentEntity;
import com.fathur.academy.data.source.local.entity.CourseEntity;
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
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CourseReaderViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    private CourseReaderViewModel viewModel;
    private CourseEntity dummyCourse = DataDummy.generateDummyCourses().get(0);
    private String courseId = dummyCourse.getCourseId();
    private ArrayList<ModuleEntity> dummyModules = DataDummy.generateDummyModules(courseId);
    private String moduleId = dummyModules.get(0).getmModuleId();
    @Mock
    private AcademyRepository academyRepository;

    @Mock
    private Observer<Resource<List<ModuleEntity>>> modulesObserver;

    @Mock
    private Observer<Resource<ModuleEntity>> moduleObserver;

    @Before
    public void setUp() {
        viewModel = new CourseReaderViewModel(academyRepository);
        viewModel.setCourseId(courseId);
        viewModel.setSelectedModule(moduleId);

        ModuleEntity dummyModule = dummyModules.get(0);
        dummyModule.contentEntity = new ContentEntity("<h3 class=\\\"fr-text-bordered\\\">" + dummyModule.getmTitle() + "</h3><p>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p>");
    }

    @Test
    public void getModules() {
        MutableLiveData<Resource<List<ModuleEntity>>> modules = new MutableLiveData<>();
        Resource<List<ModuleEntity>> resource = Resource.success(dummyModules);
        modules.setValue(resource);
        when(academyRepository.getAllModulesByCourse(courseId)).thenReturn(modules);

        viewModel.modules.observeForever(modulesObserver);
        verify(modulesObserver).onChanged(resource);
    }

    @Test
    public void getSelectedModule() {
        MutableLiveData<Resource<ModuleEntity>> module = new MutableLiveData<>();
        Resource<ModuleEntity> resource = Resource.success(dummyModules.get(0));
        module.setValue(resource);
        when(academyRepository.getContent(moduleId)).thenReturn(module);

        viewModel.selectedModule.observeForever(moduleObserver);
        verify(moduleObserver).onChanged(resource);
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