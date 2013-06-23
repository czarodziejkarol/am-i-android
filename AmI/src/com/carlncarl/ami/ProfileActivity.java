package com.carlncarl.ami;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.carlncarl.ami.game.Player;

public class ProfileActivity extends Activity {

	public static final String PLAYER_KEY = "player";

	private Player player;
	private EditText editPlayerName;
	private ImageView imageViewProfile;
	private Button buttonSaveSettings;
	private CheckBox boxSound;
	private CheckBox boxQuest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_activity);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			this.player = (Player) extras.getSerializable(PLAYER_KEY);

		}

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		editPlayerName = (EditText) findViewById(R.id.editTextUsernameProfil);
		boxSound = (CheckBox) findViewById(R.id.checkBoxSoundsProfil);
		boxQuest = (CheckBox) findViewById(R.id.checkBoxSaveAskProfil);
		imageViewProfile = (ImageView) findViewById(R.id.imageViewPlayerIconProfil);

		ImageButton buttonImage = (ImageButton) findViewById(R.id.buttonCameraProfil);
		buttonImage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(takePicture, 0);
			}
		});

		buttonSaveSettings = (Button) findViewById(R.id.buttonSaveProfile);
		buttonSaveSettings.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 0:
			if (resultCode == Activity.RESULT_OK) {
				// data.get

				Bitmap photo = (Bitmap) data.getExtras().get("data");

				FileOutputStream fos = null;
				try {
					fos = this.openFileOutput(player.getImage(),
							Context.MODE_PRIVATE);
					photo.compress(Bitmap.CompressFormat.JPEG, 10, fos);
					fos.flush();
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

				File filePath = this.getFileStreamPath(player.getImage());
				imageViewProfile.setImageDrawable(Drawable
						.createFromPath(filePath.toString()));
			}
		}
	}

	protected void savePlayer() {

	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.profile, menu);
	// return true;
	// }

}
