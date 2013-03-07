/*******************************************************************************
 * CDDL HEADER START
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0 only (the "License"). You may not use this file except in compliance with the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. 
 * See the License for the specific language governing permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 * CDDL HEADER END
 * 
 * Copyright 2006-2013 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH and Max-Planck-Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 ******************************************************************************/
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
