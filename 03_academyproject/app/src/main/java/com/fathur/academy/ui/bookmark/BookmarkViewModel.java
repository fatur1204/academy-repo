package com.fathur.academy.ui.bookmark;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.PagedList;

import com.fathur.academy.data.source.AcademyRepository;
import com.fathur.academy.data.source.local.entity.CourseEntity;

public class BookmarkViewModel extends ViewModel {
    /*public List<CourseEntity> getBookmarks() {
        return DataDummy.generateDummyCourses();
    }*/

    private AcademyRepository academyRepository;

    public BookmarkViewModel(AcademyRepository mAcademyRepository) {
        this.academyRepository = mAcademyRepository;
    }

    public LiveData<PagedList<CourseEntity>> getBookmarks() {
        return academyRepository.getBookmarkedCourses();
    }

    void setBookmark(CourseEntity courseEntity) {
        final boolean newState = !courseEntity.isBookmarked();
        academyRepository.setCourseBookmark(courseEntity, newState);
    }
}
