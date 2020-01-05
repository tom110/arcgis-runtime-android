package com.sdgm.map.ui.gallery;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.sdgm.map.R;

import java.util.List;

public class LayerListViewAdapter extends ArrayAdapter<Layer> {
    Context context;
    List<Layer> modelItems;
    @SuppressWarnings("unchecked")

    public LayerListViewAdapter(Context context, List<Layer> resource)
    {
        super(context, R.layout.content_listview,resource);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.modelItems = resource;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        TextView textView;
        CheckBox checkBox;

        Layer layer= this.getItem(position);
        // TODO Auto-generated method stub
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        if(convertView==null){
            convertView = inflater.inflate(R.layout.content_listview, null);
            textView = convertView.findViewById(R.id.textView);
            checkBox = convertView.findViewById(R.id.checkbox);

            convertView.setTag(new LayerListViewHolder(textView,checkBox));

            // If CheckBox is toggled, update the planet it is tagged with.
            checkBox.setOnClickListener(v -> {
                CheckBox cb = (CheckBox) v ;
                Layer layer1 = (Layer) cb.getTag();
                layer1.setSelected( cb.isChecked() );
                Toast.makeText(context.getApplicationContext(), "checkbox item text : " + layer1.getName(), Toast.LENGTH_SHORT).show();
            });
        }else{
            LayerListViewHolder viewHolder = (LayerListViewHolder) convertView.getTag();
            checkBox = viewHolder.getCheckBox() ;
            textView = viewHolder.getTextView() ;
        }
        checkBox.setTag(layer);

        checkBox.setChecked(layer.isSelected());
        textView.setText(layer.getName());

        return convertView;
    }
}
