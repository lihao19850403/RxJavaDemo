package com.lihao.rxjavademo.gobang_game;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lihao.rxjavademo.R;
import com.lihao.rxjavademo.gobang_game.model.GameState;
import com.lihao.rxjavademo.gobang_game.model.SymbolType;
import com.lihao.rxjavademo.gobang_game.view.GameGridView;
import com.lihao.rxjavademo.gobang_game.view.PlayerView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

public class GobangSavedBrowserAdapter extends RecyclerView.Adapter<GobangSavedBrowserAdapter.GameInfoViewHolder> {

    private List<String> dataList = new ArrayList<String>();
    private OnItemClickListener listener;
    private OnItemClickListener longListener;

    protected class GameInfoViewHolder extends RecyclerView.ViewHolder {

        private TextView fileInfoView;
        private GameGridView fileInfoIcon;
        private PlayerView nextPlayerIcon;

        public GameInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            fileInfoView = itemView.findViewById(R.id.file_info_view);
            fileInfoIcon = itemView.findViewById(R.id.file_info_icon);
            nextPlayerIcon = itemView.findViewById(R.id.next_player_icon);
        }
    }

    /** 单击接口。 */
    public interface OnItemClickListener {

        /**
         * 单击事件。
         * @param view 单击的条目视图。
         * @param savedGameName 单击的存档名称。
         */
        public void onClick(View view, String savedGameName);
    }

    @NonNull
    @Override
    public GameInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gobang_browser, null);
        return new GameInfoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GameInfoViewHolder holder, int position) {
        String savedGameName = dataList.get(position);
        final String dateInfo = new String(Base64.decode(savedGameName.getBytes(), Base64.DEFAULT));
        Pair<GameState, long[]> gameInfoPair = GameUtils.loadGame(dateInfo);
        GameState gameState = gameInfoPair.first;
        // 棋盘缩略图。
        holder.fileInfoIcon.setData(gameState.gameGrid);
        // 存档时间。
        String[] dateInfos = dateInfo.split(" ");
        holder.fileInfoView.setText(dateInfos[0].concat("\n").concat(dateInfos[1]));
        // 下一位玩家指示器。
        holder.nextPlayerIcon.setData(gameState.lastPlayedSymbol == SymbolType.BLACK ? SymbolType.RED : SymbolType.BLACK);
        // 单击事件。
        holder.itemView.setClickable(true);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(holder.itemView, dateInfo);
            }
        });
        holder.itemView.setOnLongClickListener(v -> {
            if (longListener != null) {
                longListener.onClick(holder.itemView, dateInfo);
            }
            return false;
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
    public void setDataList(List<String> newDataList) {
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

    /**
     * 设置列表项长按事件回调。
     * @param newListener 列表项长按事件回调。
     */
    public void setOnItemLongClickListener(OnItemClickListener newListener) {
        longListener = newListener;
    }
}
