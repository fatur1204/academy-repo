package com.fathur.academy.ui.detail;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.fathur.academy.R;
import com.fathur.academy.data.source.local.entity.CourseEntity;
import com.fathur.academy.databinding.ActivityDetailCourseBinding;
import com.fathur.academy.databinding.ContentDetailCourseBinding;
import com.fathur.academy.ui.CourseReaderActivity;
import com.fathur.academy.ui.academy.viewmodel.ViewModelFactory;

public class DetailCourseActivity extends AppCompatActivity {
    public static final String EXTRA_COURSE = "extra_course";
    private static final String TAG = DetailCourseActivity.class.getSimpleName();
    DetailCourseAdapter adapter = new DetailCourseAdapter();
    DetailCourseViewModel viewModel;
    private ContentDetailCourseBinding detailContentBinding;
    private ActivityDetailCourseBinding activityDetailCourseBinding;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_course);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        activityDetailCourseBinding = ActivityDetailCourseBinding.inflate(getLayoutInflater());
        detailContentBinding = activityDetailCourseBinding.detailContent;

        setContentView(activityDetailCourseBinding.getRoot());

        setSupportActionBar(activityDetailCourseBinding.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ViewModelFactory factory = ViewModelFactory.getInstance(this);
        viewModel = new ViewModelProvider(this, factory).get(DetailCourseViewModel.class);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String courseId = extras.getString(EXTRA_COURSE);
            Log.d(TAG, "data extra : " + courseId);
            if (courseId != null) {
                viewModel.setCourseId(courseId);
                activityDetailCourseBinding.content.setVisibility(View.GONE);

                viewModel.courseModule.observe(this, courseWithModuleResource -> {
                    if (courseWithModuleResource != null) {
                        switch (courseWithModuleResource.status) {
                            case LOADING:
                                activityDetailCourseBinding.progressBar.setVisibility(View.VISIBLE);
                                break;
                            case SUCCESS:
                                if (courseWithModuleResource.data != null) {
                                    activityDetailCourseBinding.progressBar.setVisibility(View.GONE);
                                    activityDetailCourseBinding.content.setVisibility(View.VISIBLE);
                                    adapter.setModules(courseWithModuleResource.data.mModules);
                                    adapter.notifyDataSetChanged();
                                    populateCourse(courseWithModuleResource.data.mCourse);
                                }
                                break;
                            case ERROR:
                                activityDetailCourseBinding.progressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
            }

        }

        detailContentBinding.rvModule.setNestedScrollingEnabled(false);
        detailContentBinding.rvModule.setLayoutManager(new LinearLayoutManager(this));
        detailContentBinding.rvModule.setHasFixedSize(true);
        detailContentBinding.rvModule.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(detailContentBinding.rvModule.getContext(), DividerItemDecoration.VERTICAL);
        detailContentBinding.rvModule.addItemDecoration(dividerItemDecoration);
    }

    private void populateCourse(CourseEntity courseEntity) {
        detailContentBinding.textTitle.setText(courseEntity.getTitle());
        detailContentBinding.textDescription.setText(courseEntity.getDescription());
        detailContentBinding.textDate.setText(getResources().getString(R.string.deadline_date, courseEntity.getDeadline()));

        Glide.with(this)
                .load(courseEntity.getImagePath())
                .transform(new RoundedCorners(20))
                .apply(RequestOptions.placeholderOf(R.drawable.ic_loading)
                        .error(R.drawable.ic_error))
                .into(detailContentBinding.imagePoster);

        detailContentBinding.btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailCourseActivity.this, CourseReaderActivity.class);
                intent.putExtra(CourseReaderActivity.EXTRA_COURSE_ID, courseEntity.getCourseId());
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        this.menu = menu;
        viewModel.courseModule.observe(this, courseWithModule -> {
            if (courseWithModule != null) {
                switch (courseWithModule.status) {
                    case LOADING:
                        activityDetailCourseBinding.progressBar.setVisibility(View.VISIBLE);
                        break;
                    case SUCCESS:
                        if (courseWithModule.data != null) {
                            activityDetailCourseBinding.progressBar.setVisibility(View.GONE);
                            boolean state = courseWithModule.data.mCourse.isBookmarked();
                            setBookmarkState(state);
                        }
                        break;
                    case ERROR:
                        activityDetailCourseBinding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_bookmark) {
            viewModel.setBookmark();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detailContentBinding = null;
        activityDetailCourseBinding = null;
    }

    private void setBookmarkState(boolean state) {
        Log.d(TAG, "state_value : " + state);
        if (menu == null) return;
        MenuItem menuItem = menu.findItem(R.id.action_bookmark);
        if (state) {
            menuItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_bookmark_white_full));
        } else {
            menuItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_bookmark_white));
        }
    }
}