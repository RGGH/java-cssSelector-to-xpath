package org.sam.rosenthal.cssselectortoxpath.utilities;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.sam.rosenthal.cssselectortoxpath.model.CssAttribute;
import org.sam.rosenthal.cssselectortoxpath.model.CssAttributeValueType;
import org.sam.rosenthal.cssselectortoxpath.model.CssElementCombinatorPair;

public class CssElementCombinatorPairsToXpath 
{
	public static Properties VERSION_PROPERTIES=getVersionProperties();
	
	private CssSelectorStringSplitter cssSelectorString=new CssSelectorStringSplitter();
	
	public String cssElementCombinatorPairListConversion(List <CssElementCombinatorPair> elementCombinatorPairs) throws CssSelectorStringSplitterException
	{
		StringBuilder xpathBuilder=new StringBuilder();
		
		xpathBuilder.append("//");
		boolean firstTime=true;
		for(Iterator<CssElementCombinatorPair> cssElementCombinatorPairIterator=elementCombinatorPairs.iterator();
			cssElementCombinatorPairIterator.hasNext();)
		{
			CssElementCombinatorPair elementCombinatorPair = cssElementCombinatorPairIterator.next();
			if(firstTime)
			{
				firstTime=false;
			}
			else
			{
				xpathBuilder.append(elementCombinatorPair.getCombinatorType().getXpath());
			}
			addElementToXpathString(xpathBuilder, elementCombinatorPair);
			convertCssAttributeListToXpath(xpathBuilder,elementCombinatorPair);
		}
		return xpathBuilder.toString();
	}

	private static Properties getVersionProperties() {
		ClassLoader classLoader = CssElementCombinatorPairsToXpath.class.getClassLoader();
		Properties properties= new Properties();
		try {
			properties.load(classLoader.getResourceAsStream("version.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties;
	}

	private void addElementToXpathString(StringBuilder xpathBuilder, CssElementCombinatorPair elementCombinatorPair) {
		String element=elementCombinatorPair.getCssElementAttributes().getElement();
		if(element!=null)
		{
			xpathBuilder.append(element);
		}
		else
		{
			xpathBuilder.append("*");
		}
	}
	
	private void convertCssAttributeListToXpath(StringBuilder xpathBuilder, CssElementCombinatorPair elementCombinatorPair)
	{
		List<CssAttribute> cssAttributeList = elementCombinatorPair.getCssElementAttributes().getCssAttributeList();
		//starts-with(@href, "abc")
		for(CssAttribute cssAttribute: cssAttributeList)
		{
			String name = cssAttribute.getName();
			String value = cssAttribute.getValue();
			if(cssAttribute.getType()==CssAttributeValueType.EQUAL)
			{
				xpathBuilder.append("[");
				exactMatchXpath(xpathBuilder, name, value);
			}
			else if(cssAttribute.getType()==CssAttributeValueType.CARROT_EQUAL)
			{
				xpathBuilder.append("[starts-with(@");
				xpathBuilder.append(name);
				xpathBuilder.append(",\"");
				xpathBuilder.append(value);
				xpathBuilder.append("\")]");
			}
			else if(cssAttribute.getType()==CssAttributeValueType.DOLLAR_SIGN_EQUAL)
			{
				//TODO: implement this when we implement xpath 2.0
//				xpathBuilder.append("[ends-with(@");
//				xpathBuilder.append(cssAttribute.getName());
//				xpathBuilder.append(",\"");
//				xpathBuilder.append(cssAttribute.getValue());
//				xpathBuilder.append("\")]");
				
				xpathBuilder.append("[substring(@");
				xpathBuilder.append(name);
				xpathBuilder.append(",string-length(@");
				xpathBuilder.append(name);
				xpathBuilder.append(")-string-length(\"");
				xpathBuilder.append(value);
				xpathBuilder.append("\")+1)=\"");
				xpathBuilder.append(value);
				xpathBuilder.append("\"]");
			}
			else if(cssAttribute.getType()==CssAttributeValueType.STAR_EQUAL)
			{
				xpathBuilder.append("[contains(@");
				xpathBuilder.append(name);
				xpathBuilder.append(",\"");
				xpathBuilder.append(value);
				xpathBuilder.append("\")]");
			}
			else if(cssAttribute.getType()==CssAttributeValueType.TILDA_EQUAL)
			{
				xpathBuilder.append("[contains(concat(\" \",normalize-space(@");
				xpathBuilder.append(name);
				xpathBuilder.append("),\" \"),\" ");
				xpathBuilder.append(value);
				xpathBuilder.append(" \")]");
			}
			else if(cssAttribute.getType()==CssAttributeValueType.PIPE_EQUAL)
			{
				xpathBuilder.append("[starts-with(@");
				xpathBuilder.append(name);
				xpathBuilder.append(",concat(\"");
				xpathBuilder.append(value);
				xpathBuilder.append("\",\"-\")) or ");
				exactMatchXpath(xpathBuilder, name, value);
			}
			else if(cssAttribute.getType()==null)
			{
				xpathBuilder.append("[@");
				xpathBuilder.append(name);
				xpathBuilder.append("]");
			}
		}
	}

	private void exactMatchXpath(StringBuilder xpathBuilder, String name, String value) {
		xpathBuilder.append("@");
		xpathBuilder.append(name);
		xpathBuilder.append("=\"");
		xpathBuilder.append(value);
		xpathBuilder.append("\"]");
	}
	
	public String cssElementCombinatorPairListListConversion(List<List<CssElementCombinatorPair>> cssElementCombinatorPairListList) throws CssSelectorStringSplitterException
	{
		StringBuilder xpathBuilder=new StringBuilder();
		boolean moreThanOne=cssElementCombinatorPairListList.size()>1;
		Iterator<List<CssElementCombinatorPair>> cssElementCombinatorPairIterator=cssElementCombinatorPairListList.iterator();
		while(cssElementCombinatorPairIterator.hasNext())
		{
			List<CssElementCombinatorPair> cssElementCombinatorPairList = cssElementCombinatorPairIterator.next();
			if(moreThanOne) {
				xpathBuilder.append("(");
			}
			xpathBuilder.append(cssElementCombinatorPairListConversion(cssElementCombinatorPairList));
			if(moreThanOne) 
			{
				xpathBuilder.append(")");
			}
			if(cssElementCombinatorPairIterator.hasNext())
			{
				xpathBuilder.append("|");
			}
		}
		return xpathBuilder.toString();
	}
	
	public String convertCssSelectorStringToXpathString(String selectorString) throws CssSelectorStringSplitterException
	{
		List<List<CssElementCombinatorPair>> cssElementCombinatorPairListList=cssSelectorString.listSplitSelectorsIntoElementCombinatorPairs(selectorString);
		String xpath=cssElementCombinatorPairListListConversion(cssElementCombinatorPairListList);
		//System.out.println("CSS Selector="+selectorString+", Xpath string="+xpath);

		return xpath;
	}	
	
	public String mainGo(String[] args)
	{
		
		if(args.length!=1)
		{
			throw new RuntimeException(getUsageString());
		}
		if("-version".equalsIgnoreCase(args[0])||"-v".equalsIgnoreCase(args[0]))
		{
			return "Java-CSS-Selector-To-XPath version:  "+getVersionNumber();
		}
		else if("-help".equalsIgnoreCase(args[0])||"-h".equalsIgnoreCase(args[0]))
		{
			return getUsageString();
		}
		String error;
		try
		{
			return "XPath = "+convertCssSelectorStringToXpathString(args[0]);
		}
		catch (CssSelectorStringSplitterException e)
		{
			if (e.getMessage().trim().length()>0) {
				error="Error: "+e.getMessage();
			} else {
				error="Error:  Invalid CSS Selector";
			}
		}
		catch (RuntimeException e)
		{
			error="Unexpected Error:  "+e;
		}
		throw new RuntimeException(error);
	}

	private String getUsageString() {
		return "java -classpath <Java-CSS-Selector-To-XPath jar file>  org.sam.rosenthal.cssselectortoxpath.utilities.CssElementCombinatorPairsToXpath <CSS Selector string>";
	}
	public String getVersionNumber() {
		return VERSION_PROPERTIES.getProperty("java.cssSelector.to.xpath.version");
	}
	
	public static void main(String[] args)
	{
		try {
		   System.out.println(new CssElementCombinatorPairsToXpath().mainGo(args));
		   System.exit(0);
		} catch (RuntimeException e) {
			System.err.println(e.getMessage());
		}
		System.exit(1);;
	}
}
		
		
	
