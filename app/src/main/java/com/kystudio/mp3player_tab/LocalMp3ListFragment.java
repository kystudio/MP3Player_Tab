package com.kystudio.mp3player_tab;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.kystudio.model.Mp3Info;
import com.kystudio.utils.FileUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/**
 * A simple {@link ListFragment} subclass.
 */
public class LocalMp3ListFragment extends ListFragment {
    private List<Mp3Info> mp3Infos = null;

    public LocalMp3ListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_local_mp3_list, container, false);

        return rootView;
    }

    @Override
    public void onResume() {
        FileUtils fileUtils = new FileUtils();
        mp3Infos = fileUtils.getMp3Files("mp3/");
        List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        for (Iterator iterator = mp3Infos.iterator(); iterator.hasNext(); ) {
            Mp3Info mp3Info = (Mp3Info) iterator.next();
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("mp3_name", mp3Info.getMp3Name());
            map.put("mp3_size", mp3Info.getMp3Size());

            list.add(map);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(this.getActivity(), list, R.layout.local_mp3info_item, new String[]{"mp3_name", "mp3_size"}, new int[]{R.id.mp3_name, R.id.mp3_size});
        setListAdapter(simpleAdapter);

        super.onResume();
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (mp3Infos != null) {
            Mp3Info mp3Info = mp3Infos.get(position);
            Intent intent = new Intent();
            intent.putExtra("mp3Info", mp3Info);
            intent.setClass(this.getActivity(), PlayerActivity.class);
            startActivity(intent);
        }
    }
}
