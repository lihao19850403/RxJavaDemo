package com.lihao.rxjavademo.filebrowser;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lihao.rxjavademo.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FileBrowserAdapter extends RecyclerView.Adapter<FileBrowserAdapter.FileInfoViewHolder> {

    private List<File> dataList = new ArrayList<File>();
    private OnItemClickListener listener;

    protected class FileInfoViewHolder extends RecyclerView.ViewHolder {

        private TextView fileInfoView;
        private ImageView fileInfoIcon;

        public FileInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            fileInfoView = itemView.findViewById(R.id.file_info_view);
            fileInfoIcon = itemView.findViewById(R.id.file_info_icon);
        }
    }

    /** 单击接口。 */
    public interface OnItemClickListener {

        /**
         * 单击事件。
         * @param view 单击的条目视图。
         * @param file 单击的File数据项。
         */
        public void onClick(View view, File file);
    }

    @NonNull
    @Override
    public FileInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file_browser, null);
        return new FileInfoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FileInfoViewHolder holder, int position) {
        final File file = dataList.get(position);
        holder.fileInfoView.setText(file.getName());
        // 根据文件类型，指定不同的标记图标。
        if (file != null && file.isDirectory()) {
            holder.fileInfoIcon.setImageResource(R.drawable.file_info_directory);
        } else {
            holder.fileInfoIcon.setImageResource(R.drawable.file_info_data);
        }
        holder.itemView.setClickable(true);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(holder.itemView, file);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    /**
     * 设置新的数据集，如果新数据集是空的，则整个列表内容都会被清空。
     *
     * @param newDataList 新数据集。
     */
    public void setDataList(List<File> newDataList) {
        dataList.clear();
        if (newDataList != null && newDataList.size() > 0) {
            dataList.addAll(newDataList);
        }
    }

    /**
     * 设置列表项单击事件回调。
     * @param newListener 列表项单击事件回调。
     */
    public void setOnItemClickListener(OnItemClickListener newListener) {
        listener = newListener;
    }
}
