package org.Terry.Android;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.text.method.ScrollingMovementMethod;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public abstract class CalculatorComponents extends Activity {
  protected Button[] btn = new Button[47] ;	
	private TextView resultDisplayText ;
	private TextView displayM;
	protected MenuInflater calculatorMenu;
	private int width, height;
	
	public void onCreate(Bundle savedInstanceState) {
//		int width = 0, height = 0;
		super.onCreate(savedInstanceState);
    	setContentView(R.layout.main); 
    	/* find the views by their ids */
		for(int i = 0; i < 47; i++) {
	        btn[i] = (Button)this.findViewById(R.id.buttonMC + i);    // find buttons by their ids
	    }
		resultDisplayText= (TextView)this.findViewById(R.id.displayCalculationResult);
		displayM = (TextView) this.findViewById(R.id.displayM);		
		// get the size of screen 
		Display display = this.getWindowManager().getDefaultDisplay();    // get the view of the screen
    	// portrait orientation of the monitor
		if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			width = display.getWidth();    // get the width of the screen
			height=display.getHeight();  // get the height of the screen	
		}
		// landscape orientation of monitor
    	else if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
    		width = (int) (display.getWidth() * 0.5);    
    		height=(int) (display.getHeight() * 1.6);  
    	}
		// set the different text size of the different buttons as the length of their text
	    for(int i = 0; i < 47; i++) {
	        btn[i].setHeight((int)(height * 0.07));
		    btn[i].setWidth((int)(width * 0.17));
	    	switch(btn[i].getId()) {
	    	case R.id.button0 :
	    	case R.id.button1:
	    	case R.id.button2:
	    	case R.id.button3:
	    	case R.id.button4:
	    	case R.id.button5:
	    	case R.id.button6:
	    	case R.id.button7:
	    	case R.id.button8:
	    	case R.id.button9:
	    	case R.id.buttonCalculate:
	    		btn[i].setTextSize((float) (height * 0.02));
	    		break;
	    	case R.id.buttonLog10:
	    	case R.id.buttonReset:
	    	case R.id.buttonRadian:
	    	case R.id.buttonMulSqrt:
	    	case R.id.buttonRandom:
	    		btn[i].setTextSize((float) (height * 0.011));
	    		break;
	    	default:
		    	btn[i].setTextSize((float) (height * 0.015));	
	    	}
	    }
	    // set the height and text size of the two text views
	    resultDisplayText.setText("0");
	    resultDisplayText.setTextSize((float) (height * 0.03));	
	    resultDisplayText.setMovementMethod(ScrollingMovementMethod.getInstance());    // to display the extra lines
	    if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) 
	    	resultDisplayText.setHeight((int)(height * 0.11));
    	else if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) 
    		resultDisplayText.setHeight((int)(height * 0.055));
    	
	    calculatorMenu = new MenuInflater(this);    // create menu in calculator
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		calculatorMenu.inflate(R.menu.my_menu, menu);    // create menu
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.menu_exit:
			exitAlert("Do you want to exit?");
			break;
		case R.id.menu_about:
			aboutAlert("This is developed by Terry Xie!");
			break;
		}
		return true;
	}
	
	private void aboutAlert(String msg) {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(msg);
		builder.setCancelable(false);
		builder.setPositiveButton("Return", new DialogInterface.OnClickListener() {			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				return;
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	private void exitAlert(String msg) {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(msg);
		builder.setCancelable(false);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				return;
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	public void setLblDisplayText(String s) {
		resultDisplayText.setText(s);
	}
	public void setLblMemoryDisplayText(String s) {
		displayM.setText(s);
	}
}
