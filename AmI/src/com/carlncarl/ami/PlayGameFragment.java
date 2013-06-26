package com.carlncarl.ami;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.carlncarl.ami.game.Action;
import com.carlncarl.ami.game.Game;
import com.carlncarl.ami.game.Player;

/**
 * @author mwho
 * 
 */
public class PlayGameFragment extends Fragment {

	private static final int STATE_WAIT_ANSWERS = 0;
	private static final int STATE_QUESTION = 1;
	private static final int STATE_ANSWER = 2;
	private static final int STATE_WAIT_QUESTION = 3;
	/**
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 *      android.view.ViewGroup, android.os.Bundle)
	 */
	private GameService gService;
	private PlayerAdapter adapter;
	private TabHostActivity parent;
	private TextView textViewAsker;
	private LinearLayout answerLayout;
	private EditText editTextQuestion;
	private LinearLayout questionLayout;
	private LinearLayout waitingLayout;
	private ListView listViewMyQuestions;
	private TextView textViewStatus;
	private TextView textViewAskedQuestion;
	private HorizontialListView listview;

	PlayTabInterface callback;
	private ArrayAdapter<String> adapterQuestion;

	public interface PlayTabInterface {
		public void onAnswerGiven(int answer);

		public void onQuestionAsked(String question);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
			// We have different layouts, and in one of them this
			// fragment's containing frame doesn't exist. The fragment
			// may still be created from its saved state, but there is
			// no reason to try to create its view hierarchy because it
			// won't be displayed. Note this is not needed -- we could
			// just run the code below, where we would create and return
			// the view hierarchy; it would just never be used.
			return null;
		}

		return (LinearLayout) inflater.inflate(R.layout.gameplay_tab1,
				container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		parent = (TabHostActivity) getActivity();
		gService = parent.getGService();

		Button buttonGameplayYes = (Button) getView().findViewById(
				R.id.button_yes);
		buttonGameplayYes.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				answer(Game.ANSWER_YES);
			}

		});
		Button buttonGameplayDont = (Button) getView().findViewById(
				R.id.button_dont);
		buttonGameplayDont.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				answer(Game.ANSWER_DONT_KNOW);
			}

		});
		Button buttonGameplayNo = (Button) getView().findViewById(
				R.id.button_no);
		buttonGameplayNo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				answer(Game.ANSWER_NO);
			}
		});

		Button buttonSendQuestion = (Button) getView().findViewById(
				R.id.buttonSendQuestion);
		buttonSendQuestion.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				sendQuestion();
			}

		});

		// listViewPlayers = (ListView)
		// getView().findViewById(R.id.playerlistView);

		listview = (HorizontialListView) getView().findViewById(R.id.listview);

		textViewAsker = (TextView) getView().findViewById(R.id.textViewAsker);
		answerLayout = (LinearLayout) getView().findViewById(R.id.answerLayout);
		editTextQuestion = (EditText) getView().findViewById(
				R.id.editTextQuestion);
		questionLayout = (LinearLayout) getView().findViewById(
				R.id.questionLayout);
		waitingLayout = (LinearLayout) getView().findViewById(
				R.id.waitingLayout);
		textViewStatus = (TextView) getView().findViewById(R.id.textViewStatus);
		textViewAskedQuestion = (TextView) getView().findViewById(
				R.id.textViewAskedQuestion);

		// Game.players_test = new LinkedList<Player>();
		// Player p = new Player("1231233-321321-1", "Karol",
		// Player.DEFAULT_PHOTO);
		// Game.players_test.addFirst(p);
		// p = new Player("1231233-321321-1", "Karol", Player.DEFAULT_PHOTO);
		// Game.players_test.addFirst(p);
		// p = new Player("1231233-321321-1", "Karol",Player.DEFAULT_PHOTO);
		// Game.players_test.addFirst(p);
		// p = new Player("1231233-321321-1", "Karol", Player.DEFAULT_PHOTO);
		// Game.players_test.addFirst(p);
		// p = new Player("1231233-321321-1", "Karol",Player.DEFAULT_PHOTO);
		// Game.players_test.addFirst(p);
		// p = new Player("1231233-321321-1", "Karol", Player.DEFAULT_PHOTO);
		// Game.players_test.addFirst(p);
		// p = new Player("1231233-321321-1", "Karol",Player.DEFAULT_PHOTO);
		// Game.players_test.addFirst(p);
		//
		// adapter = new PlayerAdapter(getActivity(), Game.players);
		// listview.setAdapter(adapter);

	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		setState();
	}

	public void setGService(GameService gs) {
		gService = gs;

		//

		setState();
	}

	private void setState() {
		if (gService == null)
			return;

		adapter = new PlayerAdapter(gService, Game.players);
		// /test graczy
		// adapter = new PlayerAdapter(gService, Game.players_test);
		listview.setAdapter(adapter);
		listViewMyQuestions = (ListView) getView().findViewById(
				R.id.listViewMyQuestions);
		adapterQuestion = new ArrayAdapter<String>(gService,
				android.R.layout.simple_list_item_1, gService.getGame()
						.getMyQuestions());
		listViewMyQuestions.setAdapter(adapterQuestion);
		listViewMyQuestions
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						setQuestionFromMy(position);
					}
				});

		int state = gService.getGame().getGameStatus();

		switch (state) {
		case Game.GAME_STATUS_WRITE_QUESTION:
			setVisibleState(STATE_QUESTION);
			break;
		case Game.GAME_STATUS_TYPE_ANSWER:
			setVisibleState(STATE_ANSWER);
			Action a = gService.getGame().getLastAction();
			if (a != null) {
				receivedQuestion(a.getPlayer(), a.getValue());
			}
			break;
		case Game.GAME_STATUS_WAIT_FOR_QUESTION:
			setVisibleState(STATE_WAIT_QUESTION);
			break;
		case Game.GAME_STATUS_WAIT_FOR_ANSWER:
			setVisibleState(STATE_WAIT_ANSWERS);
			break;
		default:
			setVisibleState(STATE_WAIT_ANSWERS);
			break;
		}

	}

	protected void setQuestionFromMy(int position) {
		editTextQuestion.setText(gService.getGame().getMyQuestions()
				.get(position));

	}

	protected void sendQuestion() {
		callback.onQuestionAsked(editTextQuestion.getText().toString());
		editTextQuestion.setText("");
		InputMethodManager mgr = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(editTextQuestion.getWindowToken(), 0);

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		callback = (PlayTabInterface) activity;
	}

	private void answer(int answer) {

		callback.onAnswerGiven(answer);
	}

	public void receivedQuestion(Player player, String question) {
		if (gService.getGame().getMe().getUuid().equals(player.getUuid())) {

			setVisibleState(STATE_WAIT_ANSWERS);
		} else {
			setVisibleState(STATE_ANSWER);
			textViewAsker.setText(player.getName() + " ("
					+ player.getCharacter() + ")");
			textViewAskedQuestion.setText(question);
		}
	}

	public void turnPlayer(Player player) {
		if (player.getUuid().equals(gService.getGame().getMe().getUuid())) {
			// dla mnie. Wpisuje pytanie
			setVisibleState(STATE_QUESTION);
			notifyPlayersAdapter();

		} else {
			notifyPlayersAdapter();
			// odpowiadanie na pytanie
			setVisibleState(STATE_WAIT_ANSWERS);
			// answerButtonsView.setVisibility(View.VISIBLE);
			textViewAsker.setText(player.getName() + " ("
					+ player.getCharacter() + ")");
		}
	}

	public void notifyPlayersAdapter() {
		adapter.notifyDataSetChanged();
	}

	private void setVisibleState(int stateWait) {
		switch (stateWait) {
		case STATE_WAIT_QUESTION:
			textViewStatus.setText("Waiting for question");
			waitingLayout.setVisibility(View.VISIBLE);
			questionLayout.setVisibility(View.GONE);
			answerLayout.setVisibility(View.GONE);
		case STATE_WAIT_ANSWERS:
			textViewStatus.setText("Waiting for answers");
			waitingLayout.setVisibility(View.VISIBLE);
			questionLayout.setVisibility(View.GONE);
			answerLayout.setVisibility(View.GONE);
			break;
		case STATE_ANSWER:
			waitingLayout.setVisibility(View.GONE);
			questionLayout.setVisibility(View.GONE);
			answerLayout.setVisibility(View.VISIBLE);
			break;
		case STATE_QUESTION:
			waitingLayout.setVisibility(View.GONE);
			questionLayout.setVisibility(View.VISIBLE);
			answerLayout.setVisibility(View.GONE);
			break;
		default:
			waitingLayout.setVisibility(View.GONE);
			questionLayout.setVisibility(View.GONE);
			answerLayout.setVisibility(View.GONE);
			break;
		}

	}

	public void notifyQuestionsAdapter() {
		adapterQuestion.notifyDataSetChanged();
	}

	public void showEnd() {
		setVisibleState(-1);
	}
}
