package hu.zsoltkiss.demo.activity;

import hu.zsoltkiss.demo.R;
import hu.zsoltkiss.demo.fragment.GameboardFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity implements GameboardFragment.GameProgressListener {
	
	private GameboardFragment gameboard;
	
	private Button btnNewGame;
	
	private TextView tvStepCounter;
	
	private int steps;
	
	/***********************************************************************************
	 * ACTIVITY LIFECYCLE
	 ***********************************************************************************/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initBoard();
		initButton();
	}
	
	/***********************************************************************************
	 * INTERFACE IMPLEMENTATION: GameProgressListener
	 ***********************************************************************************/
	@Override
	public void incrementStepCount() {
		if (tvStepCounter == null) {
			tvStepCounter = (TextView)findViewById(R.id.tvStepCounter);
		}
		
		steps++;
		
		tvStepCounter.setText(String.valueOf(steps));
		
	}
	
	/***********************************************************************************
	 * PRIVATE METHODS
	 ***********************************************************************************/
	
	private void initBoard() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction tran = fragmentManager.beginTransaction();
		
		gameboard = new GameboardFragment(); 
		gameboard.setProgressListener(this);
		
		tran.add(R.id.boardFragmentHolder, gameboard);
		tran.commit();
	}

	private void initButton() {
		if (findViewById(R.id.btnNewGame) != null) {
			btnNewGame = (Button)findViewById(R.id.btnNewGame);
			
			btnNewGame.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					gameboard.newGame();
					steps = 0;
					tvStepCounter.setText(String.valueOf(steps));
				}
			});
			
			if (findViewById(R.id.btnCheat) != null) {
				Button btnCheat = (Button)findViewById(R.id.btnCheat);
				
				btnCheat.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						gameboard.cheat();
						
					}
				});
			}
		}
	}

	
	
}
