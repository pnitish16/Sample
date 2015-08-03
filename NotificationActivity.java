package com.momslist.activity;

import java.security.acl.NotOwnerException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.webservices.DataManager;
import com.android.webservices.ParseManager;
import com.android.webservices.WebResponseListener;
import com.android.webservices.WebServiceData;
import com.android.webservices.WebServiceManager;
import com.android.webservices.WebServiceManager.REQUEST_TYPE;
import com.google.android.gms.drive.internal.ad;
import com.momlist.entity.Notification;
import com.momlist.interfaces.onReadListener;
import com.momslist.R;
import com.momslist.adapter.MyOrderListAdapter;
import com.momslist.adapter.NotificationAdapter;
import com.momslist.widget.CustomDialogManager;
import com.utils.BadgeView;
import com.utils.Helper;
import com.utils.HelperHttp;

public class NotificationActivity extends SlidingBaseActivity implements OnClickListener,onReadListener {
	
	private DataManager datamgr;
	private Context context;
	private ArrayList<Notification> alNotifications,templist;
	private int notification_count;
	private NotificationAdapter adapter;
	private ListView lvNotification;
	private int page_index = 1, page_size = 10;
	private ImageView ivRightMenu,ivRightMenu1,ivRightMenu2;
	private int cartCount;
	private BadgeView badge;
	private boolean deleteActivated = false;
	private ArrayList<String> alDeleteNotifications;
	private String deletionCSV ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_notification);
		context = NotificationActivity.this;
		datamgr = DataManager.getInstance();
		
		//Action Bar View Settings
		
		((TextView) findViewById(R.id.tvActionBarTitle)).setText("notifications");
		((TextView) findViewById(R.id.tvActionBarTitle)).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		
		lvNotification = (ListView) findViewById(R.id.lvNotification);
		
		setCartBadge();
		customMenu.setMenuId(6);
				
		alNotifications = new ArrayList<Notification>();
		
		//Adapter Initialization with empty array
		
		adapter = new NotificationAdapter(context, R.layout.list_item_notification, alNotifications);
		lvNotification.setAdapter(adapter);
		lvNotification.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		
		lvNotification.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				adapter.toggleSelection(position);
			}
		});
		
		//Setting the onMultiChoice Mode Adapter.
		
//		lvNotification.setMultiChoiceModeListener(new MultiChoiceModeListener() {
//
//			@Override
//			public void onItemCheckedStateChanged(ActionMode mode,
//					int position, long id, boolean checked) {
//				// Capture total checked items
//				final int checkedCount = lvNotification.getCheckedItemCount();
//				// Set the CAB title according to total checked items
//				mode.setTitleOptionalHint(false);
//				mode.invalidate();
////				mode.setTitle(checkedCount + " Selected");
//				// Calls toggleSelection method from ListViewAdapter Class
//				adapter.toggleSelection(position);
//			}
//
//			@Override
//			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
////				switch (item.getItemId()) {
////				case R.id.spam_user:
////					// Calls getSelectedIds method from ListViewAdapter Class
////					SparseBooleanArray selected = adapter
////							.getSelectedIds();
////					// Captures all selected ids with a loop
////					for (int i = (selected.size() - 1); i >= 0; i--) {
////						if (selected.valueAt(i)) {
////							Notification selecteditem = adapter
////									.getItem(selected.keyAt(i));
////							// Remove selected items following the ids
////							adapter.remove(selecteditem);
////						}
////					}
////					// Close CAB
////					mode.finish();
////					return true;
////				default:
////					return false;
////				}
//				
//				mode.finish();
//				return true;
//			}
//
//			@Override
//			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
////				mode.getMenuInflater().inflate(R.menu.spam_user, menu);
//				Helper.showToast("Delete Button Activated", context);
//				deleteActivated = true;
//				return true;
//			}
//
//			@Override
//			public void onDestroyActionMode(ActionMode mode) {
//				// TODO Auto-generated method stub
//				adapter.removeSelection();
//			}
//
//			@Override
//			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//				// TODO Auto-generated method stub
//				return false;
//			}
//		});
		
		getUnreadNotifications();
		getNotifications();
	}
	
	
	public void getUnreadNotifications()
	{
		JSONObject reqobj=new JSONObject();
		try 
		{
			reqobj.put("mem_id",datamgr.getUser().getId());
		} 
		catch (JSONException e1) 
		{
			e1.printStackTrace();
		}
		
		WebServiceData webServiceData=new WebServiceData();
		webServiceData.setUrl("get_unreadnotificationcount.php");
		webServiceData.setData(reqobj.toString());
		List<NameValuePair>params=new ArrayList<NameValuePair>();
		webServiceData.setParams(params);
		WebServiceManager webServiceManager=new WebServiceManager(context);
		webServiceManager.setRequestType(REQUEST_TYPE.POST);
		webServiceManager.setWebResponseListener(new WebResponseListener() {
			
			@Override
			public void onSuccess(String result) {

				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(result);
					if (jsonObject != null) {
						if (!(jsonObject.optString("status").equals("0"))) {
							
							
						} else {
							// get error details
							Helper.showToast(jsonObject.optString("message").toLowerCase(), context);
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			@Override
			public void onFailed() {
				// TODO Auto-generated method stub
				
			}
		});
		
		if(HelperHttp.isNetworkAvailable(context))
		{
			webServiceManager.execute(webServiceData);
		}
		else
		{
			HelperHttp.showNoInternetDialog(context);
		}
	}
	
	public void getNotifications()
	{
		JSONObject reqobj=new JSONObject();
		try 
		{
			reqobj.put("mem_id",datamgr.getUser().getId());
			reqobj.put("page_index",page_index);
			reqobj.put("page_size",page_size);
		} 
		catch (JSONException e1) 
		{
			e1.printStackTrace();
		}
		
		WebServiceData webServiceData=new WebServiceData();
		webServiceData.setUrl("notifcation.php");
		webServiceData.setData(reqobj.toString());
		List<NameValuePair>params=new ArrayList<NameValuePair>();
		webServiceData.setParams(params);
		WebServiceManager webServiceManager=new WebServiceManager(context);
		webServiceManager.setRequestType(REQUEST_TYPE.POST);
		webServiceManager.setWebResponseListener(new WebResponseListener() {
			
			@Override
			public void onSuccess(String result) {

				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(result);
					if (jsonObject != null) {
						if (!(jsonObject.optString("status").equals("0"))) {
							
							alNotifications = ParseManager.parseNotification(result);
							adapter = new NotificationAdapter(context, R.layout.list_item_notification, alNotifications);
							lvNotification.setAdapter(adapter);
							adapter.setListener(NotificationActivity.this);
							adapter.notifyDataSetChanged();
							
						} else {
							// get error details
							Helper.showToast(jsonObject.optString("message").toLowerCase(), context);
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			@Override
			public void onFailed() {
				// TODO Auto-generated method stub
				
			}
		});
		
		if(HelperHttp.isNetworkAvailable(context))
		{
			webServiceManager.execute(webServiceData);
		}
		else
		{
			HelperHttp.showNoInternetDialog(context);
		}
	}
	
	private void setCartBadge() {
		
		ivRightIcon.setVisibility(View.VISIBLE);
		ivRightIcon1.setVisibility(View.VISIBLE);
		ivRightIcon2.setVisibility(View.VISIBLE);
		
		//Badge for cart
		cartCount = DataManager.getInstance().getUser().getCartCount();
		if(cartCount>0)
		{
			badge = new BadgeView(context,ivRightIcon);
			badge.setText(""+cartCount);
			badge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
			badge.setBadgeMargin(0,0);
			badge.show();
			
			ivRightIcon.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					Intent intent=new Intent(NotificationActivity.this, CartDetailActivity.class);
					startActivity(intent);
				}
			});
			
		}
		
		ivRightIcon.setImageResource(R.drawable.cart_icon);
		ivRightIcon1.setImageResource(R.drawable.done_icon);
		ivRightIcon2.setImageResource(R.drawable.trash_icon);
		ivRightIcon1.setOnClickListener(this);
		ivRightIcon2.setOnClickListener(this);
		
	}
	
	public void delete_notification()
		{
			JSONObject reqobj=new JSONObject();
			
//			push_message_id(comma sepatred values for multiple delete)
			try 
			{
				reqobj.put("push_message_id",datamgr.getUser().getId());
			} 
			catch (JSONException e1) 
			{
				e1.printStackTrace();
			}
			
			WebServiceData webServiceData=new WebServiceData();
			webServiceData.setUrl("delete_notifcation.php");
			webServiceData.setData(reqobj.toString());
			List<NameValuePair>params=new ArrayList<NameValuePair>();
			webServiceData.setParams(params);
			WebServiceManager webServiceManager=new WebServiceManager(context);
			webServiceManager.setRequestType(REQUEST_TYPE.POST);
			webServiceManager.setWebResponseListener(new WebResponseListener() {
				
				@Override
				public void onSuccess(String result) {

					JSONObject jsonObject;
					try {
						jsonObject = new JSONObject(result);
						if (jsonObject != null) {
							if (!(jsonObject.optString("status").equals("0"))) {
								Helper.showToast(jsonObject.optString("message").toLowerCase(), context);
								
							} else {
								// get error details
								Helper.showToast(jsonObject.optString("message").toLowerCase(), context);
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				@Override
				public void onFailed() {
					// TODO Auto-generated method stub
					
				}
			});
			
			if(HelperHttp.isNetworkAvailable(context))
			{
				webServiceManager.execute(webServiceData);
			}
			else
			{
				HelperHttp.showNoInternetDialog(context);
			}
		}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);

		//Cart Icon
		if(v == ivRightIcon)
		{
			Intent cartIntent = new Intent(NotificationActivity.this,CartDetailActivity.class);
			startActivity(cartIntent);
		}
		
		//Select All Icon
		else if(v == ivRightIcon1)
		{
			SparseBooleanArray selected = adapter.getSelectedIds();
			 
			if(selected.size()==adapter.getCount())
				adapter.unSelectAll();
			else
				adapter.selectAll();
		}
		
		//Delete Selected
		
		else if(v == ivRightIcon2)
		{
//			Helper.showToast("items could be deleted", context);
//			new DeleteNotification().execute();
			
			SparseBooleanArray selected = adapter.getSelectedIds();
			// Captures all selected ids with a loop
			
			if(selected.size()>0)
			{
				alDeleteNotifications = new ArrayList<String>();
				
			for (int i = (selected.size() - 1); i >= 0; i--) 
			{
				if (selected.valueAt(i)) 
				{
					Notification selecteditem = adapter.getItem(selected.keyAt(i));
					
					alDeleteNotifications.add(""+selecteditem.getPush_message_id());
					// Remove selected items following the ids
					adapter.remove(adapter.getItem(selected.keyAt(i)));
					selected.delete(i);
				}
			}
			
//			deleteNotifications();
			adapter.unSelectAll();
			
			}
			else
			{
				Helper.showToast("select atleast one to delete", context);
			}
			
		}
	}


	@Override
	public void onRead(int position,boolean isRead) {
		// TODO Auto-generated method stub
		if(isRead)
		{
			Helper.showToast("message already read", context);
		}
		else
		{
			CustomDialogManager.showOkDialog(context, "Notification", alNotifications.get(position).getPush_message());
			alNotifications.get(position).setIs_read(1);
		}
	}
	
	public void deleteNotifications()
	{
		JSONObject reqobj=new JSONObject();
		
		deletionCSV = alDeleteNotifications.toString().replace("[", "{");
		deletionCSV = deletionCSV.replace("]", "}");
		
		try 
		{
//			push_message_id(comma sepatred values for multiple delete)
			reqobj.put("push_message_id",deletionCSV);
		} 
		catch (JSONException e1) 
		{
			e1.printStackTrace();
		}
		
		WebServiceData webServiceData=new WebServiceData();
		webServiceData.setUrl("delete_notifcation.php");
		webServiceData.setData(reqobj.toString());
		List<NameValuePair>params=new ArrayList<NameValuePair>();
		webServiceData.setParams(params);
		WebServiceManager webServiceManager=new WebServiceManager(context);
		webServiceManager.setRequestType(REQUEST_TYPE.POST);
		webServiceManager.setWebResponseListener(new WebResponseListener() {
			
			@Override
			public void onSuccess(String result) {

				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(result);
					if (jsonObject != null) {
						if (!(jsonObject.optString("status").equals("0"))) {
							
							Helper.showToast(jsonObject.optString("message").toLowerCase(), context);
//							alMyOrders = ParseManager.parseMyOrders(result);
//							
//							adapter = new MyOrderListAdapter(context, R.layout.list_item_myorder, alMyOrders);
//							lvOrders.setAdapter(adapter);
							
						} else {
							// get error details
							Helper.showToast(jsonObject.optString("message").toLowerCase(), context);
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			@Override
			public void onFailed() {
				// TODO Auto-generated method stub
				
			}
		});
		
		if(HelperHttp.isNetworkAvailable(context))
		{
			webServiceManager.execute(webServiceData);
		}
		else
		{
			HelperHttp.showNoInternetDialog(context);
		}
	}
	
	
	public class DeleteNotification extends AsyncTask<Void,Void,Void>
	{

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			SparseBooleanArray selected = adapter.getSelectedIds();
			// Captures all selected ids with a loop
			
			if(selected.size()>0)
			{
				alDeleteNotifications = new ArrayList<String>();
				
			for (int i = (selected.size() - 1); i >= 0; i--) 
			{
				if (selected.valueAt(i)) 
				{
					Notification selecteditem = adapter.getItem(selected.keyAt(i));
					
					alDeleteNotifications.add(""+selecteditem.getPush_message_id());
					// Remove selected items following the ids
					adapter.remove(adapter.getItem(selected.keyAt(i)));
					selected.delete(i);
				}
			}
			
//			deleteNotifications();
			adapter.unSelectAll();
			
			Helper.Log("Deletelist", alDeleteNotifications.toString().replace("[", ""));
//			
			}
			else
			{
				Helper.showToast("select atleast one to delete", context);
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
	}
}
