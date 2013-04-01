package org.Terry.Android;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class Calculator extends ContinuousCalculatorWithMath implements OnClickListener{  
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);      
        for(int i = 0; i < 47; i++) 
         	btn[i].setOnClickListener(this);    //   set click listeners for buttons
    }
    public void onClick(View view) {
    	onClickSolution(view);
    }
}
