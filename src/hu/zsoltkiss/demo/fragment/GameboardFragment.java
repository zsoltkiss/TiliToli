package hu.zsoltkiss.demo.fragment;

import hu.zsoltkiss.demo.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

public class GameboardFragment extends Fragment implements OnClickListener {
	
	public interface GameProgressListener {
		public void incrementStepCount();
	}

	private final static String TAG = "GameboardFragment";
	private final static String EMPTY_VALUE = "";

	private final static List<String> GOAL_OF_GAME = Arrays
			.asList(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9",
					"10", "11", "12", "13", "14", "15", "" });

	private List<String> boardState;

	private TableLayout tableLayout;

	private boolean gameOver;
	
	private MediaPlayer mediaPlayer;
	
	private GameProgressListener progressListener;

	/***********************************************************************************
	 * FRAGMENT LIFECYCLE
	 ***********************************************************************************/

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View v = inflater
				.inflate(R.layout.gameboard_fragment, container, false);

		tableLayout = (TableLayout) v;

		initModel();
		shuffle();
		initBoard(false);
		initMediaPlayer();

		return v;
	}
	
	

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if (mediaPlayer != null) {
			mediaPlayer.release();
		}
	}



	/***********************************************************************************
	 * INTERFACE IMPLEMENTATION: OnCLickListener
	 ***********************************************************************************/
	@Override
	public void onClick(View v) {

		if (!gameOver) {

			Log.d(TAG, "TextView with tag " + v.getTag()
					+ " clicked. Content: " + ((TextView) v).getText());

			int emptyValueIndex = emptyValueIndexInModel();

			List<TextView> clickables = getNeighborsOfEmptyCell();

			if (clickables.contains(v)) {
				TextView tvClicked = (TextView) v;
				TextView tvEmpty = (TextView) tableLayout.findViewWithTag("pos"
						+ emptyValueIndex);

				int selectedValueIndex = -1;

				try {
					selectedValueIndex = Integer.parseInt(((String) tvClicked
							.getTag()).substring(3));

					// csere a modellben
					boardState.set(emptyValueIndex, tvClicked.getText()
							.toString());
					boardState.set(selectedValueIndex, EMPTY_VALUE);

					// csere a view-ban
					tvEmpty.setText(tvClicked.getText());
					tvClicked.setText(EMPTY_VALUE);
				} catch (NumberFormatException nfe) {
					nfe.printStackTrace();
				}
				
				if (progressListener != null) {
					progressListener.incrementStepCount();
				}

				if (isGoalAchived()) {
					mediaPlayer.start();
					displayGameOverDialog();
					gameOver = true;
				}

			}
		}

	}
	
	/***********************************************************************************
	 * PUBLIC METHODS
	 ***********************************************************************************/
	public void newGame() {
		gameOver = false;
		shuffle();
		initBoard(true);
	}
	
	public void setProgressListener(GameProgressListener l) {
		this.progressListener = l;
	}

	/***********************************************************************************
	 * PRIVATE METHODS
	 ***********************************************************************************/

	/**
	 * A jatektabla osszes TextView elemet kikeresi es rakoti az
	 * esemenykezeloket. Elozoleg meghivja a shuffle()-t.
	 */
	private void initBoard(boolean updateOnly) {

		for (int i = 0; i < boardState.size(); i++) {
			TextView textView = (TextView) tableLayout.findViewWithTag("pos"
					+ i);
			textView.setText(boardState.get(i));

			if (!updateOnly) {
				textView.setOnClickListener(this);
			}
		}
	}
	
	private void initMediaPlayer() {
		mediaPlayer = MediaPlayer.create(getActivity(), R.raw.success);
	}

	/**
	 * Letrehozza a "modellt" reprezentalo List-et, benne a szamokkal 1-tol 15-ig.
	 */
	private void initModel() {
		boardState = new ArrayList<String>();

		for (int i = 1; i <= 15; i++) {
			boardState.add(String.valueOf(i));
		}

		boardState.add(EMPTY_VALUE);

	}
	
	/**
	 * Osszekeveri a modellben a szamokat.
	 */
	private void shuffle() {
		Collections.shuffle(boardState);
	}

	/**
	 * Visszaadja az aktualis "ures" cella indexet.
	 * 
	 * @return
	 */
	private int emptyValueIndexInModel() {
		return boardState.indexOf(EMPTY_VALUE);
	}

	/**
	 * Visszaadja az ures cella szomszedjait reprezentalo TextView-kat.
	 * 
	 * @param index
	 * @return
	 */
	private List<TextView> getNeighborsOfEmptyCell() {
		int emptyValueIndex = emptyValueIndexInModel();
		List<Integer> neighbors = new ArrayList<Integer>();

		if (emptyValueIndex % 4 == 0) {
			// nulladik oszlopban van. szoba johet: jobbra, le, fel.

			neighbors.add(emptyValueIndex + 1);
			neighbors.add(emptyValueIndex + 4);
			neighbors.add(emptyValueIndex - 4);
		}

		if (emptyValueIndex % 4 == 1 || emptyValueIndex % 4 == 2) {
			// elso vagy masodik oszlopban van. szoba johet: balra, jobbra, le,
			// fel.

			neighbors.add(emptyValueIndex + 1);
			neighbors.add(emptyValueIndex - 1);
			neighbors.add(emptyValueIndex + 4);
			neighbors.add(emptyValueIndex - 4);
		}

		if (emptyValueIndex % 4 == 3) {
			// harmadik oszlopban van. szoba johet: balra, le, fel.

			neighbors.add(emptyValueIndex - 1);
			neighbors.add(emptyValueIndex + 4);
			neighbors.add(emptyValueIndex - 4);
		}

		Log.d(TAG, "empty value index: " + emptyValueIndex
				+ ". Neighbour indexes: " + neighbors);

		List<TextView> clickables = new ArrayList<TextView>();
		for (int n : neighbors) {
			if (n >= 0 && n < boardState.size()) {
				TextView textView = (TextView) tableLayout
						.findViewWithTag("pos" + n);
				clickables.add(textView);
			}
		}

		return clickables;
	}

	private boolean isGoalAchived() {
		Log.d(TAG, "isGoalAchived() ENTER");
		return boardState.equals(GOAL_OF_GAME);
	}

	private void displayGameOverDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		builder.setMessage(R.string.game_over_message).setTitle(
				R.string.game_over_title);

		builder.setPositiveButton(R.string.btn_OK,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					
						// itt nincs tennivalo, az OK gomb klikkre az Android eltunteti a dialog-ot
					}

				});

		AlertDialog dialog = builder.create();

		dialog.show();
	}
	
	@SuppressWarnings("unused")
	public void cheat() {
		boardState = Arrays
				.asList(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9",
						"10", "11", "12", "", "13", "14", "15"});
		
		for (int i = 0; i < boardState.size(); i++) {
			TextView textView = (TextView) tableLayout.findViewWithTag("pos"
					+ i);
			textView.setText(boardState.get(i));

		}
	}
}
