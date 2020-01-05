package com.sdgm.map.ui.gallery;

import android.Manifest;
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

import com.sdgm.map.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {

    ListView listView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView= view.findViewById(R.id.listView);
        List<Layer> layers=new ArrayList<>();
//        getPermission(); //调试的时候要执行一次，以后可注释掉
        List<String> files=getFilesAllName("2017井");
        List<Layer> layerList= layers;
        if(files.size()>0) {
            for (int i = 0; i < files.size(); i++) {
                layerList.add(new Layer(files.get(i), files.get(i), false));
            }
        }
        LayerListViewAdapter adapter= new LayerListViewAdapter(getActivity(), layerList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((adapterView, view1, itemIndex, l) -> {
            Object itemObject = adapterView.getAdapter().getItem(itemIndex);
            Layer itemDto= (Layer) itemObject;

            Toast.makeText(getActivity().getApplicationContext(), "select item text : " + itemDto.getName(), Toast.LENGTH_SHORT).show();
        });
    }

    public static List<String> getFilesAllName(String folderName) {
        File sdDir = Environment.getExternalStorageDirectory();
        File path = new File(sdDir+File.separator +folderName.trim());
        if (Environment.getExternalStorageState().
                equals(Environment.MEDIA_MOUNTED)){
            File[] files=path.listFiles();
            if (files == null){
                Log.e("error","空目录");return null;
            }
            List<String> s = new ArrayList<>();
            for(int i =0;i<files.length;i++){
                s.add(files[i].getName());
            }
            return s;
        }else{
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
}