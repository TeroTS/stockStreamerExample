package at.atserverapiexample;

import java.util.Iterator;
import java.util.Vector;
import at.shared.ATServerAPIDefines;
import at.shared.ATServerAPIDefines.ATStreamResponseType;
import at.shared.ATServerAPIDefines.ATSymbolStatus;

public class Requestor extends at.feedapi.ActiveTickServerRequester
{
	public Requestor(APISession apiSession, Streamer streamer) 
	{
		super(apiSession.GetServerAPI(), apiSession.GetSession(), streamer);
	}
	
	public void OnRequestTimeoutCallback(long origRequest)
	{
		System.out.println("(" + origRequest + "): Request timed-out");
	}

	public void OnQuoteStreamResponse(long origRequest, ATServerAPIDefines.ATStreamResponseType responseType, Vector<ATServerAPIDefines.ATQUOTESTREAM_DATA_ITEM> vecData)
	{
		String strResponseType = "";
		switch(responseType.m_responseType)
		{
			case ATStreamResponseType.StreamResponseSuccess: strResponseType = "StreamResponseSuccess"; break;
			case ATStreamResponseType.StreamResponseInvalidRequest: strResponseType = "StreamResponseInvalidRequest"; break;
			case ATStreamResponseType.StreamResponseDenied: strResponseType = "StreamResponseDenied"; break;
			default: break;
		}
		
		System.out.println("RECV (" + origRequest +"): Quote stream response [" + strResponseType + "]\n--------------------------------------------------------------");
		
		if(responseType.m_responseType == ATStreamResponseType.StreamResponseSuccess)
		{
			String strSymbolStatus = "";
			Iterator<ATServerAPIDefines.ATQUOTESTREAM_DATA_ITEM> itrDataItems = vecData.iterator();
			while(itrDataItems.hasNext())
			{
				ATServerAPIDefines.ATQUOTESTREAM_DATA_ITEM atDataItem = (ATServerAPIDefines.ATQUOTESTREAM_DATA_ITEM)itrDataItems.next();
				switch(atDataItem.symbolStatus.m_atSymbolStatus)
				{
					case ATSymbolStatus.SymbolStatusSuccess: strSymbolStatus = "SymbolStatusSuccess"; break;
					case ATSymbolStatus.SymbolStatusInvalid: strSymbolStatus = "SymbolStatusInvalid"; break;
					case ATSymbolStatus.SymbolStatusUnavailable: strSymbolStatus = "SymbolStatusUnavailable"; break;
					case ATSymbolStatus.SymbolStatusNoPermission: strSymbolStatus = "SymbolStatusNoPermission"; break;
					default: break;
				}
				
				System.out.println("\tsymbol:" + strSymbolStatus + " symbolStatus: " + strSymbolStatus);
			}
		}		
	}
}