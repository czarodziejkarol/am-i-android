package com.carlncarl.ami;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
 

 
/**
 * @author mwho
 *
 */
public class HistoryFragment extends Fragment {
    
	private GameService gService;
	private TabHostActivity parent;
	private HistoryAdapter historyAdapter;
	private ListView historyList;
	
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
        return (LinearLayout)inflater.inflate(R.layout.gameplay_tab2, container, false);
    }

    
    public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		parent = (TabHostActivity) getActivity();
		gService = parent.getGService();
		setGService(parent.getGService());
	}
    
    
    public void setGService(GameService gs){
		gService = gs;
		setState();
	
	}
	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		setState();
	}

	private void setState() {
		if(gService!=null){
			
			if(historyAdapter!=null){
				historyAdapter.notifyDataSetChanged();
			} else {
				historyList = (ListView) getView().findViewById(R.id.historyList);
				historyAdapter = new HistoryAdapter(gService, gService.getGame().getActions());
				historyList.setAdapter(historyAdapter);
			}
			
		}
	}

}