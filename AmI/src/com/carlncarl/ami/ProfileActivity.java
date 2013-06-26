package com.carlncarl.ami;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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

import com.carlncarl.ami.db.Database;
import com.carlncarl.ami.db.MySQLiteHelper;
import com.carlncarl.ami.game.Player;

public class ProfileActivity extends Activity {

	public static final String PLAYER_KEY = "player";

	private Player player;
	private EditText editPlayerName;
	private ImageView imageViewProfile;
	private Button buttonSaveSettings;
	private CheckBox boxQuest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.profile_activity);
		

		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			this.player = (Player) extras.getSerializable(PLAYER_KEY);

		}

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		editPlayerName = (EditText) findViewById(R.id.editTextUsernameProfil);
		editPlayerName.setText(player.getName());
		boxQuest = (CheckBox) findViewById(R.id.checkBoxSaveAskProfil);
		boxQuest.setChecked(player.isAuto_add());
		imageViewProfile = (ImageView) findViewById(R.id.imageViewPlayerIconProfil);
		
		File filePath = this.getFileStreamPath(player.getImage());
		imageViewProfile.setImageDrawable(Drawable
				.createFromPath(filePath.toString()));
		
		
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
				savePlayer();
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
        String[] params = {editPlayerName.getText().toString(), Boolean.toString(boxQuest.isChecked()),
        		player.getUuid()};
        new CreatePlayerTask().execute(params);
        player.setName(params[0]);
        player.setAuto_add(boxQuest.isChecked());
	}

	
	private class CreatePlayerTask extends AsyncTask<String, Integer, Player> {

        @Override
        protected Player doInBackground(String... params) {
            MySQLiteHelper myHel = new MySQLiteHelper(getBaseContext());

            SQLiteDatabase db = myHel.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(Database.Player.COLUMN_NAME_NAME, params[0]);
            
            values.put(Database.Player.COLUMN_NAME_AUTO_ADD, Boolean.parseBoolean(params[1]));

            String whereClause=Database.Player.COLUMN_NAME_UUID+"='"+params[2]+"'";
			db.update(Database.Player.TABLE_NAME, values, whereClause, null);
            
            
            
            
            db.close();
            return null;

        }

        @Override
        protected void onPostExecute(Player result) {
        	finish();
        }
    }
	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.profile, menu);
	// return true;
	// }

}
