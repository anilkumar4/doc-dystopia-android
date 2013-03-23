package com.doc.dystopia;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.FragmentManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
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
	private LocationManager myLocationManager;
	private Geocoder geocoder;

	private LinearLayout infoWindowLayout;
	private TextView infoWindowAddrView;
	
	Marker mMarker;
	
	
	private void buildInfoWindowView(Marker marker){
		
		LinearLayout layout = new LinearLayout(getBaseContext());
		layout.setOrientation(LinearLayout.HORIZONTAL);
		
		TextView locationText = new TextView(getBaseContext());
		locationText.setText(getLocationDescriptor(marker));
		
		layout.addView(locationText);
		
		infoWindowAddrView = locationText;
		infoWindowLayout = layout;
	}
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_confirm);
		// Show the Up button in the action bar. 
		setupActionBar();
		
		
		geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
		

		myLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		
		FragmentManager myFragmentManager = getFragmentManager();
		MapFragment myMapFragment = (MapFragment) myFragmentManager
				.findFragmentById(R.id.map);
		mMap = myMapFragment.getMap();


		mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		
		Location lastKnown = myLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		LatLng loc = new LatLng(lastKnown.getLatitude(), lastKnown.getLongitude());
		
		MarkerOptions options = new MarkerOptions();
        options.position(loc);
        options.draggable(true);

        mMarker = mMap.addMarker(options);
        mMarker.setSnippet(getLocationDescriptor(mMarker));
        mMarker.setTitle("Camera Location");
        
		buildInfoWindowView(mMarker);

		mMap.setInfoWindowAdapter(new InfoWindowAdapter() {
			
			@Override
			public View getInfoWindow(Marker marker) {
				return null;
			}
			
			@Override
			public View getInfoContents(Marker marker) {
				return infoWindowLayout;
			}
		});
		
        mMarker.showInfoWindow();

		
		mMap.setOnMarkerDragListener(new OnMarkerDragListener() {
			
			@Override
			public void onMarkerDragStart(Marker marker) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onMarkerDragEnd(Marker marker) {
				infoWindowAddrView.setText(getLocationDescriptor(marker));
				marker.showInfoWindow(); //show update		
			}
			
			@Override
			public void onMarkerDrag(Marker marker) {
				//not updating location constantly to avoid lag.
			}
		});
		
		/*
		mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(Marker marker) {
				if(marker.isInfoWindowShown()){
					marker.hideInfoWindow();
				}else{
					marker.showInfoWindow();
				}
				
				return false;
			}
		});*/
		
		
		
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 19.0F));
		
	}
	
	
	/**
	 * Try for reverse Geocoded address, fall back on lat/long
	 * @param marker
	 * @return
	 */
	String getLocationDescriptor(Marker marker){
		String address = null;
		List<Address> addresses;
		try {
			addresses = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1);
		} catch (IOException e) {
			return marker.getPosition().toString();
		}
		if (addresses.size() > 0){
			//just get the first on the list of possibles
			Address addr = addresses.get(0);
			address = addr.getThoroughfare() + ", " + addr.getLocality() + ", " + addr.getAdminArea() + ", " + addr.getCountryName();
		}else{
			address = "empty list of addresses returned";
		}
		
		return address;

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
