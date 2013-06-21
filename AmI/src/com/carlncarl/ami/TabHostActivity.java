package com.carlncarl.ami;

import java.util.HashMap;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;

import com.carlncarl.ami.PlayGameFragment.PlayTabInterface;
import com.carlncarl.ami.game.Action;
import com.carlncarl.ami.game.Game;
import com.carlncarl.ami.game.Player;

public class TabHostActivity extends FragmentActivity implements
		TabHost.OnTabChangeListener, PlayTabInterface {
	private TabHost mTabHost;
	private Game game;
	private HashMap<String, TabInfo> mapTabInfo = new HashMap<String, TabInfo>();
	private TabInfo mLastTab = null;

	private GameService gService;
	protected boolean serviceConnected;
	
	
	private ServiceConnection sConn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			gService = null;
			serviceConnected = false;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			gService = ((GameService.GameBinder) service).getService();
			gService.setPlayActivity(TabHostActivity.this);
			if(gService.getGame().isServer()){
				gService.getGame().startTurn();
			}
			PlayGameFragment playFragment = (PlayGameFragment) getSupportFragmentManager().findFragmentByTag("Tab1");
			playFragment.setGService(gService);
			game = gService.getGame();

			//gService.initialize(GameActivity.this, game);
			// gService.setActivity(GameActivity.this);
			// gService.setGame(game);
			//
			serviceConnected = true;
		}
	};
	
	protected void onStart() {
		super.onStart();
		bindService(new Intent(this, GameService.class), sConn,
				Context.BIND_ABOVE_CLIENT);
	};
	
	
	private class TabInfo {
		private String tag;
		private Class clss;
		private Bundle args;
		private Fragment fragment;

		TabInfo(String tag, Class clazz, Bundle args) {
			this.tag = tag;
			this.clss = clazz;
			this.args = args;
		}

	}

	class TabFactory implements TabContentFactory {

		private final Context mContext;

		/**
		 * @param context
		 */
		public TabFactory(Context context) {
			mContext = context;
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see android.widget.TabHost.TabContentFactory#createTabContent(java.lang.String)
		 */
		public View createTabContent(String tag) {
			View v = new View(mContext);
			v.setMinimumWidth(0);
			v.setMinimumHeight(0);
			return v;
		}

	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		// Bundle extras = getIntent().getExtras();
		// if (extras != null) {
		// this.setGame((Game) extras.getSerializable(GameService.EXTRA_GAME));
		// }

		// Step 1: Inflate layout
		setContentView(R.layout.gameplay);
		// Step 2: Setup TabHost
		initialiseTabHost(savedInstanceState);
		if (savedInstanceState != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab")); // set
																				// the
																				// tab
																				// as
																				// per
																				// the
																				// saved
																				// state
		}
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onSaveInstanceState(android.os.Bundle)
	 */
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("tab", mTabHost.getCurrentTabTag()); // save the tab
																// selected
		super.onSaveInstanceState(outState);
	}

	/**
	 * Step 2: Setup TabHost
	 */
	private void initialiseTabHost(Bundle args) {
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
		TabInfo tabInfo = null;
		TabHostActivity.addTab(
				this,
				this.mTabHost,
				this.mTabHost.newTabSpec("Tab1").setIndicator(
						prepareTabView(this,
								R.drawable.button_panel_corner_left)),
				(tabInfo = new TabInfo("Tab1", PlayGameFragment.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);
		TabHostActivity.addTab(
				this,
				this.mTabHost,
				this.mTabHost.newTabSpec("Tab2").setIndicator(
						prepareTabView(this,
								R.drawable.button_panel_center_left)),
				(tabInfo = new TabInfo("Tab2", HistoryFragment.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);
		TabHostActivity.addTab(
				this,
				this.mTabHost,
				this.mTabHost.newTabSpec("Tab3").setIndicator(
						prepareTabView(this,
								R.drawable.button_panel_center_right)),
				(tabInfo = new TabInfo("Tab3", LobbyFragment.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);
		TabHostActivity.addTab(
				this,
				this.mTabHost,
				this.mTabHost.newTabSpec("Tab4").setIndicator(
						prepareTabView(this,
								R.drawable.button_panel_corner_right)),
				(tabInfo = new TabInfo("Tab4", Tab4Fragment.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);
		// Default to first tab
		this.onTabChanged("Tab1");
		//
		mTabHost.setOnTabChangedListener(this);
	}

	/**
	 * @param activity
	 * @param tabHost
	 * @param tabSpec
	 * @param clss
	 * @param args
	 */
	private static void addTab(TabHostActivity activity, TabHost tabHost,
			TabHost.TabSpec tabSpec, TabInfo tabInfo) {
		// Attach a Tab view factory to the spec
		tabSpec.setContent(activity.new TabFactory(activity));
		String tag = tabSpec.getTag();

		// Check to see if we already have a fragment for this tab, probably
		// from a previously saved state. If so, deactivate it, because our
		// initial state is that a tab isn't shown.
		tabInfo.fragment = activity.getSupportFragmentManager()
				.findFragmentByTag(tag);
		if (tabInfo.fragment != null && !tabInfo.fragment.isDetached()) {
			FragmentTransaction ft = activity.getSupportFragmentManager()
					.beginTransaction();
			ft.detach(tabInfo.fragment);
			ft.commit();
			activity.getSupportFragmentManager().executePendingTransactions();
		}

		tabHost.addTab(tabSpec);
	}

	private static View prepareTabView(Context context, int drawable) {
		View view = LayoutInflater.from(context).inflate(R.layout.tab, null);
		view.setBackgroundResource(drawable);
		return view;

	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see android.widget.TabHost.OnTabChangeListener#onTabChanged(java.lang.String)
	 */
	public void onTabChanged(String tag) {
		TabInfo newTab = this.mapTabInfo.get(tag);
		if (mLastTab != newTab) {
			FragmentTransaction ft = this.getSupportFragmentManager()
					.beginTransaction();
			if (mLastTab != null) {
				if (mLastTab.fragment != null) {
					ft.detach(mLastTab.fragment);
				}
			}
			if (newTab != null) {
				if (newTab.fragment == null) {
					newTab.fragment = Fragment.instantiate(this,
							newTab.clss.getName(), newTab.args);
					ft.add(R.id.realtabcontent, newTab.fragment, newTab.tag);
				} else {
					ft.attach(newTab.fragment);
				}
			}

			mLastTab = newTab;
			ft.commit();
			this.getSupportFragmentManager().executePendingTransactions();
		}
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
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

	public GameService getGService() {
		return gService;
	}

	public void setGService(GameService gService) {
		this.gService = gService;
	}

	@Override
	public void onAnswerGiven(int answer) {
		game.sendAnswerToServer(answer);
	}

	@Override
	public void onQuestionAsked(String question) {
		game.sendAskedQuestion(question);
	}

	@Override
	public void onTypeMyCharacter(String character) {
		// TODO Auto-generated method stub
		
	}

	public void startNewTurn(final Player p){
		this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				PlayGameFragment playFragment = (PlayGameFragment) getSupportFragmentManager().findFragmentByTag("Tab1");
				playFragment.turnPlayer(p);
			}
		});
	}
	
	
	public void receiveQuestion(final Player p, final String question){

	}

	public void receiveAction(final Action action) {
		if(action.getType() == Action.ACTION_QUESTION){
		this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				PlayGameFragment playFragment = (PlayGameFragment) getSupportFragmentManager().findFragmentByTag("Tab1");
				playFragment.receivedQuestion(action.getPlayer(), action.getValue());
			}
		});
		} else {
//			this.runOnUiThread(new Runnable() {
//				
//				@Override
//				public void run() {
//					PlayGameFragment playFragment = (PlayGameFragment) getSupportFragmentManager().findFragmentByTag("Tab1");
//					playFragment.receivedQuestion(action.getPlayer(), action.getValue());
//				}
//			});
		}
	}

	public void notifyQuestionsAdapter() {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				PlayGameFragment playFragment = (PlayGameFragment) getSupportFragmentManager().findFragmentByTag("Tab1");
				if(playFragment.isAdded()){
					playFragment.notifyQuestionsAdapter();
				}
				
			}
		});
	}
	
}