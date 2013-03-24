package com.doc.dystopia;

import java.util.Locale;

import android.app.Activity;
import android.app.FragmentManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationConfirm extends Activity {
	private GoogleMap mMap;
	
	/**
	 * NOTE: event listeners and info window adapter need to be recreated if the map ends up being serialized 
	 * @param markerToInit
	 */
	private void initializeMarker(final Marker markerToInit){
		final TextView locationTextView = new TextView(getBaseContext());
		locationTextView.setText("loading...");
		
		mMap.setInfoWindowAdapter(new InfoWindowAdapter() {
			@Override public View getInfoContents(Marker marker) {
				if(markerToInit.equals(marker))
					return locationTextView;
				else
					return null; //fall back to default
			}
			
			@Override public View getInfoWindow(Marker marker) {return null;}
		});
		
		final Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
		
		new ReverseGeoSyncTask(markerToInit, locationTextView, geocoder).execute();

		//update marker when drag event ends
		mMap.setOnMarkerDragListener(new OnMarkerDragListener() {
			@Override
			public void onMarkerDragEnd(Marker marker) {
				if(markerToInit.equals(marker))
					new ReverseGeoSyncTask(marker, locationTextView, geocoder).execute();
			}
			
			@Override public void onMarkerDragStart(Marker marker) {}
			@Override public void onMarkerDrag(Marker marker) {}
		});
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_confirm);
		// Show the Up button in the action bar. 
		setupActionBar();

		//initialize map
		FragmentManager myFragmentManager = getFragmentManager();
		MapFragment myMapFragment = (MapFragment) myFragmentManager.findFragmentById(R.id.map);
		mMap = myMapFragment.getMap();
		mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		
		
		//show marker at last known location
		LocationManager locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		Location lastKnown = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		LatLng loc = new LatLng(lastKnown.getLatitude(), lastKnown.getLongitude());
		
		MarkerOptions options = new MarkerOptions();
        options.position(loc);
        options.draggable(true);
        Marker marker = mMap.addMarker(options);
		initializeMarker(marker);
		
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 19.0F));
	}


	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.location_confirm, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
