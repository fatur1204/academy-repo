package com.fathur.academy.ui.bookmark;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fathur.academy.R;
import com.fathur.academy.data.source.local.entity.CourseEntity;
import com.fathur.academy.databinding.ItemsBookmarkBinding;
import com.fathur.academy.ui.detail.DetailCourseActivity;

import java.util.ArrayList;

public class BookmarkAdapter extends PagedListAdapter<CourseEntity, BookmarkAdapter.CourseViewHolder> {
    private static final String TAG = BookmarkAdapter.class.getSimpleName();
    private final BookmarkFragmentCallback callback;
    private final ArrayList<CourseEntity> listCourses = new ArrayList<>();

    public BookmarkAdapter(BookmarkFragmentCallback callback) {
        super(DIFF_CALLBACK);
        this.callback = callback;
    }

    private static DiffUtil.ItemCallback<CourseEntity> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<CourseEntity>() {
                @Override
                public boolean areItemsTheSame(@NonNull CourseEntity oldItem, @NonNull CourseEntity newItem) {
                    return oldItem.getCourseId().equals(newItem.getCourseId());
                }

                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(@NonNull CourseEntity oldItem, @NonNull CourseEntity newItem) {
                    return oldItem.equals(newItem);
                }
            };

    /*public void setCourses(List<CourseEntity> courses) {
        if (courses == null) return;
        this.listCourses.clear();
        this.listCourses.addAll(courses);
    }*/


    @NonNull
    @Override
    public BookmarkAdapter.CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemsBookmarkBinding binding = ItemsBookmarkBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CourseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarkAdapter.CourseViewHolder holder, int position) {
        CourseEntity course = getItem(position);
        if (course != null) {
            holder.bind(course);
        }
    }

    public CourseEntity getSwipedData(int swipedPosition) {
        return getItem(swipedPosition);
    }

    //@Override
    /*public int getItemCount() {
        return listCourses.size();
    }*/

    public class CourseViewHolder extends RecyclerView.ViewHolder {
        private final ItemsBookmarkBinding binding;

        CourseViewHolder(ItemsBookmarkBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(CourseEntity course) {
            Log.d(TAG, "data bookmark : " + course.getTitle());
            binding.tvItemTitle.setText(course.getTitle());
            binding.tvItemDate.setText(itemView.getResources().getString(R.string.deadline_date, course.getDeadline()));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), DetailCourseActivity.class);
                    intent.putExtra(DetailCourseActivity.EXTRA_COURSE, course.getCourseId());
                    itemView.getContext().startActivity(intent);
                }
            });
            binding.imgShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onShareClick(course);
                }
            });
            Glide.with(itemView.getContext())
                    .load(course.getImagePath())
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_loading).error(R.drawable.ic_error))
                    .into(binding.imgPoster);
        }
    }
}
