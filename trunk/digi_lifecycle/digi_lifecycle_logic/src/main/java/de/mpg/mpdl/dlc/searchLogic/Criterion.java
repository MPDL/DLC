package de.mpg.mpdl.dlc.searchLogic;


public abstract class Criterion {
	public enum Operator
	{
		AND, OR, NOT
	}
	
	protected Operator operator = Operator.AND;
		
	protected String value;
	
	protected int openBracket = 0;
	
	protected int closeBracket = 0;
	
	protected String connector = "=";

	
	public Criterion()
	{
		
	}
	
	public Criterion(Criterion toBeCloned)
	{
		this.operator = toBeCloned.operator;
		this.value = toBeCloned.value;
		this.openBracket = toBeCloned.openBracket;
		this.closeBracket = toBeCloned.closeBracket;
		this.connector = toBeCloned.connector;
	}
	
	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getOpenBracket() {
		return openBracket;
	}

	public void setOpenBracket(int openBracket) {
		this.openBracket = openBracket;
	}

	public int getCloseBracket() {
		return closeBracket;
	}

	public void setCloseBracket(int closeBracket) {
		this.closeBracket = closeBracket;
	}
	
	
	public String getConnector() {
		return connector;
	}

	public void setConnector(String connector) {
		this.connector = connector;
	}

}
