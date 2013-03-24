package com.doc.dystopia;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * kick off the async task to get the location, populate the text view and refresh the marker's infoWindow. 
 * Async because network IO on UI thread is shit
 * @author kinsp1
 *
 */
public class ReverseGeoSyncTask extends AsyncTask<Void, Void, List<Address>> {
    private TextView mTextView;
	private Geocoder mGeocoder;
	private Marker mMarker;
	private LatLng pos;

    public ReverseGeoSyncTask(Marker marker, TextView textView, Geocoder geocoder) {
    	mTextView = textView;
    	mGeocoder = geocoder;
    	mMarker = marker;
    	pos = mMarker.getPosition(); // because android shits a brick if this isn't done on the main thread. Teaches me for trying to be clever.
    }

	// do reverse geocoding using network, run in the task thread
	@Override protected List<Address> doInBackground(Void...voids) {
		try {
			return mGeocoder.getFromLocation(pos.latitude, pos.longitude, 1);
		} catch (IOException e) {
			return new ArrayList<Address>();
		}
	}

	// handle the address list. Possible values are empty and 1 entry (maxResults=1)
    @Override  protected void onPostExecute(List<Address> addressList) {
    	if (addressList.size() > 0){
    		mTextView.setText(describeAddress(addressList.get(0))); // success! show address
        }else{
        	mTextView.setText(mMarker.getPosition().toString()); // fail. Show lat/long
        }
    	mMarker.showInfoWindow();
    }
    
    // a posh toString(). Generates a displayable address string from an address
	String describeAddress(Address address){
		return address.getThoroughfare() + " " + address.getLocality() + ", " + address.getAdminArea() + ", " + address.getCountryName();
	}
    
}