package com.sdgm.map.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import com.esri.arcgisruntime.geometry.AreaUnit;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.geometry.LinearUnit;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.io.RequestConfiguration;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.WebTiledLayer;

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.SketchCreationMode;
import com.esri.arcgisruntime.mapping.view.SketchEditor;
import com.esri.arcgisruntime.mapping.view.SketchGeometryChangedEvent;
import com.esri.arcgisruntime.mapping.view.SketchGeometryChangedListener;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sdgm.map.R;
import com.sdgm.map.TianDiTuMethodsClass;


import java.io.File;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.esri.arcgisruntime.mapping.view.SketchCreationMode.POLYGON;

public class HomeFragment extends Fragment {


    private MapView mapView;
    private LocationDisplay mLocationDisplay;
    Map<String, FeatureLayer> featureLayers = new HashMap<>();
    private Callout mCallout;
    private SketchEditor mSketchEditor = new SketchEditor();
    SketchGeometryChangedListener sketchGeometryChangedListener;
    private static final HashMap<String, String> attrMap = new HashMap<String, String>();



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        return root;
    }

    private void initAtrrMap(){
        attrMap.put("MIAN_JI","面积");
        attrMap.put("DI_LEI","地类");
        attrMap.put("GLLX","土地管理类型");
        attrMap.put("SHENG","省（区、市）");
        attrMap.put("XIAN","县（市、旗）");
        attrMap.put("XIANG","乡");
        attrMap.put("CUN","村");
        attrMap.put("LIN_YE_JU","林业局（场）");
        attrMap.put("LIN_CHANG","林场（分场）");
        attrMap.put("LIN_BAN","林班");
        attrMap.put("XIAO_BAN","图斑（小班）");
        attrMap.put("DI_MAO","地貌");
        attrMap.put("PO_XIANG","坡向");
        attrMap.put("PO_WEI","坡位");
        attrMap.put("PO_DU","坡度");
        attrMap.put("KE_JI_DU","交通区位");
        attrMap.put("TU_RANG_LX","土壤类型（名称）");
        attrMap.put("TU_CENG_HD","土层厚度");
        attrMap.put("LD_QS","土地权属");
        attrMap.put("LIN_ZHONG","林种");
        attrMap.put("QI_YUAN","起源");
        attrMap.put("SEN_LIN_LB","森林类别");
        attrMap.put("SHI_QUAN_D","事权等级");
        attrMap.put("GJGYL_BHDJ","国家级公益林保护等级");
        attrMap.put("G_CHENG_LB","工程类别");
        attrMap.put("LING_ZU","龄组");
        attrMap.put("YU_BI_DU","郁闭度/覆盖度");
        attrMap.put("YOU_SHI_SZ","优势树种");
        attrMap.put("PINGJUN_XJ","平均胸径");
        attrMap.put("HUO_LMGQXJ","公顷蓄积（活立木）");
        attrMap.put("MEI_GQ_ZS","每公顷株数");
        attrMap.put("TD_TH_LX","土地退化类型");
        attrMap.put("DISPE","灾害类型");
        attrMap.put("DISASTER_C","灾害等级");
        attrMap.put("ZL_DJ","林地质量等级");
        attrMap.put("LD_KD","林带宽度");
        attrMap.put("LD_CD","林带长度");
        attrMap.put("BCLD","是否为补充林地");
        attrMap.put("BH_DJ","林地保护等级");
        attrMap.put("LYFQ","林地功能分区");
        attrMap.put("QYKZ","主体功能区");
        attrMap.put("BHYY","林地变化原因");
        attrMap.put("BHND","变化年度");
        attrMap.put("Remarks","说明");
        attrMap.put("XJ_JYDW","县级经营单位");
        attrMap.put("STQW","生态区位");
        attrMap.put("GYLGHFS","公益林管护方式");
        attrMap.put("SF_BC","是否纳入补偿");
        attrMap.put("LD_S_YOU_Q","林地所有权");
        attrMap.put("LM_S_YOU_Q","林木所有权");
        attrMap.put("LM_S_YONG_Q","林木使用权");
        attrMap.put("FZZ_HD","腐殖质厚度");
        attrMap.put("TU_RANG_ZD","土壤质地");
        attrMap.put("YZH_CD","盐渍化程度");
        attrMap.put("DXSMS","地下水埋深");
        attrMap.put("LX_GMMC","林下灌木名称");
        attrMap.put("LX_GMGD","林下灌木盖度");
        attrMap.put("CB_MC","草本名称");
        attrMap.put("CB_GD","草本盖度");
        attrMap.put("ZB_FB","植被分布");
        attrMap.put("LFSZZK","林分生长状况");
        attrMap.put("QUN_LUO_JG","群落结构");
        attrMap.put("SZ_ZC","树种组成");
        attrMap.put("SZJG","树种结构");
        attrMap.put("ZLND","造林年度");
        attrMap.put("PJ_NL","平均年龄");
        attrMap.put("PJ_SG","平均树高");
        attrMap.put("XB_XJ","小班蓄积");
        attrMap.put("JJLCL","经济林（竹林）年产量");
        attrMap.put("SF_LB","是否林保范围");
        attrMap.put("SS_SZ","散生树种");
        attrMap.put("SS_ZS","散生株数");
        attrMap.put("SZ_XJ","散生蓄积");
        attrMap.put("BH_YJ","变化依据");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        super.onViewCreated(view, savedInstanceState);
        mapView = getView().findViewById(R.id.mapView);
        ArcGISMap map = new ArcGISMap();
        mapView.setMap(map);

//        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        LinearLayout groupPollingAddress = (LinearLayout)inflater.inflate(R.layout.nav_header_main, null);

//        TextView userTextView= groupPollingAddress.findViewById(R.id.app_user);
//        String user = getActivity().getIntent().getStringExtra("user");
//        userTextView.setText(user);

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
                String message = "请打开手机GPS定位功能！";
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        });

        initAtrrMap();

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
        intentFilter.addAction("hideAttributes");
        intentFilter.addAction("measureArea");
        BroadcastReceiver br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String addLayerName = intent.getStringExtra("addLayerName");
                String deleteLayerName = intent.getStringExtra("deleteLayerName");
                String locateLayerName = intent.getStringExtra("locateLayerName");
                String hideAttributes = intent.getStringExtra("hideAttributes");
                String measureArea = intent.getStringExtra("measureArea");
                if (addLayerName != null) {
                    FeatureLayer featureLayer = loadShapefile(ContextCompat.getExternalFilesDirs(getActivity(), null)[0] + File.separator + "maps" + File.separator + addLayerName);
                    featureLayers.put(addLayerName, featureLayer);
                }
                if (deleteLayerName != null) {
                    mapView.getMap().getOperationalLayers().remove(featureLayers.get(deleteLayerName));
                    featureLayers.remove(deleteLayerName);
                }
                if (locateLayerName != null) {
                    if (featureLayers.keySet().contains(locateLayerName)) {
                        mapView.setViewpointGeometryAsync(featureLayers.get(locateLayerName).getFullExtent());
                    }
                }
                if (hideAttributes != null) {
                    if (mCallout.isShowing()) {
                        mCallout.dismiss();
                    }
                }
                if (measureArea != null) {
                    if (sketchGeometryChangedListener != null) {
                        mSketchEditor.removeGeometryChangedListener(sketchGeometryChangedListener);
                    }
                    // add a graphic of point, multipoint, polyline and polygon.

                    mapView.setSketchEditor(mSketchEditor);

                    sketchGeometryChangedListener = sketchGeometryChangedEvent -> {
                        switch (mSketchEditor.getSketchCreationMode()) {
                            case POLYLINE:
                                //在此进行polyline的计算
                                break;
                            case POLYGON:
                                Geometry polygonGeometry = mSketchEditor.getGeometry();
                                if (polygonGeometry != null) {
                                    Polygon polygon = (Polygon) GeometryEngine.project(polygonGeometry, SpatialReference.create(3857));
                                    if (mCallout.isShowing()) {
                                        mCallout.dismiss();
                                    }

                                    double areaValue = GeometryEngine.area(polygon);
                                    if (areaValue != 0.0) {
                                        TextView calloutContent = new TextView(getActivity());
                                        calloutContent.setTextColor(Color.BLACK);
                                        calloutContent.setSingleLine(false);
                                        calloutContent.setVerticalScrollBarEnabled(true);
                                        calloutContent.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
                                        calloutContent.setMovementMethod(new ScrollingMovementMethod());
                                        calloutContent.setLines(1);
                                        calloutContent.append("面积" + " : " + formatDouble(Math.abs(areaValue) / 10000) + "公顷" + "\n");
                                        mCallout.setLocation(polygon.getExtent().getCenter());
                                        mCallout.setContent(calloutContent);
                                        mCallout.show();
                                    }
                                }
                                break;
                            default:
                                break;
                        }
                    };

                    mSketchEditor.addGeometryChangedListener(sketchGeometryChangedListener);
                    mSketchEditor.start(POLYGON);

                }
            }
        };
        localBroadcastManager.registerReceiver(br, intentFilter);

        // get the callout that shows attributes
        mCallout = mapView.getCallout();

    }

    private String formatDouble(double s) {
        DecimalFormat fmt = new DecimalFormat("##0.000");
        return fmt.format(s);
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
        FeatureTable table = mFeatureLayer.getFeatureTable();
        if (table.getGeometryType() == GeometryType.MULTIPOINT || table.getGeometryType() == GeometryType.POINT) {
            SimpleMarkerSymbol markerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.DIAMOND, Color.RED, 10.0f);
            mFeatureLayer.setRenderer(new SimpleRenderer(markerSymbol));
        } else if (table.getGeometryType() == GeometryType.POLYGON) {
            SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 1.0f);
            SimpleFillSymbol fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, Color.YELLOW, lineSymbol);
            SimpleRenderer renderer = new SimpleRenderer(fillSymbol);
            mFeatureLayer.setRenderer(renderer);
            mFeatureLayer.setOpacity(0.5f);
        } else if (table.getGeometryType() == GeometryType.POLYLINE) {
            SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.RED, 1.0f);
            SimpleRenderer renderer = new SimpleRenderer(lineSymbol);
            mFeatureLayer.setRenderer(renderer);
        } else {

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
    @SuppressLint("ClickableViewAccessibility")
    private void queryBySelectFeaturesAsync(FeatureLayer mFeatureLayer) {
        mapView.setOnTouchListener(new DefaultMapViewOnTouchListener(getActivity(), mapView) {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {

                if (mCallout.isShowing()) {
                    mCallout.dismiss();
                }

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
                        // create a TextView to display field values
                        TextView calloutContent = new TextView(getActivity());
                        calloutContent.setTextColor(Color.BLACK);
                        calloutContent.setSingleLine(false);
                        calloutContent.setVerticalScrollBarEnabled(true);
                        calloutContent.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
                        calloutContent.setMovementMethod(new ScrollingMovementMethod());

                        FeatureQueryResult result = future.get();
                        //mFeatureLayer.getFeatureTable().deleteFeaturesAsync(result);
                        Iterator<Feature> iterator = result.iterator();

                        int counter = 0;
                        while (iterator.hasNext()) {
                            counter++;
                            Feature feature = iterator.next();
                            Map<String, String> attrs = new HashMap<String, String>();
                            Map<String, Object> attributes = feature.getAttributes();
                            for (String key : attributes.keySet()) {
                                String k = attrMap.get(key);
                                String v = String.valueOf(attributes.get(key));
                                Log.e("xyh" + k, v);
                                if (!v.isEmpty() && k!=null) {
                                    attrs.put(k, v);
                                }
                            }
                            calloutContent.setLines(attrs.size());
                            for (String key : attrs.keySet()) {
                                String key1 = key;
                                String v1 = attrs.get(key);
                                calloutContent.append(key1 + " : " + v1 + "\n");
                            }


                            //高亮显示选中区域
                            mFeatureLayer.selectFeature(feature);
                            Geometry geometry = feature.getGeometry();
                            mapView.setViewpointGeometryAsync(geometry.getExtent());

                            // show CallOut
                            mCallout.setLocation(clickPoint);
                            mCallout.setContent(calloutContent);
                            mCallout.show();

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
    public void onPause() {

        if (mapView != null) {
            mapView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mapView != null) {
            mapView.dispose();
        }
        super.onDestroy();
    }
}