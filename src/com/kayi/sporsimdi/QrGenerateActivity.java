package com.kayi.sporsimdi;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.spor.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.kayi.sporsimdi.helper.SQLiteHandler;
import com.kayi.sporsimdi.helper.SessionManager;

public class QrGenerateActivity extends Activity {

	private static final String TAG = QrGenerateActivity.class.getSimpleName();

	private SessionManager session;
	private SQLiteHandler db;

	private String qrCode = "";

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_qr);

		session = new SessionManager(getApplicationContext());

		String email = session.getEmail();

		Bundle b = getIntent().getExtras();
		qrCode = b.getString("qrcode");
		
		 WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
		   Display display = manager.getDefaultDisplay();
		   Point point = new Point();
		   display.getSize(point);
		   int width = point.x;
		   int height = point.y;
		   int smallerDimension = width < height ? width : height;
		   smallerDimension = (smallerDimension * 3/4)*3/4;
		
		
		QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(qrCode,
	             null,
	             Contents.Type.TEXT, 
	             BarcodeFormat.QR_CODE.toString(),
	             smallerDimension);
	   try {
	    Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
	    ImageView myImage = (ImageView) findViewById(R.id.qrcodeView);
	    myImage.setImageBitmap(bitmap);
	 
	   } catch (WriterException e) {
	    e.printStackTrace();
	   }
		
		
//		   try {
//		    Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
//		    ImageView myImage = (ImageView) findViewById(R.id.imageView1);
//		    myImage.setImageBitmap(bitmap);
//		 
//		   } catch (WriterException e) {
//		    e.printStackTrace();
//		   }
//		
//
//		QRCodeWriter writer = new QRCodeWriter();
//		ImageView tnsd_iv_qr = (ImageView) findViewById(R.id.qrcodeView);
//		try {
//			BitMatrix bitMatrix = writer.encode(qrCode, BarcodeFormat.QR_CODE,
//					512, 512);
//			
//			
//			Bitmap bitmap = bitMatrix.encodeAsBitmap();
//		    ImageView myImage = (ImageView) findViewById(R.id.imageView1);
//		    myImage.setImageBitmap(bitmap);
//
//			int width = 512;
//			int height = 512;
//			Bitmap bmp = Bitmap.createBitmap(width, height,
//					Bitmap.Config.RGB_565);
//			for (int x = 0; x < width; x++) {
//				for (int y = 0; y < height; y++) {
//					if (bitMatrix.get(x, y) == 0)
//						bmp.setPixel(x, y, Color.BLACK);
//					else
//						bmp.setPixel(x, y, Color.WHITE);
//				}
//			}
//			tnsd_iv_qr.setImageBitmap(bmp);
//
//		} catch (WriterException e) {
//			// Log.e("QR ERROR", ""+e);11111111111111111111
//
//		}

		if (!session.isLoggedIn()) {
			// logoutUser();
		}

	}

	/**
	 * Logging out the user. Will set isLoggedIn flag to false in shared
	 * preferences Clears the user data from sqlite users table
	 * */
	// private void logoutUser() {
	// session.setLogin("", false);
	//
	// db.deleteUsers();
	//
	// // Launching the login activity
	// Intent intent = new Intent(QrGenerateActivity.this, LoginActivity.class);
	// startActivity(intent);
	// finish();
	// }
}
