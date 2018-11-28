package com.siyka.omron.fins;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siyka.omron.fins.FinsHeader.MessageType;
import com.siyka.omron.fins.commands.FinsCommand;
import com.siyka.omron.fins.commands.MemoryAreaReadCommand;
import com.siyka.omron.fins.responses.FinsResponse;
import com.siyka.omron.fins.responses.SimpleResponse;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.ReferenceCountUtil;

import static com.siyka.omron.fins.CommonCodecs.encodeHeader;
import static com.siyka.omron.fins.CommonCodecs.decodeHeader;

public class FinsFrameUdpCodec extends MessageToMessageCodec<DatagramPacket, FinsFrame<?>> {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private InetSocketAddress remoteAddress;
	private InetSocketAddress localAddress;
	
	private Map<Byte, FinsCommand> outgoingCommands;
	private Map<Byte, FinsCommand> incomingCommands;
	
	private final FinsCommandEncoder commandEncoder;
	private final FinsResponseDecoder responseDecoder;

	private final FinsCommandDecoder commandDecoder;
	private final FinsResponseEncoder responseEncoder;
    
	public FinsFrameUdpCodec(InetSocketAddress remoteAddress, InetSocketAddress localAddress) {
		super();
		this.remoteAddress = remoteAddress;
		this.localAddress = localAddress;
		
		this.commandEncoder = new FinsCommandEncoder();
		this.responseDecoder = new FinsResponseDecoder();
		
		this.commandDecoder = new FinsCommandDecoder();
		this.responseEncoder = new FinsResponseEncoder();
		
		this.outgoingCommands = new HashMap<>();
		this.incomingCommands = new HashMap<>();
	}
	
	@Override
	protected void encode(ChannelHandlerContext context, FinsFrame<?> frame, List<Object> out) {
		logger.debug("Encoding FINS frame to DatagramPacket");
		ByteBuf buffer = Unpooled.buffer();
		FinsHeader header = frame.getHeader();
		encodeHeader(header, buffer);

		logger.debug("PDU: {}", frame.getPdu());
		
		FinsPdu pdu = frame.getPdu();
		if (pdu instanceof FinsCommand && header.getMessageType() == MessageType.COMMAND) {
			logger.debug("Encoding FINS command");
			this.commandEncoder.encode((FinsCommand) frame.getPdu(), buffer);
		} else if (pdu instanceof FinsResponse && header.getMessageType() == MessageType.RESPONSE) {
			logger.debug("Encoding FINS response");
			this.responseEncoder.encode((FinsResponse) frame.getPdu(), buffer);
		} else {
			throw new EncoderException("Header and PDU type didn't match");
		}
	
		ReferenceCountUtil.release(frame);
		out.add(new DatagramPacket(buffer, this.remoteAddress, this.localAddress));
	}
	
	@Override
	protected void decode(ChannelHandlerContext context, DatagramPacket packet, List<Object> out) {
		logger.debug("Decoding FINS frame from DatagramPacket");
		ByteBuf buffer = packet.content();
		FinsHeader header = decodeHeader(buffer);
		
		context.channel().attr(ServiceAddressCorrelation.SERVICE_ADDRESS).set(header.getServiceAddress());
		
		switch(header.getMessageType()) {
			case COMMAND: {
				out.add(this.commandDecoder.decode(buffer));
				break;
			}
			
			case RESPONSE: {
				out.add(this.responseDecoder.decode(buffer));
				break;
			}
		}
		
	}	
	
}
