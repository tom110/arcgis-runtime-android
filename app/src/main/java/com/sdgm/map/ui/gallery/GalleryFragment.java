package com.sdgm.map.ui.gallery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.sdgm.map.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class GalleryFragment extends Fragment {

    ListView listView;

    String mapsFolder = "maps";

    Intent locateLayerIntent = new Intent("locateLayer");

    File folder ;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = view.findViewById(R.id.listView);
        List<Layer> layers = new ArrayList<>();
//        getPermission(); //调试的时候要执行一次，以后可注释掉
        //判断文件夹是否存在，不存在则创建

        folder = new File(ContextCompat.getExternalFilesDirs(getActivity(), null)[0] + File.separator + mapsFolder);

        if (!folder.exists() && !folder.isDirectory()) {
            folder.mkdirs();
            Toast.makeText(getActivity().getApplicationContext(), "请把地图文件放入maps文件夹", Toast.LENGTH_SHORT).show();
        } else {
            Log.i(TAG, "onViewCreated: 文件夹已存在");
        }
        List<String> files = getFilesAllName();
        List<Layer> layerList = layers;
        if(files!=null) {
            if (files.size() > 0) {
                for (int i = 0; i < files.size(); i++) {
                    layerList.add(new Layer(files.get(i), files.get(i), false));
                }
            }
        }else{
            Toast.makeText(getActivity().getApplicationContext(), "获取文件夹文件出错", Toast.LENGTH_SHORT).show();
        }
        LayerListViewAdapter adapter = new LayerListViewAdapter(getActivity(), layerList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((adapterView, view1, itemIndex, l) -> {
            Object itemObject = adapterView.getAdapter().getItem(itemIndex);
            Layer itemDto = (Layer) itemObject;
            locateLayerIntent.putExtra("locateLayerName", itemDto.getName());
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(locateLayerIntent);
            Toast.makeText(getActivity().getApplicationContext(), "select item text : " + itemDto.getName(), Toast.LENGTH_SHORT).show();
        });
    }

    public List<String> getFilesAllName() {

        if (Environment.getExternalStorageState().
                equals(Environment.MEDIA_MOUNTED)) {
            File[] files = folder.listFiles();
            if (files == null) {
                Log.e("error", "空目录");
                return null;
            }
            List<String> s = new ArrayList<>();
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().endsWith(".shp"))
                    s.add(files[i].getName());
            }
            return s;
        } else {
            return null;
        }
    }

    void getPermission() {
        int permissionCheck1 = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionCheck2 = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck1 != PackageManager.PERMISSION_GRANTED || permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    124);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (requestCode == 124) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(getActivity().getApplicationContext(), "获取到权限了", Toast.LENGTH_SHORT).show();
//                mImgDir = new File(folder);//初始化File对象
//                File[] files = mImgDir.listFiles();//噩梦结束了吗？
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "搞不定啊", Toast.LENGTH_SHORT).show();
            }
        }
    }
}