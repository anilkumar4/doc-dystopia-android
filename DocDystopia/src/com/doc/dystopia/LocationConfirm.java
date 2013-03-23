package com.doc.dystopia;

import android.app.Activity;
import android.app.FragmentManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationConfirm extends Activity {
	private GoogleMap mMap;
	private LocationManager myLocationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_confirm);
		// Show the Up button in the action bar. 
		setupActionBar();
		
		
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
        //options.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));

		Marker marker = mMap.addMarker(options);
		/*
		mMap.setInfoWindowAdapter(new InfoWindowAdapter() {
			
			@Override
			public View getInfoWindow(Marker marker) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public View getInfoContents(Marker marker) {
				//ImageView imgView = new ImageView(getBaseContext());
				ImageView image;
				image = new ImageView(getBaseContext());
				
				byte[] byteArray = getIntent().getByteArrayExtra("image");
				Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
				
				image.setImageBitmap(bmp);

				return image;
			}
		});
		
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
		});
		
		*/
		
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
