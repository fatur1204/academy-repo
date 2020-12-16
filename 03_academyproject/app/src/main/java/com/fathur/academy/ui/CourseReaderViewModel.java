package com.fathur.academy.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.fathur.academy.data.source.AcademyRepository;
import com.fathur.academy.data.source.local.entity.ModuleEntity;
import com.fathur.academy.vo.Resource;

import java.util.List;

public class CourseReaderViewModel extends ViewModel {
    private static final String TAG = CourseReaderViewModel.class.getSimpleName();
    private MutableLiveData<String> courseId = new MutableLiveData<>();
    private MutableLiveData<String> moduleId = new MutableLiveData<>();
    private AcademyRepository academyRepository;
    public LiveData<Resource<List<ModuleEntity>>> modules = Transformations.switchMap(courseId,
            mCourseId -> academyRepository.getAllModulesByCourse(mCourseId));
    public LiveData<Resource<ModuleEntity>> selectedModule = Transformations.switchMap(moduleId,
            selectedPosition -> academyRepository.getContent(selectedPosition)
    );

    public CourseReaderViewModel(AcademyRepository mAcademyRepository) {
        this.academyRepository = mAcademyRepository;
    }

    public void setCourseId(String courseId) {
        this.courseId.setValue(courseId);
    }

    public void setSelectedModule(String moduleId) {
        this.moduleId.setValue(moduleId);
    }

    public void readContent(ModuleEntity module) {
        academyRepository.setReadModule(module);
    }

    public int getModuleSize() {
        if (modules.getValue() != null) {
            List<ModuleEntity> moduleEntities = modules.getValue().data;
            if (moduleEntities != null) {
                return moduleEntities.size();
            }
        }
        return 0;
    }

    public void setNextPage() {
        if (selectedModule.getValue() != null && modules.getValue() != null) {
            ModuleEntity moduleEntity = selectedModule.getValue().data;
            List<ModuleEntity> moduleEntities = modules.getValue().data;
            if (moduleEntity != null && moduleEntities != null) {
                int position = moduleEntity.getmPosition();
                if (position < moduleEntities.size() && position >= 0) {
                    setSelectedModule(moduleEntities.get(position + 1).getmModuleId());
                }
            }
        }
    }

    public void setPrevPage() {
        if (selectedModule.getValue() != null && modules.getValue() != null) {
            ModuleEntity moduleEntity = selectedModule.getValue().data;
            List<ModuleEntity> moduleEntities = modules.getValue().data;
            if (moduleEntity != null && moduleEntities != null) {
                int position = moduleEntity.getmPosition();
                if (position < moduleEntities.size() && position >= 0) {
                    setSelectedModule(moduleEntities.get(position - 1).getmModuleId());
                }
            }
        }
    }

    /*public void setSelectedCourse(String courseId) {
        this.courseId = courseId;
    }

    /*public void setSelectedModule(String moduleId) {
        this.moduleId = moduleId;
    }

    public LiveData<List<ModuleEntity>> getModules() {
        return academyRepository.getAllModulesByCourse(courseId);
    }

    public LiveData<ModuleEntity> getSelectedModule() {
        return academyRepository.getContent(courseId, moduleId);
    }*/
}
