package net.netdatacorp.efile.handler;

import java.io.PrintStream;
import java.util.Map;

import javax.xml.ws.handler.LogicalHandler;
import javax.xml.ws.handler.LogicalMessageContext;
import javax.xml.ws.handler.MessageContext;

import net.netdatacorp.util.HandlerUtil;

public class HTTPHeaderLoggingLogicalHandler extends
		BaseHandler<LogicalMessageContext> implements
		LogicalHandler<LogicalMessageContext> {
	private static final String HANDLER_NAME = "HTTPHeaderLoggingLogicalHandler";

	private static PrintStream out = System.out;

	public HTTPHeaderLoggingLogicalHandler() {
		super();
		super.setHandlerName(HANDLER_NAME);
	}

	public boolean handleMessage(LogicalMessageContext smc) {
		out.println("------------------------------------");
		out.println("In LogicalHandler " + HandlerName + ":handleMessage()");

		Boolean outboundProperty = (Boolean) smc
				.get(LogicalMessageContext.MESSAGE_OUTBOUND_PROPERTY);
		Map<?, ?> http_req_headers = (Map<?, ?>) smc
				.get(MessageContext.HTTP_REQUEST_HEADERS);
		String http_req_method = (String) smc
				.get(MessageContext.HTTP_REQUEST_METHOD);

		if (outboundProperty.booleanValue()) {
			out.println("\ndirection = outbound ");
		} else {
			out.println("\ndirection = inbound ");
			out.println("\n***HTTP Request Headers***");

			if (http_req_headers != null) {
				out.println(HandlerUtil.outputMap(http_req_headers));
			} else {
				out.println("HTTP Request Headers is not available!");
			}
			
			out.println("\n***HTTP Request Method***");
			if (http_req_method != null) {
				out.println(http_req_method);
			}else{
				out.println("HTTP Request Method is not available!");
			}

			out.println("Exiting LogicalHandler " + HandlerName
					+ ":handleMessage()");
			out.println("------------------------------------");
		}
		return true;
	}
}
