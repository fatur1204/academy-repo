package com.fathur.academy.ui.reader.list;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fathur.academy.MyAdapterClickListener;
import com.fathur.academy.data.source.local.entity.ModuleEntity;
import com.fathur.academy.databinding.FragmentModuleListBinding;
import com.fathur.academy.ui.CourseReaderActivity;
import com.fathur.academy.ui.CourseReaderCallback;
import com.fathur.academy.ui.CourseReaderViewModel;
import com.fathur.academy.ui.academy.viewmodel.ViewModelFactory;

import java.util.List;


public class ModuleListFragment extends Fragment implements MyAdapterClickListener {
    public static final String TAG = ModuleListFragment.class.getSimpleName();

    private CourseReaderViewModel viewModel;
    private FragmentModuleListBinding fragmentModuleListBinding;
    private ModuleListAdapter adapter;
    private CourseReaderCallback courseReaderCallback;

    public ModuleListFragment() {
        // Required empty public constructor
    }

    public static ModuleListFragment newInstance() {
        return new ModuleListFragment();
    }


    /*@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_module_list, container, false);

        fragmentModuleListBinding = FragmentModuleListBinding.inflate(inflater, container, false);
        return fragmentModuleListBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() != null) {
            ViewModelFactory factory = ViewModelFactory.getInstance(requireActivity());
            viewModel = new ViewModelProvider(requireActivity(), factory).get(CourseReaderViewModel.class);
            //viewModel = new ViewModelProvider(requireActivity(), new ViewModelProvider.NewInstanceFactory()).get(CourseReaderViewModel.class);
            adapter = new ModuleListAdapter(this);
            //populateRecyclerView(viewModel.getModules());

            viewModel.modules.observe(this, moduleEntities -> {
                if (moduleEntities != null) {
                    switch (moduleEntities.status) {
                        case LOADING:
                            fragmentModuleListBinding.progressBar.setVisibility(View.VISIBLE);
                            break;
                        case SUCCESS:
                            fragmentModuleListBinding.progressBar.setVisibility(View.GONE);
                            populateRecyclerView(moduleEntities.data);
                            break;
                        case ERROR:
                            fragmentModuleListBinding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        courseReaderCallback = ((CourseReaderActivity) context);
    }

    @Override
    public void onItemClicked(int position, String moduleId) {
        courseReaderCallback.moveTo(position, moduleId);
        viewModel.setSelectedModule(moduleId);
    }

    private void populateRecyclerView(List<ModuleEntity> modules) {
        fragmentModuleListBinding.progressBar.setVisibility(View.GONE);
        adapter.setModules(modules);
        fragmentModuleListBinding.rvModule.setLayoutManager(new LinearLayoutManager(getContext()));
        fragmentModuleListBinding.rvModule.setHasFixedSize(true);
        fragmentModuleListBinding.rvModule.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        fragmentModuleListBinding.rvModule.addItemDecoration(dividerItemDecoration);
    }
}