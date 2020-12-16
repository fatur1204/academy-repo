package com.fathur.academy.ui.detail;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.fathur.academy.data.source.AcademyRepository;
import com.fathur.academy.data.source.local.entity.CourseEntity;
import com.fathur.academy.data.source.local.entity.CourseWithModule;
import com.fathur.academy.vo.Resource;

public class DetailCourseViewModel extends ViewModel {
    private static final String TAG = DetailCourseViewModel.class.getSimpleName();
    private MutableLiveData<String> courseId = new MutableLiveData<>();
    private AcademyRepository academyRepository;
    public LiveData<Resource<CourseWithModule>> courseModule = Transformations.switchMap(courseId,
            mCourseId -> academyRepository.getCourseWithModules(mCourseId));

    public DetailCourseViewModel(AcademyRepository mAcademyRepository) {
        this.academyRepository = mAcademyRepository;
    }

    public String getCourseId() {
        return courseId.getValue();
    }

    public void setCourseId(String courseId) {
        this.courseId.setValue(courseId);
    }

    void setBookmark() {
        Resource<CourseWithModule> moduleResource = courseModule.getValue();
        Log.d(TAG, "values_ : " + courseModule.getValue());
        if (moduleResource != null) {
            CourseWithModule courseWithModule = moduleResource.data;
            if (courseWithModule != null) {
                CourseEntity courseEntity = courseWithModule.mCourse;
                final boolean newState = !courseEntity.isBookmarked();
                academyRepository.setCourseBookmark(courseEntity, newState);
            }
        }
    }
}
