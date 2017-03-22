package com.zy.zmail.server.north.zytcp;//package com.zy.zmail.server.north.zytcp;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.apache.mina.core.buffer.IoBuffer;
//import org.apache.mina.core.service.IoHandlerAdapter;
//import org.apache.mina.core.session.IoSession;
//
//import java.net.InetSocketAddress;
//
///**
// * Created by wenliz */
//public class InboundHandler extends IoHandlerAdapter {
//    private static Log log = LogFactory.getLog(IoHandlerAdapter.class);
//    private ZytcpGateway tcpGateway;
//
//    public InboundHandler(ZytcpGateway gateway){
//        this.tcpGateway = gateway;
//    }
//
//    public void messageReceived(IoSession session, Object message){
//        if(message instanceof IoBuffer){
//            String ipAddress = ((InetSocketAddress)session.getRemoteAddress()).getAddress().getHostAddress();
//            Integer portNo = ((InetSocketAddress) session.getRemoteAddress()).getPort();
//        }
//    }
//}
