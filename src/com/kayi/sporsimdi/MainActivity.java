package com.kayi.sporsimdi;

import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.spor.R;
import com.kayi.sporsimdi.app.AppConfig;
import com.kayi.sporsimdi.app.AppController;
import com.kayi.sporsimdi.helper.SQLiteHandler;
import com.kayi.sporsimdi.helper.SessionManager;

public class MainActivity extends Activity {

	private TextView txtName;
	private TextView txtEmail;
	private Button btnLogout;
	private Button btnQrGenerate;

	private SQLiteHandler db;
	private SessionManager session;

	private static final String TAG = QrGenerateActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		txtName = (TextView) findViewById(R.id.name);
		txtEmail = (TextView) findViewById(R.id.email);
		btnLogout = (Button) findViewById(R.id.btnLogout);
		btnQrGenerate = (Button) findViewById(R.id.btnLinkToGenerateQrScreen);

		// SqLite database handler
		db = new SQLiteHandler(getApplicationContext());

		// session manager
		session = new SessionManager(getApplicationContext());

		if (!session.isLoggedIn()) {
			logoutUser();
		}

		// Fetching user details from sqlite
		HashMap<String, String> user = db.getUserDetails();

		String name = user.get("name");
		String email = user.get("email");

		// Displaying the user details on the screen
		txtName.setText(name);
		txtEmail.setText(email);

		// Logout button click event
		btnLogout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				logoutUser();
			}
		});

		btnQrGenerate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				String checkLoginUrl = AppConfig.URL_GENERATE_QR
						+ session.getEmail();
				
				String tag_string_req = "req_generateqr";

				StringRequest strReq = new StringRequest(Method.GET,
						checkLoginUrl, new Response.Listener<String>() {

							@Override
							public void onResponse(String response) {
								Log.d(TAG,
										"Login Response: "
												+ response.toString());

								try {
									JSONObject jObj = new JSONObject(response);
									String code = jObj.getString("resultCode");

									// Check for error node in json
									if (code.equals("SUCCESS")) {
										// user successfully logged in
										// Create login session

										String qrCode = jObj
												.getString("qrCode");

										Bundle b = new Bundle();
										b.putString("qrcode", qrCode); // Your
																		// id
										Intent intent = new Intent(
												MainActivity.this,
												QrGenerateActivity.class);

										intent.putExtras(b); // Put your id to
																// your next
																// Intent
										startActivity(intent);

										// Launch main activity
									} else {
										// Error in login. Get the error message
										String errorMsg = jObj
												.getString("message");
										Toast.makeText(getApplicationContext(),
												errorMsg, Toast.LENGTH_LONG)
												.show();
									}
								} catch (Exception e) {
									// JSON error
									e.printStackTrace();
								}

							}
						}, new Response.ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {
								Log.e(TAG, "Login Error: " + error.getMessage());
								Toast.makeText(getApplicationContext(),
										error.getMessage(), Toast.LENGTH_LONG)
										.show();
							}
						}) {

				};

				AppController.getInstance().addToRequestQueue(strReq,
						tag_string_req);

			}
		});

		// Adding request to request queue

	}

	/**
	 * Logging out the user. Will set isLoggedIn flag to false in shared
	 * preferences Clears the user data from sqlite users table
	 * */
	private void logoutUser() {
		session.setLogin("", false);

		db.deleteUsers();

		// Launching the login activity
		Intent intent = new Intent(MainActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}
}
