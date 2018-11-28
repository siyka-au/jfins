<<<<<<< HEAD
package com.siyka.omron.fins.master;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siyka.omron.fins.CommandCode;
import com.siyka.omron.fins.EndCode;
import com.siyka.omron.fins.FinsFrame;
import com.siyka.omron.fins.FinsHeader;
import com.siyka.omron.fins.commands.FinsCommand;
import com.siyka.omron.fins.commands.MemoryAreaReadCommand;
import com.siyka.omron.fins.commands.MemoryAreaWriteBitsCommand;
import com.siyka.omron.fins.commands.MemoryAreaWriteWordsCommand;
import com.siyka.omron.fins.responses.FinsResponse;
import com.siyka.omron.fins.responses.MemoryAreaReadWordsResponse;
import com.siyka.omron.fins.responses.SimpleResponse;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.ReferenceCountUtil;

public class FinsFrameUdpCodec extends MessageToMessageCodec<DatagramPacket, FinsFrame<?>> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final InetSocketAddress remoteAddress;
	private final InetSocketAddress localAddress;
	
    private final Map<Byte, FinsCommand> outgoingCommands;
    
	public FinsFrameUdpCodec(InetSocketAddress remoteAddress, InetSocketAddress localAddress) {
		super();
		this.remoteAddress = remoteAddress;
		this.localAddress = localAddress;
		this.outgoingCommands = new HashMap<>();
	}
	
	@Override
	protected void encode(ChannelHandlerContext context, FinsFrame<?> frame, List<Object> out) throws Exception {
		logger.debug("Encoding FINS command frame to DatagramPacket");
		final ByteBuf buffer = Unpooled.buffer();
		Codecs.encodeHeader(buffer, frame.getHeader());

		switch (frame.getHeader().getMessageType()) {
			case COMMAND: {
				encodeCommand(buffer, context, frame);
				break;
			}
			
			case RESPONSE: {
				encodeResponse(buffer, context, frame);
				break;
			}
		}
	
		ReferenceCountUtil.release(frame);
		out.add(new DatagramPacket(buffer, this.remoteAddress, this.localAddress));
	}
	
	@Override
	protected void decode(ChannelHandlerContext context, DatagramPacket packet, List<Object> out) {
		logger.debug("Decoding FINS response frame from DatagramPacket");
		final ByteBuf buffer = packet.content();
		final FinsHeader header = Codecs.decodeHeader(buffer);
		final CommandCode commandCode = Codecs.decodeCommandCode(buffer);
		
		switch(header.getMessageType()) {
			case RESPONSE: {
				out.add(decodeResponse(context, header, commandCode, buffer));
				break;
			}
			
			case COMMAND: {
				out.add(decodeCommand(context, header, commandCode, buffer));
				break;
			}
		}
	}	
	
	// Worker methods

	
	private void encodeResponse(ByteBuf buffer, ChannelHandlerContext context, FinsFrame<?> frame) {
		final FinsResponse response = (FinsResponse) frame.getPdu();
		
		if (response instanceof SimpleResponse) {
			Codecs.encodeSimpleResponse(buffer, (SimpleResponse) response);
		}
	}

	private FinsFrame<?> decodeCommand(ChannelHandlerContext context, FinsHeader header, CommandCode commandCode, ByteBuf buffer) {
		switch (commandCode) {
			default:
				return null;
		}
	}
	
}
=======
package com.siyka.omron.fins.master;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siyka.omron.fins.FinsCommand;
import com.siyka.omron.fins.FinsCommandCode;
import com.siyka.omron.fins.FinsEndCode;
import com.siyka.omron.fins.FinsFrame;
import com.siyka.omron.fins.FinsHeader;
import com.siyka.omron.fins.FinsResponse;
import com.siyka.omron.fins.MemoryAreaReadCommand;
import com.siyka.omron.fins.MemoryAreaReadWordsResponse;
import com.siyka.omron.fins.MemoryAreaWriteBitsCommand;
import com.siyka.omron.fins.MemoryAreaWriteWordsCommand;
import com.siyka.omron.fins.SimpleResponse;
import com.siyka.omron.fins.codec.Codecs;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.ReferenceCountUtil;

public class FinsFrameUdpCodec extends MessageToMessageCodec<DatagramPacket, FinsFrame<?>> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final InetSocketAddress remoteAddress;
	private final InetSocketAddress localAddress;
	
    private final Map<Byte, FinsCommand> outgoingCommands;
    
	public FinsFrameUdpCodec(InetSocketAddress remoteAddress, InetSocketAddress localAddress) {
		super();
		this.remoteAddress = remoteAddress;
		this.localAddress = localAddress;
		this.outgoingCommands = new HashMap<>();
	}
	
	@Override
	protected void encode(ChannelHandlerContext context, FinsFrame<?> frame, List<Object> out) throws Exception {
		logger.debug("Encoding FINS command frame to DatagramPacket");
		final ByteBuf buffer = Unpooled.buffer();
		Codecs.encodeHeader(buffer, frame.getHeader());

		switch (frame.getHeader().getMessageType()) {
			case COMMAND: {
				encodeCommand(buffer, context, frame);
				break;
			}
			
			case RESPONSE: {
				encodeResponse(buffer, context, frame);
				break;
			}
		}
	
		ReferenceCountUtil.release(frame);
		out.add(new DatagramPacket(buffer, this.remoteAddress, this.localAddress));
	}
	
	@Override
	protected void decode(ChannelHandlerContext context, DatagramPacket packet, List<Object> out) {
		logger.debug("Decoding FINS response frame from DatagramPacket");
		final ByteBuf buffer = packet.content();
		final FinsHeader header = Codecs.decodeHeader(buffer);
		final FinsCommandCode commandCode = Codecs.decodeCommandCode(buffer);
		
		switch(header.getMessageType()) {
			case RESPONSE: {
				out.add(decodeResponse(context, header, commandCode, buffer));
				break;
			}
			
			case COMMAND: {
				out.add(decodeCommand(context, header, commandCode, buffer));
				break;
			}
		}
	}	
	
	// Worker methods
	private void encodeCommand(ByteBuf buffer, ChannelHandlerContext context, FinsFrame<?> frame) {
		final FinsCommand command = (FinsCommand) frame.getPdu();
		this.outgoingCommands.put(frame.getHeader().getServiceAddress(), command);
		
		if (command instanceof MemoryAreaReadCommand) {
			Codecs.encodeMemoryAreaReadCommand(buffer, (MemoryAreaReadCommand) command);
		} else if (command instanceof MemoryAreaWriteWordsCommand) {
			Codecs.encodeMemoryAreaWriteWordsCommand(buffer, (MemoryAreaWriteWordsCommand) command);
		} else if (command instanceof MemoryAreaWriteBitsCommand) {
			Codecs.encodeMemoryAreaWriteBitsCommand(buffer, (MemoryAreaWriteBitsCommand) command);
		}
	}
	
	private void encodeResponse(ByteBuf buffer, ChannelHandlerContext context, FinsFrame<?> frame) {
		final FinsResponse response = (FinsResponse) frame.getPdu();
		
		if (response instanceof SimpleResponse) {
			Codecs.encodeSimpleResponse(buffer, (SimpleResponse) response);
		}
	}

	private FinsFrame<?> decodeCommand(ChannelHandlerContext context, FinsHeader header, FinsCommandCode commandCode, ByteBuf buffer) {

	}
	
	private FinsFrame<?> decodeResponse(ChannelHandlerContext context, FinsHeader header, FinsCommandCode commandCode, ByteBuf buffer) {
		final FinsEndCode endCode = FinsEndCode.valueOf(buffer.readShort()).orElse(FinsEndCode.UNKNOWN);
		return Optional.ofNullable(this.outgoingCommands.get(header.getServiceAddress())).map(initiatingCommand -> {
			
			// Verify the command code is the same
			if (commandCode == initiatingCommand.getCommandCode()) {
				
				switch (initiatingCommand.getCommandCode()) {
					case MEMORY_AREA_READ: {
						return new FinsFrame<MemoryAreaReadWordsResponse>(
								header,
								new MemoryAreaReadWordsResponse(
										endCode,
										Codecs.decodeMemoryAreaReadWordsResponse(
												buffer,
												(MemoryAreaReadCommand) initiatingCommand)
										)
								);
					}
						
					default:
				}
			}
		});
		
		
	}
	
}
>>>>>>> d4238ca4922744b473d7e4f1526c8dce06f37b00
