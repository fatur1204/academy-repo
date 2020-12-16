package com.fathur.academy.ui.academy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fathur.academy.databinding.FragmentAcademyBinding;
import com.fathur.academy.ui.academy.viewmodel.ViewModelFactory;

public class AcademyFragment extends Fragment {
    private FragmentAcademyBinding fragmentAcademyBinding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_academy, container, false);

        fragmentAcademyBinding = FragmentAcademyBinding.inflate(inflater, container, false);
        return fragmentAcademyBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() != null) {
            ViewModelFactory factory = ViewModelFactory.getInstance(getActivity());
            AcademyViewModel viewModel = new ViewModelProvider(this, factory).get(AcademyViewModel.class);

            AcademyAdapter academyAdapter = new AcademyAdapter();

            viewModel.getCourses().observe(this, courses -> {
                if (courses != null) {
                    switch (courses.status) {
                        case LOADING:
                            fragmentAcademyBinding.progressBar.setVisibility(View.VISIBLE);
                            break;
                        case SUCCESS:
                            fragmentAcademyBinding.progressBar.setVisibility(View.GONE);
                            academyAdapter.submitList(courses.data);
                            //academyAdapter.notifyDataSetChanged();
                            break;
                        case ERROR:
                            fragmentAcademyBinding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });

            fragmentAcademyBinding.rvAcademy.setLayoutManager(new LinearLayoutManager(getContext()));
            fragmentAcademyBinding.rvAcademy.setHasFixedSize(true);
            fragmentAcademyBinding.rvAcademy.setAdapter(academyAdapter);
        }
    }
}