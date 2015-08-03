package com.momslist.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.provider.ContactsContract.Contacts.Data;
import android.util.DisplayMetrics;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.webservices.DataManager;
import com.momlist.entity.CartProductItem;
import com.momlist.entity.MyOrderItem;
import com.momlist.entity.Notification;
import com.momlist.entity.ShipMethodItem;
import com.momlist.interfaces.LoadMoreListener;
import com.momlist.interfaces.QuantityListener;
import com.momlist.interfaces.onReadListener;
import com.momslist.R;
import com.momslist.widget.CustomEdittextWithDrawable;
import com.momslist.widget.CustomEdittextWithDrawable.DrawableClickListener;
import com.momslist.widget.CustomEdittextWithDrawable.DrawableClickListener.DrawablePosition;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.utils.Utils.DatePickerFragment;

public class NotificationAdapter extends ArrayAdapter<Notification> 
{
	// String color1="#50FFF5F4";//
	String color1 = "#ffffff";
	// String color2="#50E8E8E8";
	String color2 = "#59e8e8e8";
	// lazy loading
	DisplayImageOptions options;
	ImageLoader imageLoader;
	ArrayList<Notification> data;
	onReadListener listener;

	LoadMoreListener loadMoreListener;
	boolean loadMoreEnabled=true;
	boolean isGridView=true;
	private int layoutResID;
	private SparseBooleanArray mSelectedItemsIds;
	
	int width;
	@SuppressWarnings("deprecation")
	
	public NotificationAdapter(Context context, int resource,ArrayList<Notification> objects) 
	{
		super(context, resource, objects);
		data = objects;
		this.layoutResID = resource;
		mSelectedItemsIds = new SparseBooleanArray();
	}
	
	@Override
	public Notification getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
			ViewHolder viewHolder;
			View row = convertView;
			
			if (row == null) {
				
				viewHolder = new ViewHolder();
				row = LayoutInflater.from(getContext()).inflate(
						R.layout.list_item_notification, parent, false);
				
				viewHolder.tvPushMsg = (TextView) row.findViewById(R.id.tvPushMsg);
				viewHolder.tvPushMsgDate = (TextView) row.findViewById(R.id.tvPushMsgDate);
				viewHolder.ivChecked  = (ImageView) row.findViewById(R.id.ivChecked);
				row.setTag(viewHolder);
			}
			else
			{
				viewHolder = (ViewHolder) row.getTag();
			}
			
			Notification item = getItem(position);
			viewHolder.tvPushMsg.setText(getItem(position).getPush_message());
			viewHolder.tvPushMsgDate.setText(getItem(position).getPush_message_date());
			
			if(getItem(position).getIs_read() == 1)
				viewHolder.ivChecked.setImageResource(R.drawable.checked_box);
			else
				viewHolder.ivChecked.setImageResource(R.drawable.unchecked_box);
			
			
			if(position%2==0)
				row.setBackgroundColor(Color.parseColor("#FDD9D9"));
			else			
				row.setBackgroundColor(Color.parseColor("#FFFFFF"));
			
			if(mSelectedItemsIds.get(position))
				row.setBackgroundColor(Color.parseColor("#F68A8A"));
			
			viewHolder.ivChecked.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(listener != null)
					{
						if(getItem(position).getIs_read() == 1)
							listener.onRead(position,true);
						else
							listener.onRead(position,false);
					}
				}
			});
			
			return row;
	}
	
	private static class ViewHolder
	{
		public TextView tvPushMsg,tvPushMsgDate;
		public ImageView ivChecked;
	}
	
	public void setListener(onReadListener listener)
	{
		this.listener = listener;
	}
	
	@Override
	public void remove(Notification object) {
		// TODO Auto-generated method stub
		data.remove(object);
		notifyDataSetChanged();
	}
	
	public void toggleSelection(int position) {
		selectView(position, !mSelectedItemsIds.get(position));
	}

	public void removeSelection() {
		mSelectedItemsIds = new SparseBooleanArray();
		notifyDataSetChanged();
	}

	public void selectView(int position, boolean value) {
		if (value)
			mSelectedItemsIds.put(position, value);
		else
			mSelectedItemsIds.delete(position);
		notifyDataSetChanged();
	}
	
	public void selectAll()
	{
		for(int i=0;i<data.size();i++)
			mSelectedItemsIds.put(i, true);
		notifyDataSetChanged();
	}
	
	public void unSelectAll()
	{
		for(int i=0;i<data.size();i++)
			mSelectedItemsIds.delete(i);
		notifyDataSetChanged();
	}


	public int getSelectedCount() {
		return mSelectedItemsIds.size();
	}

	public SparseBooleanArray getSelectedIds() {
		return mSelectedItemsIds;
	}
}
