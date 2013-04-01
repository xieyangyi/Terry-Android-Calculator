package org.Terry.Android;
import java.util.LinkedList;


import android.view.View;
import android.widget.Button;

public abstract class ContinuousCalculatorWithMath extends BasicCalculator {
  public boolean isBraketButtonPressed;

	public void onClickSolution(View view) {
		super.onClickSolution(view);
		buttonMeaning = ((Button)view).getText().toString(); 
		// constant buttons such as PI, E and random
		if((buttonMeaning.equals("PI") || buttonMeaning.equals("E") || buttonMeaning.equals("random")) 
				&& !isNumberButtonPressed  && !isRightBracketButtonPressed) {
			if(isNumberInputFinished)
				displayString = displayString.substring(0, displayString.length() - 6);
			
			if(buttonMeaning.equals("PI"))
				dval = (int)(Math.PI * 1e4) / (double)1e4;    // restore 4 decimals
			else if(buttonMeaning.equals("E"))
				dval = (int)(Math.E * 1e4) / (double)1e4;
			else if(buttonMeaning.equals("random"))
				dval = (int)(Math.random() * 1e4) / (double)1e4;
			
			isCEButtonPressed = false;
			isNumberInputFinished = true;
			isLeftBracketButtonPressed = false;
			displayString += Double.toString(dval);
			setLblDisplayText(displayString);
		}
		// calculate buttons include single operation and double operation
		else if((buttonMeaning.equals("sin") || buttonMeaning.equals("cos") || buttonMeaning.equals("tan") || buttonMeaning.equals("n!")
				|| buttonMeaning.equals("abs") || buttonMeaning.equals("asin") || buttonMeaning.equals("acos")
				|| buttonMeaning.equals("atan") || buttonMeaning.equals("exp") || buttonMeaning.equals("ln")|| buttonMeaning.equals("log10") 
				|| buttonMeaning.equals("x^2") ||buttonMeaning.equals("x^y") || buttonMeaning.equals("x^(1/y)")) 
				&& (isNumberButtonPressed || isCalculateButtonPressed || isMRButtonPressed|| isNumberInputFinished) 
				&& !isLeftBracketButtonPressed) {
			operator = new Operator(buttonMeaning);
			if(isSingleOperatorButtonPressed) {
				displayString = displayString.substring(0, displayString.length() - operatorStringLength - 
						double2string(valLinkedList.getLast()).length());
				valLinkedList.removeLast();
				displayString += double2string(dval);
			}
			if(operator.isSingleOperator()) {    // single operator
				valLinkedList.add(dval);
				isCEButtonPressed = false;
				isNumberButtonPressed = true;
				isSingleOperatorButtonPressed = true;
				isCalculateButtonPressed = true;
				operatorStringLength = buttonMeaning.length() + 4; // " (" and " )" included
				/* display the string on JLabel */
				if(buttonMeaning.equals("n!")) {
					displayString += "!" + " ";
					operatorStringLength = 2;
				}
				else if(buttonMeaning.equals("x^2")) {
					displayString += "^2" + " ";
					operatorStringLength = 3;
				}
				else {
					displayString = displayString.substring(0, displayString.length() - double2string(dval).length());
					displayString += buttonMeaning + " (" + double2string(dval) + ") ";
				}						
				setLblDisplayText(displayString);
				dval = calculate(dval, operator.getOperatorName());
				if(isCalculateError) {    // solve calculate error
					displayString = "Error!";
					setLblDisplayText(displayString);
					reset();
				}
			}
			else {
				if(!isNumberButtonPressed && !isMRButtonPressed ) {   
					// delete the buttonString last time
					displayString = displayString.substring(0, displayString.length() - operatorStringLength);
					opLinkedList.removeLast();
				}
				else 
					valLinkedList.add(dval);
				
				opLinkedList.add(operator);
				operatorStringLength = buttonMeaning.length() + 2;  // there are 2 " " before and after buttonMeaning when display
				// set some parameters for next input
				dval = 0;			
				isDecimalButtonPressed = false;
				decimalPlace = 0;
				isNumberButtonPressed = false;    // use for "+/-" and "="
				isNumberInputFinished = false;
				isSingleOperatorButtonPressed = false;
				isRightBracketButtonPressed = false;
				isCalculateButtonPressed = true;
				if(buttonMeaning.equals("x^y")) {
					displayString += "^";
					operatorStringLength = 1;
				}					
				else if(buttonMeaning.equals("x^(1/y)"))
					displayString += " x^(1/y) ";
				setLblDisplayText(displayString);
			}
		}
		// buttons for radiant and degree change 
		else if((buttonMeaning.equals("radian") || buttonMeaning.equals("degree"))
				&& (isNumberButtonPressed || isNumberInputFinished || isMRButtonPressed) && !isLeftBracketButtonPressed) {
			displayString = displayString.substring(0, displayString.length() - double2string(dval).length());
			if(buttonMeaning.equals("radian")) {
				dval = Math.toRadians(dval);
				((Button)this.findViewById(R.id.buttonRadian)).setText("degree");
			}
			else if(buttonMeaning.equals("degree")) {
				dval = Math.toDegrees(dval);
				((Button)this.findViewById(R.id.buttonRadian)).setText("radian");
			}
			isLeftBracketButtonPressed = false;
			displayString += double2string(dval);
			setLblDisplayText(displayString);
		}
		// bracket buttons
		else if(buttonMeaning.equals("(") || buttonMeaning.equals(")")) {
			if(buttonMeaning.equals(")") && !isLeftBracketButtonPressed) {
				operator = new Operator(buttonMeaning);
				opLinkedList.add(operator);
				valLinkedList.add(dval);
				dval = bracketCalculate(valLinkedList, opLinkedList);
				displayString += double2string(dval);
				isRightBracketButtonPressed = true;
				setLblDisplayText(displayString);
			}	
			else if(buttonMeaning.equals("(") && !isRightBracketButtonPressed && isCalculateButtonPressed) {
				operator = new Operator(buttonMeaning);
				opLinkedList.add(operator);
				displayString += " " + buttonMeaning + " ";
				isLeftBracketButtonPressed = true;
				setLblDisplayText(displayString);
			}				
		}	
	}
	/* override the calculate method for double operator */
	public double calculate(double firstVal, double secondVal, String operator) {	
		try {
			if(operator.equals("x^y"))
				return Math.pow(firstVal, secondVal);
			else if(operator.equals("x^(1/y)"))
				return Math.pow(firstVal, 1/secondVal);
			else    // return method in father when operator is not matched in child
				return super.calculate(firstVal, secondVal, operator);
		}
		catch(ArithmeticException ex) {
			isCalculateError = true;
			return -1;
		}
	}
	/* override the calculate method for single operator */
	public double calculate(double val, String operator) {
		try {
			if(operator.equals("sin"))
				return Math.sin(Math.toRadians(val));
			else if(operator.equals("cos"))
				return Math.cos(Math.toRadians(val));
			else if(operator.equals("tan"))
				return Math.tan(Math.toRadians(val));
			else if(operator.equals("abs"))
				return Math.abs(val);
			else if(operator.equals("asin"))
				return Math.toDegrees(Math.asin(val));
			else if(operator.equals("acos"))
				return Math.toDegrees(Math.acos(val));
			else if(operator.equals("atan"))
				return Math.toDegrees(Math.atan(val));
			else if(operator.equals("exp"))
				return Math.exp(val);
			else if(operator.equals("ln"))
				return Math.log(val);
			else if(operator.equals("log10"))
				return Math.log10(val);
			else if(operator.equals("x^2"))
				return Math.pow(val, 2);
			else if(operator.equals("n!")) {
				if(val - (int)val > -1e-9 && val - (int)val < 1e-9) // factorial is only for Integer
					return factorial((int)val);
				else 
					throw new IllegalArgumentException("n! is not for double");
			}
			else    // return method in father when operator is not matched in child
				return super.calculate(val, operator);	
		}
		catch(ArithmeticException ex) {
			isCalculateError = true;
			return -1;
		}
		catch(IllegalArgumentException ex) {
			isCalculateError = true;
			return -1;
		}
	}
	public static double factorial(int val) throws IllegalArgumentException {
		double result = 1;
		if(val < 0)
			throw new IllegalArgumentException("argument can not be negative");
		else if(val == 0)
			return 1;
		else {
			for(int i = 1; i <= val; i++)
				result *= i;
			return result;
		}
	}
	public void reset() {    // overflow the function of reset()
		super.reset();
		((Button)this.findViewById(R.id.buttonRadian)).setText("radian");  // change the text of button to default value of "radian"
	}
	
}
