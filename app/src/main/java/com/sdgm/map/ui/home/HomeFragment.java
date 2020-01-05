package com.sdgm.map.ui.home;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import com.esri.arcgisruntime.io.RequestConfiguration;
import com.esri.arcgisruntime.layers.WebTiledLayer;

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sdgm.map.R;
import com.sdgm.map.TianDiTuMethodsClass;


import java.util.Arrays;
import java.util.List;

import static android.content.ContentValues.TAG;

public class HomeFragment extends Fragment {

    private static final List<String> SubDomain = Arrays.asList(new String[]{"t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7"});

    private MapView mapView;
    private LocationDisplay mLocationDisplay;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        super.onViewCreated(view, savedInstanceState);
        mapView =  getView().findViewById(R.id.mapView);
        ArcGISMap map = new ArcGISMap();
        mapView.setMap(map);

        WebTiledLayer webTiledLayer = TianDiTuMethodsClass.CreateTianDiTuTiledLayer(TianDiTuMethodsClass.LayerType.TIANDITU_IMAGE_2000);
        WebTiledLayer webTiledLayer1 = TianDiTuMethodsClass.CreateTianDiTuTiledLayer(TianDiTuMethodsClass.LayerType.TIANDITU_IMAGE_ANNOTATION_CHINESE_2000);

        //注意：在100.2.0之后要设置RequestConfiguration
        RequestConfiguration requestConfiguration = new RequestConfiguration();
        requestConfiguration.getHeaders().put("referer", "http://www.arcgis.com");
        webTiledLayer.setRequestConfiguration(requestConfiguration);
        webTiledLayer1.setRequestConfiguration(requestConfiguration);
        webTiledLayer.loadAsync();
        webTiledLayer1.loadAsync();
        Basemap basemap = new Basemap(webTiledLayer);
        basemap.getBaseLayers().add(webTiledLayer1);
        mapView.getMap().setBasemap(basemap);

        mLocationDisplay = mapView.getLocationDisplay();

        mLocationDisplay.addDataSourceStatusChangedListener(dataSourceStatusChangedEvent -> {
            if (dataSourceStatusChangedEvent.isStarted() || dataSourceStatusChangedEvent.getError() == null) {
                return;
            }

            int requestPermissionsCode = 2;
            String[] requestPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

            if (!(ContextCompat.checkSelfPermission(getActivity(), requestPermissions[0]) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(getActivity(), requestPermissions[1]) == PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(getActivity(), requestPermissions, requestPermissionsCode);
            } else {
                String message = String.format("Error in DataSourceStatusChangedListener: %s",
                        dataSourceStatusChangedEvent.getSource().getLocationDataSource().getError().getMessage());
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        });

        setupLocationDisplay();

        fab.setOnClickListener(
//                view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setupLocationDisplay();
                    }
                }
        );
    }


    public void setupLocationDisplay() {
        mLocationDisplay = mapView.getLocationDisplay();
        mLocationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.NAVIGATION);
        mLocationDisplay.startAsync();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mLocationDisplay.startAsync();
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ondestory");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView: ondestoryview");
    }
}