package com.fathur.academy.ui.academy;

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
import com.fathur.academy.databinding.ItemsAcademyBinding;
import com.fathur.academy.ui.detail.DetailCourseActivity;

import java.util.ArrayList;
import java.util.List;

public class AcademyAdapter extends PagedListAdapter<CourseEntity, AcademyAdapter.CourseViewHolder> {
    private static final String TAG = AcademyAdapter.class.getSimpleName();
    private final List<CourseEntity> listCourses = new ArrayList<>();

    AcademyAdapter() {
        super(DIFF_CALLBACK);
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

    /*public void setCourses(List<CourseEntity> listCourses) {
        if (listCourses == null) return;
        this.listCourses.clear();
        this.listCourses.addAll(listCourses);
    }*/


    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemsAcademyBinding binding = ItemsAcademyBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CourseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        CourseEntity course = getItem(position);
        if (course != null) {
            holder.bind(course);
        }
    }

    /*@Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        CourseEntity course = listCourses.get(position);
        holder.bind(course);
    }*/

    //@Override
    /*public int getItemCount() {
        return listCourses.size();
    }*/

    public class CourseViewHolder extends RecyclerView.ViewHolder {
        private final ItemsAcademyBinding binding;

        public CourseViewHolder(ItemsAcademyBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(CourseEntity course) {
            Log.d(TAG, "data academy : " + course.getTitle());
            binding.tvItemTitle.setText(course.getTitle());
            binding.tvItemDate.setText(itemView.getResources().getString(R.string.deadline_date, course.getDeadline()));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Call Detail Course : " + course.getCourseId());
                    Intent intent = new Intent(itemView.getContext(), DetailCourseActivity.class);
                    intent.putExtra(DetailCourseActivity.EXTRA_COURSE, course.getCourseId());
                    itemView.getContext().startActivity(intent);
                }
            });
            Glide.with(itemView.getContext())
                    .load(course.getImagePath())
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_loading).error(R.drawable.ic_error))
                    .into(binding.imgPoster);
        }
    }
}
