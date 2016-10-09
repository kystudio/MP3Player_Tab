package com.kystudio.mp3player_tab;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.kystudio.download.HttpDownloader;
import com.kystudio.model.Mp3Info;
import com.kystudio.mp3player_tab.adapter.MyListViewAdapter;
import com.kystudio.mp3player_tab.service.DownloadService;
import com.kystudio.xml.Mp3ListContentHandler;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;


/**
 * A simple {@link ListFragment} subclass.
 */
public class RemoteMp3ListFragment extends ListFragment {
    private List<Mp3Info> mp3Infos = null;

    public RemoteMp3ListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_remote_mp3_list, container, false);
        //updateListView();
        return rootView;
    }

    @Override
    public void onResume() {
        updateListView();
        super.onResume();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Mp3Info mp3Info = mp3Infos.get(position);
//        System.out.println(mp3Info);

        Intent intent = new Intent();
        intent.putExtra("mp3Info", mp3Info);
        intent.setClass(this.getActivity(), DownloadService.class);
        getActivity().startService(intent);
        super.onListItemClick(l, v, position, id);
    }

    public void updateListView() {
        downloadXML(AppConstant.URL.BASE_URL + "resource.xml");
    }

    private void downloadXML(String urlStr) {
        MyDownloadThread md = new MyDownloadThread(urlStr);
        md.start();
    }

    private List<Mp3Info> parse(String xmlStr) {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        List<Mp3Info> infos = new ArrayList<Mp3Info>();

        try {
            XMLReader xmlReader = saxParserFactory.newSAXParser().getXMLReader();
            Mp3ListContentHandler mp3ListContentHandler = new Mp3ListContentHandler(infos);
            xmlReader.setContentHandler(mp3ListContentHandler);
            xmlReader.parse(new InputSource(new StringReader(xmlStr)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return infos;
    }

    private MyListViewAdapter buildSimpleAdapter(List<Mp3Info> mp3Infos) {
        List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        for (Iterator iterator = mp3Infos.iterator(); iterator.hasNext(); ) {
            Mp3Info mp3Info = (Mp3Info) iterator.next();
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("mp3_name", mp3Info.getMp3Name());
            map.put("mp3_size", mp3Info.getMp3Size());
            map.put("lrc_name", mp3Info.getLrcName());
            map.put("lrc_size", mp3Info.getLrcSize());
            list.add(map);
        }
        MyListViewAdapter myListViewAdapter = new MyListViewAdapter(getActivity(), list, R.layout.remote_mp3info_item, new String[]{"mp3_name", "mp3_size"}, new int[]{R.id.mp3_name, R.id.mp3_size});

        return myListViewAdapter;
    }

    class MyDownloadThread extends Thread {
        private String urlStr;

        public MyDownloadThread(String urlStr) {
            this.urlStr = urlStr;
        }

        @Override
        public void run() {
            HttpDownloader httpDownloader = new HttpDownloader();
            String result = httpDownloader.downloadTxt(urlStr);

            mp3Infos = parse(result);
            //System.out.println(mp3Infos);
            // 更新UI
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MyListViewAdapter myListViewAdapter = buildSimpleAdapter(mp3Infos);
                    setListAdapter(myListViewAdapter);
                }
            });
        }
    }
}
