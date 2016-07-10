package com.at.solcoal.m;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.at.solcoal.R;
import com.at.solcoal.utility.ADDRESS;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class GeoAutoCompleteAdapter extends BaseAdapter implements Filterable
{
	private static final int MAX_RESULTS = 10;
	private Context mContext;
	private List<Address> resultList = new ArrayList<Address>();

	public GeoAutoCompleteAdapter(Context context)
	{
		mContext = context;
	}

	@Override
	public int getCount()
	{
		return resultList.size();
	}

	@Override
	public Address getItem(int index)
	{
		return resultList.get(index);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if (convertView == null)
		{
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.geo_search_result, parent, false);
		}

		((TextView) convertView.findViewById(R.id.geo_search_result_text))
				.setText(ADDRESS.getAddressStr(getItem(position)));

		return convertView;
	}

	@Override
	public Filter getFilter()
	{
		Filter filter = new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint)
			{
				FilterResults filterResults = new FilterResults();
				if (constraint != null)
				{
					List<Address> locations = findLocations(mContext, constraint.toString());

					// Assign the data to the FilterResults
					filterResults.values = locations;
					filterResults.count = locations.size();
				}
				return filterResults;
			}

			@Override
			protected void publishResults(CharSequence constraint, FilterResults results)
			{
				if (results != null && results.count > 0)
				{
					resultList = (List<Address>) results.values;
					notifyDataSetChanged();
				}
				else
				{
					notifyDataSetInvalidated();
				}
			}
		};
		return filter;
	}

	private List<Address> findLocations(Context context, String query_text)
	{
		List<Address> geo_search_results = new ArrayList<Address>();

		Geocoder geocoder = new Geocoder(context, context.getResources().getConfiguration().locale);
		List<Address> addresses = null;

		try
		{
			// Getting a maximum of 15 Address that matches the input text
			addresses = geocoder.getFromLocationName(query_text, 15);
			
			for (int i = 0; i < addresses.size(); i++)
			{
				Address address = (Address) addresses.get(i);
				if (address.getMaxAddressLineIndex() != -1)
				{
					geo_search_results.add(address);
				}
			}

		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return geo_search_results;
	}
}