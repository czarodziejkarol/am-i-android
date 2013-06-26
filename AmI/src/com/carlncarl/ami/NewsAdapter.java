package com.carlncarl.ami;

import java.util.LinkedList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.carlncarl.ami.game.Action;
import com.carlncarl.ami.game.Game;

public class NewsAdapter extends ArrayAdapter<Action> {
	


	

	
	private final Context context;
	private final LinkedList<Action> values;

	public NewsAdapter(Context context, LinkedList<Action> values) {
		super(context, R.layout.news_item, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	
			 
		
		
		View rowView = inflater.inflate(R.layout.news_item, parent, false);
		TextView textContent = (TextView) rowView.findViewById(R.id.textViewNewsContent);
		Action action = values.get(position);
		String messageText = "";
		switch (values.get(position).getType()) {
		
		case Action.ACTION_GUESS:
			
			 messageText  = action.getPlayer().getName() + " guessed "
					+ action.getValue();

			if (action.getPlayer().getCharacter().equalsIgnoreCase(action.getValue())) {
				messageText += ". Position: "
						+ action.getPlayer().getWinPos();
				textContent.setTextColor(Color.GREEN);
			} else {
				textContent.setTextColor(Color.RED);
			}
			 
			break;
		default:
			messageText = action.getPlayer().getName() + " answered "
			+ Game.getAnswerString(Integer.parseInt( action.getValue())).toLowerCase();
			break;
		
		}
		textContent.setText(messageText);
		
		
		
		return rowView;
	}
	
	
}



