package com.fathur.academy.data.source.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(tableName = "moduleentities",
        primaryKeys = {"moduleId", "courseId"},
        foreignKeys = @ForeignKey(entity = CourseEntity.class,
                parentColumns = "courseId",
                childColumns = "courseId"),
        indices = {@Index(value = "moduleId"),
                @Index(value = "courseId")}
)
public class ModuleEntity {
    @Embedded
    public ContentEntity contentEntity;

    @NonNull
    @ColumnInfo(name = "moduleId")
    public String mModuleId;

    @NonNull
    @ColumnInfo(name = "courseId")
    public String mCourseId;

    @NonNull
    @ColumnInfo(name = "title")
    public String mTitle;

    @NonNull
    @ColumnInfo(name = "position")
    public Integer mPosition;

    @ColumnInfo(name = "read")
    private boolean mRead = false;

    public ModuleEntity(String mModuleId, String mCourseId, String mTitle, Integer mPosition, boolean read) {
        this.mModuleId = mModuleId;
        this.mCourseId = mCourseId;
        this.mTitle = mTitle;
        this.mPosition = mPosition;
        //this.mRead = mRead;
        if (read) {
            this.mRead = read;
        }
    }

    public ContentEntity getContentEntity() {
        return contentEntity;
    }

    public void setContentEntity(ContentEntity contentEntity) {
        this.contentEntity = contentEntity;
    }

    public String getmModuleId() {
        return mModuleId;
    }

    public void setmModuleId(String mModuleId) {
        this.mModuleId = mModuleId;
    }

    public String getmCourseId() {
        return mCourseId;
    }

    public void setmCourseId(String mCourseId) {
        this.mCourseId = mCourseId;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public Integer getmPosition() {
        return mPosition;
    }

    public void setmPosition(Integer mPosition) {
        this.mPosition = mPosition;
    }

    public boolean isRead() {
        return mRead;
    }

    public void setmRead(boolean mRead) {
        this.mRead = mRead;
    }
}
