package com.fathur.academy.ui.reader.list;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fathur.academy.MyAdapterClickListener;
import com.fathur.academy.data.source.local.entity.ModuleEntity;
import com.fathur.academy.databinding.ItemsModuleListCustomBinding;

import java.util.ArrayList;
import java.util.List;

public class ModuleListAdapter extends RecyclerView.Adapter<ModuleListAdapter.ModuleViewHolder> {
    private final MyAdapterClickListener listener;
    private final List<ModuleEntity> listModules = new ArrayList<ModuleEntity>();

    public ModuleListAdapter(MyAdapterClickListener listener) {
        this.listener = listener;
    }

    public void setModules(List<ModuleEntity> listModules) {
        if (listModules == null) return;
        this.listModules.clear();
        this.listModules.addAll(listModules);
    }

    @NonNull
    @Override
    public ModuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(ModuleListAdapter.class.getSimpleName(), "dataku 00");
        ItemsModuleListCustomBinding binding = ItemsModuleListCustomBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ModuleViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ModuleViewHolder holder, int position) {
        ModuleEntity module = listModules.get(position);
        holder.bind(module);
        holder.itemView.setOnClickListener(v ->
                listener.onItemClicked(holder.getAdapterPosition(), listModules.get(holder.getAdapterPosition()).getmModuleId())
        );
    }

    @Override
    public int getItemCount() {
        Log.d(ModuleListAdapter.class.getSimpleName(), "size data : " + listModules.size());
        return listModules.size();
    }

    public class ModuleViewHolder extends RecyclerView.ViewHolder {
        private final ItemsModuleListCustomBinding binding;

        ModuleViewHolder(ItemsModuleListCustomBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ModuleEntity module) {
            binding.textModuleTitle.setText(module.getmTitle());
        }
    }
}
