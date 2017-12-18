package at.atserverapiexample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import at.feedapi.ActiveTickServerAPI;
import at.feedapi.Helpers;
import at.shared.ATServerAPIDefines;
import at.shared.ATServerAPIDefines.ATGUID;
import at.shared.ATServerAPIDefines.ATSYMBOL;
import at.shared.ATServerAPIDefines.ATStreamRequestType;
import at.utils.jlib.Errors;

public class ATServerAPIExample extends Thread
{
	public static ActiveTickServerAPI serverapi;
	public static APISession apiSession;
	
	public void PrintUsage()
	{
		System.out.println("ActiveTick Feed Java API");
		System.out.println("Available commands:");
		System.out.println("-------------------------------------------------------------");
		System.out.println("?");
		System.out.println("quit");

		System.out.println("init [serverHostname] [serverPort] [apiKey] [userid] [password]");
		System.out.println("\tserverHostname: activetick1.activetick.com serverPort: 443");
		System.out.println("\tapiUserId: valid alphanumeric apiKey, for example EF1C0A768BBB11DFBCB3F923E0D72085");
		System.out.println("\tuserid and password: valid account login credentials");
		System.out.println("\texample: init activetick1.activetick.com 443 EF1C0A768BBB11DFBCB3F923E0D72085 myuser mypass");
		System.out.println("");
		
		System.out.println("getIntradayHistoryBars [symbol] [minutes] [beginTime] [endTime]");
		System.out.println("\tminutes: intraday bar minute interval, for example 1-minute, 5-minute bars");
		System.out.println("");
		
		System.out.println("getDailyHistoryBars [symbol] [beginTime] [endTime]");
		System.out.println("getWeeklyHistoryBars [symbol] [beginTime] [endTime]");
		System.out.println("");
		
		System.out.println("getTicks [symbol] [beginTime] [endTime]");
		System.out.println("getTicks [symbol] [number of records]");
		System.out.println("");
		
		System.out.println("getMarketMovers [symbol] [exchange]");
		System.out.println("\tsymbol: \"VL\"=volume, \"NG\"/\"NL\"=net gain/loser, \"PG\"/\"PL\"=percent gain/loser, ");
		System.out.println("\texchange: A=Amex, N=NYSE, Q=NASDAQ, U=OTCBB");
		System.out.println("\texample: getMarketMovers VL Q");
		System.out.println("");
		
		System.out.println("getQuoteDb [symbol]");
		System.out.println("getOptionChain [symbol]");
		System.out.println("");
		
		System.out.println("subscribeMarketMovers [symbol] [exchange]");
		System.out.println("unsubscribeMarketMovers [symbol] [exchange]");
		
		System.out.println("subscribeQuoteStream [symbol]");
		System.out.println("unsubscribeQuoteStream [symbol]");

		System.out.println("unsubscribeQuotesOnlyQuoteStream [symbol]");
		System.out.println("unsubscribeQuotesOnlyQuoteStream [symbol]");

		System.out.println("subscribeTradesOnlyQuoteStream [symbol]");
		System.out.println("unsubscribeTradesOnlyQuoteStream [symbol]");
		System.out.println("");
		
		System.out.println("-------------------------------------------------------------");
		System.out.println("Date/time format: YYYYMMDDHHMMSS");		
		System.out.println("Symbol format: stocks=GOOG, indeces=$DJI, currencies=#EUR/USD, options=.AAPL--131004C00380000");
		System.out.println("-------------------------------------------------------------");
	}
	
	public void InvalidGuidMessage()
	{
		System.out.println("Warning! \n\tApiUserIdGuid should be 32 characters long and alphanumeric only.");
	}
	
	public ATServerAPIExample()
	{
		PrintUsage();			
		start();	//get into the run method		
	}
	
	/**********************************************************************
	 * //processInput
	 * Notes:
	 * -Process command line input
	 **********************************************************************/

	public void processInput(String userInput)
	{
		StringTokenizer st = new StringTokenizer(userInput);
		List ls = new ArrayList<String>();
		while(st.hasMoreTokens())
			ls.add(st.nextToken());
		int count = ls.size();
		
		if(count > 0 && ((String)ls.get(0)).equalsIgnoreCase("?"))
		{
			PrintUsage();
		}

		//init
		else if(count >= 5 && ((String)ls.get(0)).equalsIgnoreCase("init"))
		{
			String serverHostname = ls.get(1).toString();
			int serverPort = new Integer(ls.get(2).toString());
			String apiKey = ls.get(3).toString();
			String userId = ls.get(4).toString();
			String password = ls.get(5).toString();
			
			if(apiKey.length() != 32)
			{
				InvalidGuidMessage();
				return;
			}
			
			ATGUID atguid = (new ATServerAPIDefines()).new ATGUID();
			atguid.SetGuid(apiKey);
			
			boolean rc = apiSession.Init(atguid, serverHostname, serverPort, userId, password);
			System.out.println("\ninit status: " + (rc == true ? "ok" : "failed"));
		}
		
		/**********************************************************************
		 * //subscribeTradesOnlyQuoteStream | unsubscribeTradesOnlyQuoteStream
		 * Example:
		 * Single symbol request:
		 * 		subscribeTradesOnlyQuoteStream  AAPL
		 * 		unsubscribeTradesOnlyQuoteStream  AAPL
		 * Multiple symbol request:
		 * 		subscribeTradesOnlyQuoteStream  AAPL,AMZN
		 * 		unsubscribeTradesOnlyQuoteStream  AAPL,AMZN
		 **********************************************************************/
		else if(count >= 2 && ( ((String)ls.get(0)).equalsIgnoreCase("subscribeTradesOnlyQuoteStream") ||
				((String)ls.get(0)).equalsIgnoreCase("unsubscribeTradesOnlyQuoteStream")))
		{
			String strSymbols = ls.get(1).toString();
			List<ATSYMBOL> lstSymbols = new ArrayList<ATSYMBOL>();

			if(!strSymbols.isEmpty() && !strSymbols.contains(","))
			{
				ATSYMBOL atSymbol = Helpers.StringToSymbol(strSymbols);
				lstSymbols.add(atSymbol);
			}
			else
			{
				StringTokenizer symbolTokenizer = new StringTokenizer(strSymbols, ",");
				while(symbolTokenizer.hasMoreTokens())
				{
					ATSYMBOL atSymbol = Helpers.StringToSymbol(symbolTokenizer.nextToken());
					lstSymbols.add(atSymbol);
				}
			}			
			
			ATStreamRequestType requestType = (new ATServerAPIDefines()).new ATStreamRequestType();
			requestType.m_streamRequestType = ((String)ls.get(0)).equalsIgnoreCase("subscribeTradesOnlyQuoteStream") ? 
											ATStreamRequestType.StreamRequestSubscribeTradesOnly : ATStreamRequestType.StreamRequestUnsubscribeTradesOnly;
			
			long request = apiSession.GetRequestor().SendATQuoteStreamRequest(lstSymbols, requestType, ActiveTickServerAPI.DEFAULT_REQUEST_TIMEOUT);
			
			System.out.println("SEND " + request + ": " + ls.get(0).toString() + " request [" + strSymbols + "]");
			if(request < 0)
			{
				System.out.println("Error = " + Errors.GetStringFromError((int)request));
			}
		}			
	}

	public void run()
	{		
		  serverapi = new ActiveTickServerAPI();	      
	      apiSession = new APISession(serverapi);
	      serverapi.ATInitAPI();
	      
	      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	      
	      while(true)
	      {
		      try 
		      {
			         String line = br.readLine();
			         if(line.length() > 0)
			         {
			        	 if(line.startsWith("quit"))
			        		 break;
			        	 
			        	 processInput(line);
			         }
			  } 
		      catch (IOException e) 
			  {
		    	  System.out.println("IO error trying to read your input!");
			  }
	      }
	      
	      apiSession.UnInit();
	      serverapi.ATShutdownAPI();
	}
	
	public static void main(String args[])
	{
		ATServerAPIExample apiExample = new ATServerAPIExample();
	}
}
