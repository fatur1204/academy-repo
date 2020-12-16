package com.fathur.academy.ui.bookmark;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ShareCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fathur.academy.R;
import com.fathur.academy.data.source.local.entity.CourseEntity;
import com.fathur.academy.databinding.FragmentBookmarkBinding;
import com.fathur.academy.ui.academy.viewmodel.ViewModelFactory;
import com.google.android.material.snackbar.Snackbar;

public class BookmarkFragment extends Fragment implements BookmarkFragmentCallback {
    private static final String TAG = BookmarkFragment.class.getSimpleName();
    private FragmentBookmarkBinding fragmentBookmarkBinding;

    private BookmarkViewModel viewModel;
    private BookmarkAdapter adapter;

    public BookmarkFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_bookmark, container, false);
        fragmentBookmarkBinding = FragmentBookmarkBinding.inflate(inflater, container, false);
        return fragmentBookmarkBinding.getRoot();
        //return inflater.inflate(R.layout.fragment_bookmark, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "frag bookmark : " + getActivity());
        if (getActivity() != null) {
            ViewModelFactory factory = ViewModelFactory.getInstance(getActivity());

            viewModel = new ViewModelProvider(this, factory).get(BookmarkViewModel.class);
            //List<CourseEntity> courses = viewModel.getBookmarks();

            adapter = new BookmarkAdapter(this);
            //adapter.setCourses(courses);

            itemTouchHelper.attachToRecyclerView(fragmentBookmarkBinding.rvBookmark);

            fragmentBookmarkBinding.progressBar.setVisibility(View.VISIBLE);
            viewModel.getBookmarks().observe(this, courses -> {
                fragmentBookmarkBinding.progressBar.setVisibility(View.GONE);
                adapter.submitList(courses);
                //adapter.notifyDataSetChanged();
            });

            fragmentBookmarkBinding.rvBookmark.setLayoutManager(new LinearLayoutManager(getContext()));
            fragmentBookmarkBinding.rvBookmark.setHasFixedSize(true);
            fragmentBookmarkBinding.rvBookmark.setAdapter(adapter);
        }
    }

    private ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            return makeMovementFlags(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            if (getView() != null) {
                int swipedPosition = viewHolder.getAdapterPosition();
                CourseEntity courseEntity = adapter.getSwipedData(swipedPosition);
                viewModel.setBookmark(courseEntity);
                Snackbar snackbar = Snackbar.make(getView(), R.string.message_undo, Snackbar.LENGTH_LONG);
                snackbar.setAction(R.string.message_ok, v -> viewModel.setBookmark(courseEntity));
                snackbar.show();
            }
        }
    });

    @Override
    public void onShareClick(CourseEntity course) {
        if (getActivity() != null) {
            String mimeType = "text/plain";
            ShareCompat.IntentBuilder
                    .from(getActivity())
                    .setType(mimeType)
                    .setChooserTitle("Bagikan aplikasi ini sekarang.")
                    .setText(getResources().getString(R.string.share_text, course.getTitle()))
                    .startChooser();
        }
    }
}