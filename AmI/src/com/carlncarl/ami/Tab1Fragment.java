package com.carlncarl.ami;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.carlncarl.ami.game.Player;
 

 
/**
 * @author mwho
 *
 */
public class Tab1Fragment extends Fragment  {
    /** (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
	
	private Player player;
	private ListView listViewPlayers;
	private PlayerAdapter adapter;

	
	
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (container == null) {
            // We have different layouts, and in one of them this
            // fragment's containing frame doesn't exist.  The fragment
            // may still be created from its saved state, but there is
            // no reason to try to create its view hierarchy because it
            // won't be displayed.  Note this is not needed -- we could
            // just run the code below, where we would create and return
            // the view hierarchy; it would just never be used.
            return null;
        }
        return (LinearLayout)inflater.inflate(R.layout.gameplay_tab1, container, false);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
    	Button buttonGameplayYes = (Button) getView().findViewById(R.id.button_yes);
		buttonGameplayYes.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				answerYes();
			}

			
		});
		Button buttonGameplayDont = (Button) getView().findViewById(R.id.button_dont);
		buttonGameplayDont.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				answerDont();
			}

			
		});
		Button buttonGameplayNo = (Button) getView().findViewById(R.id.button_no);
		buttonGameplayNo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				answerNo();
				
			}

			
		});
    
    }
    private void answerYes() {
		// TODO Auto-generated method stub
		
	}
    private void answerDont() {
		// TODO Auto-generated method stub
		
	}
    private void answerNo() {
		// TODO Auto-generated method stub
		
	}

}
