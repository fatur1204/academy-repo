package com.fathur.academy.ui.reader.content;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.fathur.academy.data.source.local.entity.ModuleEntity;
import com.fathur.academy.databinding.FragmentModuleContentBinding;
import com.fathur.academy.ui.CourseReaderViewModel;
import com.fathur.academy.ui.academy.viewmodel.ViewModelFactory;


public class ModuleContentFragment extends Fragment {
    public static final String TAG = ModuleContentFragment.class.getSimpleName();
    private FragmentModuleContentBinding fragmentModuleContentBinding;
    private CourseReaderViewModel viewModel;

    public ModuleContentFragment() {
        // Required empty public constructor
    }

    public static ModuleContentFragment newInstance() {
        return new ModuleContentFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_module_content, container, false);

        fragmentModuleContentBinding = FragmentModuleContentBinding.inflate(inflater, container, false);
        return fragmentModuleContentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() != null) {
            ViewModelFactory factory = ViewModelFactory.getInstance(requireActivity());
            viewModel = new ViewModelProvider(requireActivity(), factory).get(CourseReaderViewModel.class);

            viewModel.selectedModule.observe(this, moduleEntity -> {
                if (moduleEntity != null) {
                    switch (moduleEntity.status) {
                        case LOADING:
                            fragmentModuleContentBinding.progressBar.setVisibility(View.VISIBLE);
                            break;
                        case SUCCESS:
                            if (moduleEntity.data != null) {
                                fragmentModuleContentBinding.progressBar.setVisibility(View.GONE);
                                if (moduleEntity.data.contentEntity != null) {
                                    populateWebView(moduleEntity.data);
                                }
                                Log.d(TAG, "button proc : " + moduleEntity.data);
                                setButtonNextPrevState(moduleEntity.data);
                                if (!moduleEntity.data.isRead()) {
                                    viewModel.readContent(moduleEntity.data);
                                }
                            }
                            break;
                        case ERROR:
                            fragmentModuleContentBinding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    fragmentModuleContentBinding.btnNext.setOnClickListener(v -> viewModel.setNextPage());
                    fragmentModuleContentBinding.btnPrev.setOnClickListener(v -> viewModel.setPrevPage());
                }
            });
        }
    }

    private void setButtonNextPrevState(ModuleEntity module) {
        if (getActivity() != null) {
            if (module.getmPosition() == 0) {
                Log.d(TAG, "button prev disable");
                fragmentModuleContentBinding.btnPrev.setEnabled(false);
                fragmentModuleContentBinding.btnNext.setEnabled(true);
            } else if (module.getmPosition() == viewModel.getModuleSize() - 1) {
                Log.d(TAG, "button next disable");
                fragmentModuleContentBinding.btnPrev.setEnabled(true);
                fragmentModuleContentBinding.btnNext.setEnabled(false);
            } else {
                Log.d(TAG, "button Active");
                fragmentModuleContentBinding.btnPrev.setEnabled(true);
                fragmentModuleContentBinding.btnNext.setEnabled(true);
            }
        }
    }

    private void populateWebView(ModuleEntity module) {
        fragmentModuleContentBinding.webView.loadData(module.contentEntity.getmContent(), "text/html", "UTF-8");
    }
}