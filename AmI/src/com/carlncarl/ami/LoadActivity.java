package com.carlncarl.ami;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.carlncarl.ami.db.Database;
import com.carlncarl.ami.db.MySQLiteHelper;
import com.carlncarl.ami.game.Player;

public class LoadActivity extends FragmentActivity {

    private Player player;
    private View menu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_load);
        
		
        menu = findViewById(R.id.menuView);
        menu.setVisibility(View.INVISIBLE);
        Button buttonCreateGame = (Button) findViewById(R.id.buttonCreateGame);
        buttonCreateGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(),GameActivity.class);
                intent.putExtra(GameActivity.PLAYER_KEY, player);
                intent.putExtra(GameActivity.IS_SERVER, true);
                startActivity(intent);
            }
        });
        
        Button buttonJoinGame = (Button) findViewById(R.id.buttonJoinGame);
        
        buttonJoinGame.setOnClickListener(new View.OnClickListener() {
				
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getBaseContext(),GameActivity.class);
                intent.putExtra(GameActivity.PLAYER_KEY, player);
                intent.putExtra(GameActivity.IS_SERVER, false);
                startActivity(intent);
			}
		});
Button buttona = (Button) findViewById(R.id.buttonMyProfile);
        
        buttona.setOnClickListener(new View.OnClickListener() {
				
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getBaseContext(),ProfileActivity.class);
				intent.putExtra(GameActivity.PLAYER_KEY, player);
                startActivity(intent);
			}
		});
		new LoadingTask().execute("");
	}
//
	protected void createPlayer(String name, String fileName) {
        String[] params = {name, fileName};
        new CreatePlayerTask().execute(params);
	}

	public void showCreatePlayerDialog() {
		DialogFragment dialog = new CreateUserDialogFragment(this);
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), "CreateUserDialogFragment");
	}

	protected void goToMainScreen(Player result) {
        this.player = result;
        menu.setVisibility(View.VISIBLE);
		
	}
	

	
	
	private class LoadingTask extends AsyncTask<Object, Integer, Player> {

        @Override
        protected Player doInBackground(Object... params) {
            MySQLiteHelper myHel = new MySQLiteHelper(getBaseContext());

            SQLiteDatabase db = myHel.getReadableDatabase();
            try {
                get(1000,TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (TimeoutException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String selection = "" + Database.Player.COLUMN_NAME_ME + " = 1";
            Cursor c = db.query(Database.Player.TABLE_NAME, null, selection,
                    null, null, null, null);

            Player p = null;
            if (c.getCount() != 0) {
                p = new Player(c);
            }
            c.close();
            db.close();
            return p;

        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        @Override
        protected void onPostExecute(Player result) {
            if (result == null) {
                showCreatePlayerDialog();
            } else {
                goToMainScreen(result);
            }
        }
    }
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		new LoadingTaskBack().execute("");
	}
	
	
	
	private class LoadingTaskBack extends AsyncTask<Object, Integer, Player> {

        @Override
        protected Player doInBackground(Object... params) {
            MySQLiteHelper myHel = new MySQLiteHelper(getBaseContext());

            SQLiteDatabase db = myHel.getReadableDatabase();

            String selection = "" + Database.Player.COLUMN_NAME_ME + " = 1";
            Cursor c = db.query(Database.Player.TABLE_NAME, null, selection,
                    null, null, null, null);

            Player p = null;
            if (c.getCount() != 0) {
                p = new Player(c);
            }
            c.close();
            db.close();
            return p;

        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        @Override
        protected void onPostExecute(Player result) {
        	LoadActivity.this.player = result;
        }
    }
	

    private class CreatePlayerTask extends AsyncTask<String, Integer, Player> {

        @Override
        protected Player doInBackground(String... params) {
            MySQLiteHelper myHel = new MySQLiteHelper(getBaseContext());

            SQLiteDatabase db = myHel.getWritableDatabase();

            ContentValues values = new ContentValues();
            String myUUID = UUID.randomUUID().toString();
            values.put(Database.Player.COLUMN_NAME_UUID, myUUID);
            values.put(Database.Player.COLUMN_NAME_NAME, params[0]);
            values.put(Database.Player.COLUMN_NAME_IMAGE, params[1]);
            values.put(Database.Player.COLUMN_NAME_AUTO_ADD, true);
            values.put(Database.Player.COLUMN_NAME_ME, true);
            long newRowId;
            newRowId = db.insert(Database.Player.TABLE_NAME, null, values);
            Player p = new Player(myUUID,params[0],params[1]);
            db.close();
            return p;

        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        @Override
        protected void onPostExecute(Player result) {
            if (result == null) {

            } else {
                goToMainScreen(result);
            }
        }
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.test, menu);
	    return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.item_exit:
	            exitGame();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	private void exitGame() {
		this.stopService(new Intent(this, GameService.class));
		finish();
	}
}
