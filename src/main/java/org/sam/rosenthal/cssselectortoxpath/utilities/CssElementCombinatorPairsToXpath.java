package org.sam.rosenthal.cssselectortoxpath.utilities;

import java.util.Iterator;
import java.util.List;

import org.sam.rosenthal.cssselectortoxpath.model.CssAttribute;
import org.sam.rosenthal.cssselectortoxpath.model.CssAttributeValueType;
import org.sam.rosenthal.cssselectortoxpath.model.CssElementCombinatorPair;

public class CssElementCombinatorPairsToXpath 
{
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

		for(CssAttribute cssAttribute: cssAttributeList)
		{
			if(cssAttribute.getType()==CssAttributeValueType.EQUAL)
			{
				xpathBuilder.append("[@");
				xpathBuilder.append(cssAttribute.getName());
				xpathBuilder.append("=\"");
				xpathBuilder.append(cssAttribute.getValue());
				xpathBuilder.append("\"]");
			}
		}
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
	
	public String convertCssStringToXpathString(String selectorString) throws CssSelectorStringSplitterException
	{
		List<List<CssElementCombinatorPair>> cssElementCombinatorPairListList=cssSelectorString.listSplitSelectorsIntoElementCombinatorPairs(selectorString);
		String xpath=cssElementCombinatorPairListListConversion(cssElementCombinatorPairListList);
		return xpath;
	}
	
	

//		List<CssAttribute> cssAttributeList=cssElementAttribute.getCssAttributeList();
//		if (!cssAttributeList.isEmpty()) 
//		{
//			convertCssAttributeListToXpath(xPathBuilder, cssAttributeList);
//		}
//		return xPathBuilder.toString();
//	}
//	

//	
//	public String convertCssElementAttributeToXpathString(String elementWithAttributesString) throws CssSelectorStringSplitterException
//	{
//		CssElementAttributes cssElementAttribute=cssElementAttributeParser.createElementAttribute(elementWithAttributesString);
//		String xpath= cssElementAttributeToXpathStringConversion(cssElementAttribute);
//		return xpath;
//	
//	}
}
		
		
	
