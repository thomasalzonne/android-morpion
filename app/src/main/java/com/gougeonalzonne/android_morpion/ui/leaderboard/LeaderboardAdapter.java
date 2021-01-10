package com.gougeonalzonne.android_morpion.ui.leaderboard;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gougeonalzonne.android_morpion.R;


public class LeaderboardAdapter extends BaseAdapter {
    private static ArrayList<LeaderboardItem> leaderboardArrayList;

    private LayoutInflater mInflater;

    public LeaderboardAdapter(Context context, ArrayList<LeaderboardItem> results) {
        leaderboardArrayList = results;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return leaderboardArrayList.size();
    }

    public Object getItem(int position) {
        return leaderboardArrayList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.leaderboard_field, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.board_title);
            holder.subTitle = (TextView) convertView.findViewById(R.id.board_subTitle);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(leaderboardArrayList.get(position).getTitle());
        holder.subTitle.setText(leaderboardArrayList.get(position).getSubTitle());

        return convertView;
    }

    static class ViewHolder {
        TextView title;
        TextView subTitle;
    }
}