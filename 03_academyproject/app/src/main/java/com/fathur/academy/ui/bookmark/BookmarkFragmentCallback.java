package com.fathur.academy.ui.bookmark;

import com.fathur.academy.data.source.local.entity.CourseEntity;

public interface BookmarkFragmentCallback {
    void onShareClick(CourseEntity course);
}
