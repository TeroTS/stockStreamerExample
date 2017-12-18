package at.atserverapiexample;

import at.feedapi.ActiveTickStreamListener;
import at.shared.ATServerAPIDefines;
import at.utils.jlib.PrintfFormat;

public class Streamer extends ActiveTickStreamListener
{
	APISession m_session;
	
	public Streamer(APISession session)
	{
		super(session.GetSession(), false);
		m_session = session;
	}
	
	public void OnATStreamTradeUpdate(ATServerAPIDefines.ATQUOTESTREAM_TRADE_UPDATE update)
	{		
		String strSymbol = new String(update.symbol.symbol);
		int plainSymbolIndex = strSymbol.indexOf((byte)0);
		strSymbol = strSymbol.substring(0, plainSymbolIndex);
		
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		sb.append(update.lastDateTime.hour);
		sb.append(":");
		sb.append(update.lastDateTime.minute);
		sb.append(":");
		sb.append(update.lastDateTime.second);
		sb.append(":");
		sb.append(update.lastDateTime.milliseconds);
		sb.append("] STREAMTRADE [symbol:");
		sb.append(strSymbol);
		sb.append(" last:");
		
		String strFormat = "%0." + update.lastPrice.precision + "f";
		sb.append(new PrintfFormat(strFormat).sprintf(update.lastPrice.price));
		sb.append(" lastSize:");
		sb.append(update.lastSize);		
		sb.append("]");
		System.out.println(sb.toString());
	}
}
