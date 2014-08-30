/**
 * 
 */
package com.principalmvl.lojackmykids.Adapters;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.principalmvl.lojackmykids.R;
import com.principalmvl.lojackmykids.models.KnownLocation;

/**
 * @author Vincent
 *
 */
public class KnownLocations extends BaseAdapter implements Adapter {

	private List<KnownLocation> ListKnownLocation;
	private Context context;

	public KnownLocations(Context context, List<KnownLocation> listknownLocation) {
		super();
		this.ListKnownLocation = listknownLocation;
		this.context = context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return ListKnownLocation.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return ListKnownLocation.get(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return ListKnownLocation.get(position).getId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View view = (View) inflater.inflate(
				R.layout.list_message_item, null);

		KnownLocation kL = ListKnownLocation.get(position);
		String Address = kL.getAddress();
		double lat = kL.getLat();
		double lng = kL.getLat();

		TextView titleView = (TextView) view
				.findViewById(R.id.list_message_title);
		TextView senderView = (TextView) view
				.findViewById(R.id.list_Messages_sender);
		ImageView iconView = (ImageView) view
				.findViewById(R.id.list_message_icon);

		boolean isRead = kL.isRead();

		int iconId = R.drawable.btn_radio_on_holo_light;

		if (isRead) {
			iconId = R.drawable.btn_radio_on_disabled_holo_light;
		}

		Drawable icon = context.getResources().getDrawable(iconId);

		iconView.setImageDrawable(icon);

		titleView.setText(Address);
		//senderView.setText(sender);
		return view;
	}

}
