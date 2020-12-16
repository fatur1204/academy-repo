package com.fathur.academy.data.source.local.room;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.fathur.academy.data.source.local.entity.CourseEntity;
import com.fathur.academy.data.source.local.entity.CourseWithModule;
import com.fathur.academy.data.source.local.entity.ModuleEntity;

import java.util.List;

@Dao
public interface AcademyDao {
    @Query("SELECT * FROM courseentities")
        //LiveData<List<CourseEntity>> getCourses();
    DataSource.Factory<Integer, CourseEntity> getCourse();

    @Query("SELECT * FROM courseentities where bookmarked = 1")
        //LiveData<List<CourseEntity>> getBookmarkedCourse();
    DataSource.Factory<Integer, CourseEntity> getBookmarkedCourse();

    @Transaction
    @Query("SELECT * FROM courseentities WHERE courseId = :courseId")
    LiveData<CourseWithModule> getCourseWithModuleById(String courseId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCourses(List<CourseEntity> courses);

    @Update
    void updateCourse(CourseEntity course);

    @Query("SELECT * FROM moduleentities WHERE courseId = :courseId")
    LiveData<List<ModuleEntity>> getModulesByCourseId(String courseId);

    @Query("SELECT * FROM moduleentities WHERE moduleId = :moduleId")
    LiveData<ModuleEntity> getModuleById(String moduleId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModules(List<ModuleEntity> module);

    @Update
    void updateModule(ModuleEntity module);

    @Query("UPDATE moduleentities SET content = :content WHERE moduleId = :moduleId")
    void updateModuleByContent(String content, String moduleId);
}
