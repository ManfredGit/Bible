package com.sinnus.bible.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sinnus.bible.R;
import com.sinnus.bible.bean.Track;

import java.util.ArrayList;

/**
 * Created by sinnus on 2016/1/17.
 */
public class TrackAdapter extends BaseAdapter {
    private ArrayList<Track> tracks;
    private LayoutInflater inflater;

    public TrackAdapter(Context context, ArrayList<Track> tracks) {
        inflater = LayoutInflater.from(context);
        this.tracks = tracks;
    }
    @Override
    public int getCount() {
        return tracks.size();
    }

    @Override
    public Object getItem(int position) {
        return position;//改成切换全局 书章
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_view_item, null, false);
            holder.id = (TextView) convertView.findViewById(R.id.section_id);
            holder.content = (TextView) convertView.findViewById(R.id.section_content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.id.setText("" + (position + 1));
//        holder.content.setText(chapter.getSection(position).toString());
        return convertView;
    }

    class ViewHolder {
        public TextView id;
        public TextView content;
    }
}
