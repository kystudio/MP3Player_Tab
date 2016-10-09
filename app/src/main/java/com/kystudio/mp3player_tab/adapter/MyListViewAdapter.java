package com.kystudio.mp3player_tab.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.kystudio.model.Mp3Info;
import com.kystudio.mp3player_tab.PlayerActivity;
import com.kystudio.mp3player_tab.R;
import com.kystudio.mp3player_tab.service.DownloadService;

import java.util.HashMap;
import java.util.List;

/**
 * Created by 20236320 on 2016/10/9.
 */

public class MyListViewAdapter extends BaseAdapter {
    public final class ViewHolder {
        public TextView mp3Name;
        public TextView mp3Size;
        public Button mp3Play;
        public Button mp3Download;
    }

    private List<HashMap<String, String>> mAppList;
    private final LayoutInflater mInflater;
    private Context mContext;
    private String[] keyString;
    private int[] valueViewID;
    private ViewHolder holder;

    //    public MyListViewAdapter(Context context, List<HashMap<String, String>> appList, int resource,
//                             String[] from, int[] to) {
//        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        mAppList = appList;
//        mContext = context;
//        keyString = new String[from.length];
//        valueViewID = new int[to.length];
//        System.arraycopy(from, 0, keyString, 0, from.length);
//        System.arraycopy(to, 0, valueViewID, 0, to.length);
//    }
    public MyListViewAdapter(Context context, List<HashMap<String, String>> appList, int resource,
                             String[] from, int[] to) {
        mInflater = LayoutInflater.from(context);
        mAppList = appList;
        mContext = context;
        keyString = new String[from.length];
        valueViewID = new int[to.length];
        System.arraycopy(from, 0, keyString, 0, from.length);
        System.arraycopy(to, 0, valueViewID, 0, to.length);
    }

    @Override
    public int getCount() {
        return mAppList.size();
    }

    @Override
    public Object getItem(int position) {
        return mAppList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        holder = null;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.remote_mp3info_item, null);
            holder.mp3Name = (TextView) convertView.findViewById(R.id.mp3_name);
            holder.mp3Size = (TextView) convertView.findViewById(R.id.mp3_size);
            holder.mp3Play = (Button) convertView.findViewById(R.id.mp3_play);
            holder.mp3Download = (Button) convertView.findViewById(R.id.mp3_download);
            convertView.setTag(holder);
        }
        HashMap<String, String> appInfo = mAppList.get(position);
        if (appInfo != null) {
            String mp3_name = appInfo.get(keyString[0]);
            String mp3_size = appInfo.get(keyString[1]);

            holder.mp3Name.setText(mp3_name);
            holder.mp3Size.setText(mp3_size);
            holder.mp3Play.setOnClickListener(new lvButtonListener(position));
            holder.mp3Download.setOnClickListener(new lvButtonListener(position));
        }

        return convertView;
    }

    class lvButtonListener implements View.OnClickListener {
        private int position;

        lvButtonListener(int pos) {
            position = pos;
        }

        //  @Override
        public void onClick(View v) {
            String mp3_name = "";
            String mp3_size = "";
            String lrc_name = "";
            String lrc_size = "";
            Mp3Info mp3Info = null;
            HashMap<String, String> appInfo = mAppList.get(position);
            if (appInfo != null) {
                mp3_name = appInfo.get(keyString[0]);
                mp3_size = appInfo.get(keyString[1]);
                lrc_name = appInfo.get("lrc_name");
                lrc_size = appInfo.get("lrc_size");
                mp3Info = new Mp3Info();
                mp3Info.setMp3Name(mp3_name);
                mp3Info.setMp3Size(mp3_size);
                mp3Info.setLrcName(lrc_name);
                mp3Info.setLrcSize(lrc_size);
            }
            int vid = v.getId();
            if (vid == holder.mp3Play.getId()) {
                playMp3(mp3Info);
            } else if (vid == holder.mp3Download.getId()) {
                downloadMp3(mp3Info);
            }
        }
    }

    private void playMp3(Mp3Info mp3Info){
        Intent intent = new Intent();
        intent.putExtra("mp3Info", mp3Info);
        intent.setClass(mContext, PlayerActivity.class);
        mContext.startActivity(intent);
    }

    private void downloadMp3(Mp3Info mp3Info){
        Intent intent = new Intent();
        intent.putExtra("mp3Info", mp3Info);
        intent.setClass(mContext, DownloadService.class);
        mContext.startService(intent);
    }
}