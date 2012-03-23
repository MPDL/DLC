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
