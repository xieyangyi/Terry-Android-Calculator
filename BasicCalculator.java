package org.Terry.Android;

import java.util.LinkedList;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public abstract class BasicCalculator extends CalculatorComponents{
  
	protected String buttonMeaning;            // the meaning of one button, it is the text of the button
	protected double dval = 0;                 // the input data
	protected int decimalPlace = 0;            // the place of the decimal, for input double data
	protected Operator operator;            // save the sign of the operation
	protected boolean isDecimalButtonPressed = false;       // check if the decimal button is pressed
	protected boolean isCalculateError;    // to judge that if there is any error in calculation
	protected String displayString = " ";   // what to display on the Label
	protected boolean isNumberButtonPressed = false;    
	protected boolean isCalculateButtonPressed = false;    // check if the operation buttons is pressed, singleOperator included
	protected boolean isCEButtonPressed = false;    // check if the CE button for clearing the input numbers is pressed
	protected boolean isNumberInputFinished = false;    // use for some constant input such as PI and E
	protected double memoryData = 0;    // to storage the data for memory
	protected boolean isHaveMemoryData = false;    // if the calculator has storaged any numbers
	protected boolean isMRButtonPressed;    // check if there is memory data has been read out
	protected LinkedList<Double> valLinkedList = new LinkedList();    // to storage the input doubles
	protected LinkedList<Operator> opLinkedList = new LinkedList();    // to storage the operators, singleOperator is not included
	protected int operatorStringLength;    // the length of the display string of operator, singleOperator included
	protected boolean isSingleOperatorButtonPressed = false;    // check if there is any singleOperator has been pressed
	protected boolean isRightBracketButtonPressed = false;
	protected boolean isLeftBracketButtonPressed = false;
	public double result;                 // get the result of the calculation
	private boolean isFinalCalculateButtonPressed;
	
	public void onClickSolution(View view) {
		
			buttonMeaning =((Button)view).getText().toString();  
			/** number buttons include decimal button . and sign button +/- */
			if((buttonMeaning.equals("0") || buttonMeaning.equals("1") || buttonMeaning.equals("2") || buttonMeaning.equals("3") || 
					buttonMeaning.equals("4") || buttonMeaning.equals("5") || buttonMeaning.equals("6") || buttonMeaning.equals("7") || 
					buttonMeaning.equals("8") || buttonMeaning.equals("9") || buttonMeaning.equals(".") || buttonMeaning.equals("+/-") )
					&& !isSingleOperatorButtonPressed && !isRightBracketButtonPressed ) {
				if(buttonMeaning.equals(".") && !isNumberInputFinished) {   
					if(!isNumberButtonPressed ) 
						displayString += "0";
					if(!isDecimalButtonPressed) 
						displayString += buttonMeaning;
					isDecimalButtonPressed = true;
				}
				else if(buttonMeaning.equals("+/-")) {
					dval = -dval;
					int index = displayString.lastIndexOf(" ");  // find the place where to insert the sign
					displayString = displayString.substring(0, index + 1) + double2string(dval);			
				}
				else if(!isNumberInputFinished){
					formDouble(Integer.parseInt(buttonMeaning));
					displayString += buttonMeaning;
				}
				isNumberButtonPressed = true;
				isCEButtonPressed = false;
				isLeftBracketButtonPressed = false;
				setLblDisplayText(displayString);
			}
			/** calculate buttons + - * '/' % sqrt 1/x */
			else if((buttonMeaning.equals("+") || buttonMeaning.equals("-")  || buttonMeaning.equals("*") || 
					buttonMeaning.equals("/") || buttonMeaning.equals("%") || buttonMeaning.equals("sqrt") ||
					buttonMeaning.equals("1/x")) && (isNumberButtonPressed || isCalculateButtonPressed || isMRButtonPressed
					|| isNumberInputFinished) && !isLeftBracketButtonPressed) {
				operator = new Operator(buttonMeaning);
				if(isSingleOperatorButtonPressed) {
					displayString = displayString.substring(0, displayString.length() - operatorStringLength - 
							double2string(valLinkedList.getLast()).length());
					valLinkedList.removeLast();
					displayString += double2string(dval);
				}
				if(operator.isSingleOperator()) {    // single operator
					valLinkedList.add(dval);
					dval = calculate(dval, operator.getOperatorName());
					if(isCalculateError) {    // solve calculate error
						displayString = "Error!";
						setLblDisplayText(displayString);
						reset();
					}
					else {
						isCEButtonPressed = false;
						isNumberButtonPressed = true;
						isSingleOperatorButtonPressed = true;
						isCalculateButtonPressed = true;
						operatorStringLength = buttonMeaning.length() + 2;
						displayString += " " + buttonMeaning + " ";
						setLblDisplayText(displayString);
					}
				}
				else {    // double operator
					if((!isNumberButtonPressed && !isMRButtonPressed && !isNumberInputFinished)) {   // delete the buttonString last time
						displayString = displayString.substring(0, displayString.length() - operatorStringLength);
						opLinkedList.removeLast();
					}
					else
						valLinkedList.add(dval);   // add input number to linkedList
						
					opLinkedList.add(operator);    // add operator to linkedList
					operatorStringLength = buttonMeaning.length() + 2;
					dval = 0;			
					isDecimalButtonPressed = false;
					decimalPlace = 0;
					isNumberButtonPressed = false;    // use for "+/-" and "="
					isCalculateButtonPressed = true;
					isNumberInputFinished = false;
					isSingleOperatorButtonPressed = false;
					isRightBracketButtonPressed = false;
					displayString += " " + buttonMeaning + " ";
					setLblDisplayText(displayString);
				}
			}
			/** button = only works when calculate button is pressed and number button pressed */
			else if(buttonMeaning.equals("=") && isCalculateButtonPressed && (isNumberButtonPressed 
					|| isNumberInputFinished || isMRButtonPressed)  && !isLeftBracketButtonPressed) {
				if(isSingleOperatorButtonPressed) {    // solve the display problem that press "=" just after singleOperator
					displayString = displayString.substring(0, displayString.length() - operatorStringLength - 
							double2string(valLinkedList.getLast()).length());
					valLinkedList.removeLast();
					displayString += double2string(dval);
				}
				valLinkedList.add(dval);
				result = continuousCalculate(valLinkedList, opLinkedList);
				displayString += " = " + double2string(result);
				setLblDisplayText(displayString);
				isFinalCalculateButtonPressed = true;
				reset();
			}
			/** button C, CE and Reset */
			else if(buttonMeaning.equals("C") || buttonMeaning.equals("CE") || buttonMeaning.equals("Reset")) {
				if(buttonMeaning.equals("C") && (isNumberButtonPressed || isMRButtonPressed) && !isRightBracketButtonPressed) {  
					// C is only used just after input buttons are pressed
					cButtonDouble();
					displayString = displayString.substring(0, displayString.length() - 1);
					setLblDisplayText(displayString);
				}					
				else if(buttonMeaning.equals("CE") && (isNumberButtonPressed || isNumberInputFinished || isMRButtonPressed)
						 && !isRightBracketButtonPressed) {
					if(!isCEButtonPressed) {
						displayString = displayString.substring(0, displayString.length() - double2string(dval).length());
						setLblDisplayText(displayString);
						dval = 0;
						isDecimalButtonPressed = false;
						decimalPlace = 0;
						isCEButtonPressed = true;
  						isNumberInputFinished = false;
						isNumberButtonPressed = false;
					}
				}				
				else if(buttonMeaning.equals("Reset")){
					reset();
					setLblDisplayText("0");
				}
			}
			/** buttons for memory */
			else if(buttonMeaning.equals("MC") || buttonMeaning.equals("MR") || buttonMeaning.equals("MS") || buttonMeaning.equals("M+")) {
				if(buttonMeaning.equals("MS") && (dval > 0 || isFinalCalculateButtonPressed)) {
					if(dval > 0)
						memoryData = dval;    // save the pressed variables
					else if(isFinalCalculateButtonPressed)
						memoryData = result;    // save calculation results
					setLblMemoryDisplayText("M");
					isHaveMemoryData = true;
				}
				else if(buttonMeaning.equals("MC") && isHaveMemoryData) {
					memoryData = 0;
					setLblMemoryDisplayText(" ");
					isHaveMemoryData = false;
				}
				else if(buttonMeaning.equals("MR") && isHaveMemoryData && !isSingleOperatorButtonPressed  
						&& !isRightBracketButtonPressed) {
					if(!isCalculateButtonPressed )
						displayString = displayString.substring(0, displayString.length() - double2string(dval).length());
					dval = memoryData;
					displayString += double2string(dval);
					setLblDisplayText(displayString);
					isMRButtonPressed = true;
					isLeftBracketButtonPressed = false;
				}
				else if(buttonMeaning.equals("M+") && isHaveMemoryData && dval > 0) {
					displayString = displayString.substring(0, displayString.length() - double2string(dval).length());
					memoryData += dval;
					dval = memoryData;
					displayString += double2string(dval);
					setLblDisplayText(displayString);
					isLeftBracketButtonPressed = false;
				}
			}
	} 
	/** make the input number buttons to form a number which is defined as double by the program */
	public double formDouble(int i) {
		if(isDecimalButtonPressed)  {       // dval is a double
			if(dval >= 0)    // must use -= when dval is negative while use += when dval is positive
				dval += (double)i / Math.pow(10, ++decimalPlace);
			else
				dval -= (double)i / Math.pow(10, ++decimalPlace);
		}
		else {        // dval is an integer
			if(dval >= 0)
				dval = dval * 10 + i;
			else
				dval = dval * 10 - i;
		}
		return dval;
	}
	/** the method for pressing c button */
	public double cButtonDouble() {
		if(isDecimalButtonPressed)  {       // dval is a double
			dval = (double)((int)(dval * Math.pow(10, --decimalPlace))) / Math.pow(10, decimalPlace) ;
			if(decimalPlace == 0) {
				isDecimalButtonPressed = false;
				displayString = displayString.substring(0, displayString.length() - 1);    // delete the symbol "."
			}
		}
		else
			dval = (int)dval / 10;
        if(dval == 0)
        	isNumberButtonPressed = false;    // solve the bug that it only displays with "." if dval = 0 after C button pressed
		return dval;
	}
	/** the method of calculation for double operator calculate */
	public double calculate(double firstVal, double secondVal, String operator) {
		try {
			if(operator.equals("+"))
				return firstVal + secondVal;
			else if(operator.equals("-"))
				return firstVal - secondVal;
			else if(operator.equals("*"))
				return firstVal * secondVal;
			else if(operator.equals("/")) {
				if(secondVal > -1e-9 && secondVal < 1e-9)     // double divide 0 is not error in JAVA
					throw new ArithmeticException("0 can not be divided");
				else
					return firstVal / secondVal;
			}
			else if(operator.equals("%"))
				return firstVal % secondVal;
			else 
				throw new IllegalArgumentException("calculate operation is not found");
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
	/** the method of calculation for single operator calculate */
	public double calculate(double val, String operator) {
		try {
			if(operator.equals("sqrt"))
				return Math.sqrt(val);
			else if(operator.equals("1/x")) {
				if(val > -1e-9 && val < 1e-9) 
					throw new ArithmeticException("can not calculate 1/x for 0");
				else
					return 1/val;
			}
			else 
				throw new IllegalArgumentException("calculate operation is not found");
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
	/** to solve the problem that int is always displayed as double, for example 2 is displayed as 2.0 */
	public String double2string(double dval) {
		if(isCalculateError) {
			return "Error!";
		}
		else {
			if(dval - (int)dval > -1e-9 && dval - (int)dval < 1e-9)
				return Integer.toString((int)dval);			
			else 
				return "" + Double.parseDouble(String.format("%.5f", dval));    // display only 5 number after decimal
		}
	}
	/** reset after "reset" button and "=" button */
	public void reset() {
		dval = 0;
		decimalPlace = 0;
		isCalculateError = false;
		isDecimalButtonPressed = false;
		displayString = " ";		
		isCalculateButtonPressed = false;
		isNumberButtonPressed = false;
		isCEButtonPressed = false;
		isNumberInputFinished = false;
		isMRButtonPressed = false;
		valLinkedList.clear();
		opLinkedList.clear();
		operatorStringLength = 0;
		isSingleOperatorButtonPressed = false;		
		isRightBracketButtonPressed = false;
		isLeftBracketButtonPressed = false;
	}
	/** solve the problem for continuous calculate */
	public double continuousCalculate(LinkedList<Double> valList, LinkedList<Operator> opList) {			
		for(int priority = 2; priority >= 0; priority--) {
			for(int i = 0; i < opList.size(); i++) {
				if(opList.get(i).getPriority() == priority) {
					double calResult = calculate(valList.get(i), valList.get(i+1), opList.get(i).getOperatorName());
					valList.set(i, calResult);
					valList.remove(i+1);
					opList.remove(i);
					i--;
				}
			}
		}
		return valList.get(0);	
	}
	public double bracketCalculate(LinkedList<Double> valList, LinkedList<Operator> opList) {
		// clear the string between "(" and ")" for display
		int index = displayString.lastIndexOf("(");
		displayString = displayString.substring(0, index - 1);   // delete " " before "("
		
		// calculate the numbers between "(" and ")"
		LinkedList<Double> valListForBracket = new LinkedList();
		LinkedList<Operator> opListForBracket = new LinkedList();
		double result = 0;
		for(int i = opList.size() - 1; i >= 0; i--) {
			if(opList.get(i).getOperatorName().equals("(")) {
				// form the new list
				for(int bracketPosition = i + 1; bracketPosition <= opList.size() - 2; bracketPosition++)
					opListForBracket.add(opList.get(bracketPosition));
				for(int bracketPosition = valList.size() - opList.size() + i + 1; bracketPosition < valList.size(); bracketPosition++)
					valListForBracket.add(valList.get(bracketPosition));
				// calculate between "(" and ")"
				result = continuousCalculate(valListForBracket, opListForBracket);
				// remove the list between "(" and ")"
				int opListSize = opList.size();
				for(int bracketPosition = i; bracketPosition < opList.size();)    
					opList.remove(bracketPosition);
				for(int bracketPosition = valList.size() - opListSize + i + 1; bracketPosition < valList.size();)
					valList.remove(bracketPosition);
				break;
			}
		}
		return result;
	}
	/** define the class for Operator */
	public class Operator {
		private String operatorName;
		private boolean isSingleOperator;
		private boolean isBraket;
		private int priority;
		
		public Operator() {
			this("+");    // "+" is the default operator
		}
		public Operator(String operatorName) {
			this.operatorName = operatorName;
			this.isSingleOperator = isSingleOperator();
			this.priority = getPriority();
			this.isBraket = isBraket();
		}
		public String getOperatorName() {
			return operatorName;
		}
		public boolean isSingleOperator() {
			if(operatorName.equals("sin") || operatorName.equals("cos") || operatorName.equals("tan") || operatorName.equals("n!") 
				|| operatorName.equals("abs") || operatorName.equals("asin") || operatorName.equals("acos")
				|| operatorName.equals("atan") || operatorName.equals("exp") || operatorName.equals("ln") || operatorName.equals("log10") 
				|| operatorName.equals("x^2") || operatorName.equals("sqrt") || operatorName.equals("1/x"))
				isSingleOperator = true;
			else if(operatorName.equals("x^y") || operatorName.equals("x^(1/y)") || operatorName.equals("+") || operatorName.equals("-") 
					|| operatorName.equals("*") || operatorName.equals( "/") || operatorName.equals("%") 
					|| operatorName.equals("(") || operatorName.equals(")"))
				isSingleOperator = false;
			return isSingleOperator;
		}
		public int getPriority() {
			if(operatorName.equals( "+" ) || operatorName.equals("-"))
				priority = 0;
			else if(operatorName.equals("*") || operatorName.equals("/") || operatorName.equals("%"))
				priority = 1;
			else if(operatorName.equals("x^y") || operatorName.equals("x^(1/y)"))
				priority = 2;
			else if(operatorName.equals("(") || operatorName.equals(")"))
				priority = 3;
			return priority;
		}
		public boolean isBraket() {
			if(operatorName.equals("(") || operatorName.equals(")"))
				isBraket = true;
			else
				isBraket = false;
			return isBraket;
		}
		
	}
}

