package com.doc.dystopia;

import java.io.InputStream;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

public class CopyOfSelectCamera extends Activity implements LocationSource,
		LocationListener {

	ImageView mImageView;
	Bitmap mImageBitmap;
	private GoogleMap mMap;

	LocationManager myLocationManager = null;
	OnLocationChangedListener myLocationListener = null;
	Criteria myCriteria;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_camera);

		FragmentManager myFragmentManager = getFragmentManager();
		MapFragment myMapFragment = (MapFragment) myFragmentManager
				.findFragmentById(R.id.map);
		mMap = myMapFragment.getMap();

		mMap.setMyLocationEnabled(true);
		

		mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

		myCriteria = new Criteria();
		myCriteria.setAccuracy(Criteria.ACCURACY_COARSE);
		myLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		
		
	}



	final int RQS_GooglePlayServices = 1;

	@Override
	protected void onResume() {
		super.onResume();

		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getApplicationContext());

		if (resultCode == ConnectionResult.SUCCESS) {
			Toast.makeText(
					getApplicationContext(),
					Boolean.toString(myLocationManager
							.isProviderEnabled(LocationManager.GPS_PROVIDER))
							+ ", "
							+ Boolean.toString(myLocationManager
									.isProviderEnabled(LocationManager.NETWORK_PROVIDER)),
					Toast.LENGTH_LONG).show();
			
			
			

			// Register for location updates using a Criteria, and a callback on
			// the specified looper thread.
			myLocationManager.requestLocationUpdates(0L, // minTime
					0.0f, // minDistance
					myCriteria, // criteria
					this, // listener
					null); // looper
			
			

			// Replaces the location source of the my-location layer.
			mMap.setLocationSource(this);

		} else {
			GooglePlayServicesUtil.getErrorDialog(resultCode, this,
					RQS_GooglePlayServices);
		}

	}

	@Override
	protected void onPause() {
		mMap.setLocationSource(null);
		myLocationManager.removeUpdates(this);

		super.onPause();
	}

	@Override
	protected void onStart() {
		super.onStart();

		Intent intent = getIntent();
		if (intent != null) {
			Bundle extras = intent.getExtras();
			String action = intent.getAction();
			// if this is from the share menu
			if (Intent.ACTION_SEND.equals(action)) {
				if (extras.containsKey(Intent.EXTRA_STREAM)) {

					handleCameraPhoto(intent);
				}
			}
		}

	}

	private void handleCameraPhoto(Intent intent) {

		try {

			mImageView = (ImageView) findViewById(R.id.imageView1);
			Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);

			// getContentResolver().q
			mImageBitmap = null;

			// int id = Integer.parseInt(imageUri.toString().substring(
			// imageUri.toString().lastIndexOf("/") + 1,
			// imageUri.toString().length()));
			/*
			 * mImageBitmap =
			 * MediaStore.Images.Thumbnails.getThumbnail(getContentResolver(),
			 * id, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND, null);
			 */
			InputStream in = getContentResolver().openInputStream(imageUri);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 4;
			mImageBitmap = BitmapFactory.decodeStream(in, null, options);

			// mImageBitmap =
			// MediaStore.Images.Media.getBitmap(getContentResolver(),
			// imageUri);

			mImageView.setImageBitmap(mImageBitmap);

			/*
			 * get geoData, compass angle: problem: neither are from photo,
			 * compass angle likely to be off when sharing. auto geo-tagged
			 * photos require sharing location setting problem is solvable by
			 * user inconvenience. acceptable midterm solution.
			 */

			return;
		} catch (Exception e) {
			Log.e(this.getClass().getName(), e.toString());
		}

	}

	public static Bitmap loadFullImage(Context context, Uri photoUri) {
		Cursor photoCursor = null;

		try {
			// Attempt to fetch asset filename for image
			String[] projection = { MediaStore.Images.Media.DATA };
			photoCursor = context.getContentResolver().query(photoUri,
					projection, null, null, null);

			if (photoCursor != null && photoCursor.getCount() == 1) {
				photoCursor.moveToFirst();
				String photoFilePath = photoCursor.getString(photoCursor
						.getColumnIndex(MediaStore.Images.Media.DATA));

				// Load image from path
				return BitmapFactory.decodeFile(photoFilePath, null);
			}
		} finally {
			if (photoCursor != null) {
				photoCursor.close();
			}
		}

		return null;
	}

	public static Bitmap decodeSampledBitmapFromResource(Resources res,
			int resId, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.select_camera, menu);
		return true;
	}

	@Override
	public void activate(OnLocationChangedListener listener) {
		myLocationListener = listener;
		/*
		 LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		 Criteria criteria = new Criteria();
		 String bestProvider = locationManager.getBestProvider(criteria, false);
		 Location location = locationManager.getLastKnownLocation(bestProvider);
		
		myLocationListener.onLocationChanged(location);*/
	}

	@Override
	public void deactivate() {
		myLocationListener = null;
	}

	@Override
	public void onLocationChanged(Location location) {
		Toast.makeText(
				getApplicationContext(),
				"onLocationChange!!",
				Toast.LENGTH_LONG).show();

		
		if (myLocationListener != null) {
			myLocationListener.onLocationChanged(location);

			LatLng latlng = new LatLng(location.getLatitude(),
					location.getLongitude());
			mMap.animateCamera(CameraUpdateFactory.newLatLng(latlng));

		}
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}

}
