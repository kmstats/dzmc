package com.echo.dzmc;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ExpandableAdapter extends BaseExpandableListAdapter {
	
	List<unit> groupArray;
	
	List<List<unit>> childArray;
	
	Activity activity;
	public DbAdapter db;
	
	public ExpandableAdapter(Activity a,DbAdapter da,Long uid){
		activity = a;
		db = da;
		groupArray = new ArrayList<unit>();
		childArray  = new ArrayList<List<unit>>();
		
		Cursor c = db.getUnitByPid(uid);
		
		int i = 0;
		while(c.moveToNext()){
			unit u = new unit(c.getString(0),c.getLong(1));
			groupArray.add(u);
			
			Cursor cc = db.getUnitByPid(u.id);
			List<unit> l = new ArrayList<unit>();
			while(cc.moveToNext()){
				unit su = new unit(cc.getString(0),cc.getLong(1));
				l.add(su);
			}
			childArray.add(i,l);
			i++;
		}
	}
	
	@Override
	public int getGroupCount() {
		return groupArray.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return childArray.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groupArray.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return childArray.get(groupPosition).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return ((unit)getGroup(groupPosition)).id;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return ((unit)getChild(groupPosition,childPosition)).id;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		unit u = groupArray.get(groupPosition);
		
		LayoutInflater inflater = activity.getLayoutInflater();
		View v = inflater.inflate(R.layout.row_view,null);
		TextView tv = (TextView)v.findViewById(R.id.tvName);
		tv.setText(" "+u.name);
		
		ImageView iv = (ImageView)v.findViewById(R.id.imageView1);
		if (isExpanded){
			iv.setImageResource(R.drawable.ic_up_arrow);
		}else{
			iv.setImageResource(R.drawable.ic_down_arrow);
		}
		
		return v;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		unit u = childArray.get(groupPosition).get(childPosition);
		
		TextView v = getTextView();
		v.setText(u.name);
		
		return v;
	}

	TextView getTextView() {
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT, 64);
        TextView textView = new TextView(activity);
        textView.setLayoutParams(lp);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setPadding(36, 0, 0, 0);
        textView.setTextSize(14);
        textView.setSingleLine(true);
        textView.setBackgroundColor(Color.WHITE);
        textView.setTypeface(null, Typeface.BOLD);
        
        return textView;
    }
	
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}
