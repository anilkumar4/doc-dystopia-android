package com.doc.dystopia;

import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

public class SelectCamera extends Activity {

	ImageView mImageView;
	Bitmap mImageBitmap;

	LocationManager myLocationManager = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_camera);

		myLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	}


	
	public void onButton(View view){
		
		Intent intent = new Intent(this, LocationConfirm.class);
		intent.putExtra(Intent.EXTRA_STREAM, getIntent().getParcelableExtra(Intent.EXTRA_STREAM));
		
		startActivity(intent);
		
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

			mImageView = (ImageView) findViewById(R.id.sharedImg);
			Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);

			mImageBitmap = null;

			InputStream in = getContentResolver().openInputStream(imageUri);
			try{
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 4;
				mImageBitmap = BitmapFactory.decodeStream(in, null, options);
				mImageView.setImageBitmap(mImageBitmap);
			}finally{
				in.close();
			}

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

}
