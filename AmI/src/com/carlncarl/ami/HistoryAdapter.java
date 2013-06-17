package com.carlncarl.ami;

import java.util.LinkedList;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.carlncarl.ami.game.Action;
import com.carlncarl.ami.game.Game;


public class HistoryAdapter extends ArrayAdapter<Action> {
	


	

	
		private final Context context;
		private final LinkedList<Action> values;

		public HistoryAdapter(Context context, LinkedList<Action> values) {
			super(context, R.layout.history_view, values);
			this.context = context;
			this.values = values;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
			        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			int viewId ;
			int yes = 0 , no = 0 , dont = 0;
				 viewId = R.layout.history_view;
			
			
			View rowView = inflater.inflate(viewId , parent, false);
			TextView textHistoryAskedQuestion = (TextView) rowView.findViewById(R.id.textHistoryAskedQuestion);
			//TextView textViewPlayerStatus = (TextView) rowView.findViewById(R.id.textViewPlayerStatus);
			TextView historyAnswers = (TextView) rowView.findViewById(R.id.historyAnswers);
			
			textHistoryAskedQuestion.setText(values.get(position).getValue());
			
			for (Action ans : values.get(position).getChilds()) {
				switch (Integer.parseInt(ans.getValue())) {
				case Game.ANSWER_YES:
					yes++;
					break;
				case Game.ANSWER_NO:
					no++;
					break;
	
					
					
				default:
					
					dont++;
					
					break;
				}
			}
			
			//textViewPlayerName.setText(values.get(position).getName());
			//textViewPlayerStatus.setText(values.get(position).getStatus());
			
			
			return rowView;
		}
	}



