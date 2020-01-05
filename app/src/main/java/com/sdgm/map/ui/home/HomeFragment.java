package com.sdgm.map.ui.home;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ShapefileFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.io.RequestConfiguration;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.WebTiledLayer;

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sdgm.map.R;
import com.sdgm.map.TianDiTuMethodsClass;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class HomeFragment extends Fragment {


    private MapView mapView;
    private LocationDisplay mLocationDisplay;
    Map<String,FeatureLayer> featureLayers=new HashMap<>();



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
//                String message = String.format("Error in DataSourceStatusChangedListener: %s",
//                        dataSourceStatusChangedEvent.getSource().getLocationDataSource().getError().getMessage());
                String message="请打开手机GPS定位功能！";
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        });

        setupLocationDisplay();

        fab.setOnClickListener(
//                view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
                v -> setupLocationDisplay()
        );

        //接收添加图层广播
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager
                .getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("addLayer");
        intentFilter.addAction("deleteLayer");
        intentFilter.addAction("locateLayer");
        BroadcastReceiver br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String addLayerName = intent.getStringExtra("addLayerName");
                String deleteLayerName=intent.getStringExtra("deleteLayerName");
                String locateLayerName=intent.getStringExtra("locateLayerName");
                if(addLayerName!=null) {
                    FeatureLayer featureLayer=loadShapefile("/storage/emulated/0/maps/" + addLayerName);
                    featureLayers.put(addLayerName,featureLayer);
                }
                if(deleteLayerName!=null){
                    mapView.getMap().getOperationalLayers().remove(featureLayers.get(deleteLayerName));
                    featureLayers.remove(deleteLayerName);
                }
                if(locateLayerName!=null){
                    if(featureLayers.keySet().contains(locateLayerName)){
                        mapView.setViewpointGeometryAsync(featureLayers.get(locateLayerName).getFullExtent());
                    }
                }
            }
        };
        localBroadcastManager.registerReceiver(br, intentFilter);



    }


    private void setupLocationDisplay() {
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

    private FeatureLayer loadShapefile(String shpPath) {
        // 构建ShapefileFeatureTable，引入本地存储的shapefile文件
        ShapefileFeatureTable shapefileFeatureTable = new ShapefileFeatureTable(shpPath);
        shapefileFeatureTable.loadAsync();
        // 构建featureLayerr
        FeatureLayer mFeatureLayer = new FeatureLayer(shapefileFeatureTable);
        // 设置Shapefile文件的渲染方式
        FeatureTable table=mFeatureLayer.getFeatureTable();
        if(table.getGeometryType()== GeometryType.MULTIPOINT || table.getGeometryType()== GeometryType.POINT){
            SimpleMarkerSymbol markerSymbol=new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.DIAMOND,Color.RED,10.0f);
            mFeatureLayer.setRenderer(new SimpleRenderer(markerSymbol));
        }else if(table.getGeometryType()== GeometryType.POLYGON ){
            SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 1.0f);
            SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.YELLOW, lineSymbol);
            SimpleRenderer renderer = new SimpleRenderer(fillSymbol);
            mFeatureLayer.setRenderer(renderer);
            mFeatureLayer.setOpacity(0.5f);
        }else if(table.getGeometryType()== GeometryType.POLYLINE ){
            SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 1.0f);
            SimpleRenderer renderer = new SimpleRenderer(lineSymbol);
            mFeatureLayer.setRenderer(renderer);
        }else{

        }

        mapView.setViewpointGeometryAsync(mFeatureLayer.getFullExtent());
        // 添加到地图的业务图层组中

        mapView.getMap().getOperationalLayers().add(mFeatureLayer);
        queryBySelectFeaturesAsync(mFeatureLayer);
        return mFeatureLayer;
    }

    /**
     * 查询shp方式1:selectFeaturesAsync
     */
    private void queryBySelectFeaturesAsync(FeatureLayer mFeatureLayer) {
        mapView.setOnTouchListener(new DefaultMapViewOnTouchListener(getActivity(), mapView) {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {

                mFeatureLayer.clearSelection();

                final Point clickPoint = mapView.screenToLocation(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY())));
                int tolerance = 1;
                double mapTolerance = tolerance * mapView.getUnitsPerDensityIndependentPixel();
                SpatialReference spatialReference = mapView.getSpatialReference();
                Envelope envelope = new Envelope(clickPoint.getX() - mapTolerance, clickPoint.getY() - mapTolerance,
                        clickPoint.getX() + mapTolerance, clickPoint.getY() + mapTolerance, spatialReference);
                QueryParameters query = new QueryParameters();
                query.setGeometry(envelope);
                query.setSpatialRelationship(QueryParameters.SpatialRelationship.WITHIN);
                final ListenableFuture<FeatureQueryResult> future = mFeatureLayer.selectFeaturesAsync(query, FeatureLayer.SelectionMode.NEW);
                future.addDoneListener(() -> {
                    try {
                        FeatureQueryResult result = future.get();
                        //mFeatureLayer.getFeatureTable().deleteFeaturesAsync(result);
                        Iterator<Feature> iterator = result.iterator();

                        int counter = 0;
                        while (iterator.hasNext()) {
                            counter++;
                            Feature feature = iterator.next();

                            Map<String, Object> attributes = feature.getAttributes();
                            for (String key : attributes.keySet()) {
                                Log.e("xyh" + key, String.valueOf(attributes.get(key)));
                            }

                            //高亮显示选中区域
                            mFeatureLayer.selectFeature(feature);
                            Geometry geometry = feature.getGeometry();
                            mapView.setViewpointGeometryAsync(geometry.getExtent());

                            //也可以通过添加graphic高亮显示选中区域
                            //
                            //                                SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 3);
                            //                                SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.RED, lineSymbol);
                            //
                            //                                if (mGraphicsOverlay != null) {
                            //                                    ListenableList<Graphic> graphics = mGraphicsOverlay.getGraphics();
                            //                                    if (graphics.size() > 0) {
                            //                                        graphics.removeAll(graphics);
                            //                                    }
                            //                                }
                            //                                Graphic graphic = new Graphic(geometry, fillSymbol);
                            //                                mGraphicsOverlay.getGraphics().add(graphic);
                        }

                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                });
                return super.onSingleTapConfirmed(e);
            }
        });
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