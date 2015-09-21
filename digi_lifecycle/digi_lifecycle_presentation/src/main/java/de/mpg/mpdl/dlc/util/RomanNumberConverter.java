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
 * Copyright 2011-2015 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH and Max-Planck-Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 *
 * Initial Developer (as defined by CDDL 1.0) of the DLC-Software are five
 * institutes and facilities of the Max Planck Society: Bibliotheca Hertziana 
 * (Rome), Kunsthistorisches Institut in Florenz (Florence), Max Planck Institute
 * for European Legal History (Frankfurt a.M.), Max Planck Institute for Human
 * Development (Berlin), and the Max Planck Digital Library (Munich). Users of the
 * DLC-Software are requested to include a "powered by DLC" on their webpage,
 * linking to the DLC documentation (http://dlcproject.wordpress.com/).
 ******************************************************************************/
package de.mpg.mpdl.dlc.util;

import java.util.Arrays;
import java.util.List;

/**
 * Utiltity class to convert arabic to roman numbers.
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class RomanNumberConverter
{
    private final static int[] arabic = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
    private final static String[] roman = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"}; 
    private final static String[] romanLower = {"m", "cm", "d", "cd", "c", "xc", "l", "xl", "x", "ix", "v", "iv", "i"}; 
    
    private static StringBuffer recursiveConversion(int number, int index, StringBuffer buffer, boolean lowerCase)
    {
       
            if (number==0)
            {
                return buffer;
            }
            else if (number>=arabic[index])
            {
            	String romanNumber = null;
            	
            	if(lowerCase)
            	{
            		romanNumber = romanLower[index];
            	}
            	else
            	{
            		romanNumber = roman[index];
            	}
            	
            	
                return recursiveConversion(number-arabic[index], index, buffer.append(romanNumber), lowerCase);
            }
            else
            {
                return recursiveConversion(number, index+1, buffer, lowerCase);
            }
        
    }
    
    
  
    
    
    /**
     * Converts the given integer into a roman representation
     * @param number The interger to be converted
     * @return The roman representation
     */
    public static String convert(int number, boolean lowerCase)
    {
        if (number==0)
        {
            return "0";
        }
        else
        {
            StringBuffer romanianNumber = recursiveConversion(Math.abs(number), 0, new StringBuffer(), lowerCase);
            return romanianNumber.toString();
        }
        
        
    }
    /*
    public static boolean isLowerCase(String romanNumber) throws NumberFormatException
    {
    	List<String> romanUpperCaseNumbers = Arrays.asList(roman);
    	List<String> romanLowerCaseNumbers = Arrays.asList(romanLower);
    	if(romanNumber != null)
    	{
	
			for(int i=0; i< romanNumber.length(); i++)
				{
					String currentSubString;
					if(i != romanNumber.length()-1)
					{
	
						currentSubString =  romanNumber.substring(i, i + 2);
					}
					else
					{
						currentSubString =  romanNumber.substring(i, i + 1);
					}
					
					
					
					
					if(romanUpperCaseNumbers.contains(currentSubString))
					{
						return false;
					}
					else if (romanLowerCaseNumbers.contains(currentSubString))
					{
						return true;
					}
				
					
					else
					{
						currentSubString =  romanNumber.substring(i, i + 1);
						if(romanUpperCaseNumbers.contains(currentSubString))
						{
							return false;
						}
						else if (romanLowerCaseNumbers.contains(currentSubString))
						{
							return true;
						}
						else
						{
							throw new NumberFormatException("The given roman number is invalid");
						}
					}
		
					
				
				}
    	}
    	else
    	{
    		throw new NumberFormatException("The given roman number is invalid");
    	}
    	return false;
    	
    	
    }
    
    */
    public static int convert(String romanNumber) throws NumberFormatException
    {
    	List<String> romanNumbers = Arrays.asList(roman);
    	int value=0;
    	
    	if(romanNumber != null)
    	{
	    	String upperCase = romanNumber.toUpperCase();
	    	
	    	
	    	try {
				for(int i=0; i< upperCase.length(); i++)
				{
					String currentSubString;
					if(i != upperCase.length()-1)
					{
	
						currentSubString =  upperCase.substring(i, i + 2);
					}
					else
					{
						currentSubString =  upperCase.substring(i, i + 1);
					}
					
					
					
					
					int index = romanNumbers.indexOf(currentSubString);
					if(index != -1)
					{
						value = value + arabic[index];
						i++;
					}
					else
					{
						currentSubString =  upperCase.substring(i, i + 1);
						index = romanNumbers.indexOf(currentSubString);
						value = value +arabic[index];
					}
	
				}
			} catch (Exception e) {
				throw new NumberFormatException("The given roman number is invalid");
			}
    	}

    	return value;
    }
    
    public static void main(String[] args)
    {
        //System.out.println(convert(1000));
    }
}
