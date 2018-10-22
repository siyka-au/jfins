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
import com.siyka.omron.fins.MemoryAreaReadCommand;
import com.siyka.omron.fins.MemoryAreaReadWordsResponse;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.ReferenceCountUtil;

public class FinsFrameUdpCodec extends MessageToMessageCodec<DatagramPacket, FinsFrame<FinsCommand>> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final InetSocketAddress remoteAddress;
	private final InetSocketAddress localAddress;
	
    private final Map<Byte, FinsCommand> outgoingCommands;
    
	public FinsFrameUdpCodec(final InetSocketAddress remoteAddress, final InetSocketAddress localAddress) {
		super();
		this.remoteAddress = remoteAddress;
		this.localAddress = localAddress;
		this.outgoingCommands = new HashMap<>();
	}
	
	@Override
	protected void encode(final ChannelHandlerContext context, final FinsFrame<FinsCommand> frame, final List<Object> out) throws Exception {
		logger.debug("Encoding FINS command frame to DatagramPacket");
		final ByteBuf buffer = Unpooled.buffer();
		Codecs.encodeHeader(buffer, frame.getHeader());

		switch (frame.getHeader().getMessageType()) {
			case COMMAND: {
				final FinsCommand command = (FinsCommand) frame.getPdu();
				this.outgoingCommands.put(frame.getHeader().getServiceAddress(), command);
				
				if (command instanceof MemoryAreaReadCommand) {
					Codecs.encodeMemoryAreaReadCommand(buffer, (MemoryAreaReadCommand) command);
				}
				break;
			}
			
			default:
		}
	
		ReferenceCountUtil.release(frame);
		out.add(new DatagramPacket(buffer, this.remoteAddress, this.localAddress));
	}
	
	@Override
	protected void decode(final ChannelHandlerContext context, final DatagramPacket packet, final List<Object> out) {
		logger.debug("Decoding FINS response frame from DatagramPacket");
		final ByteBuf buffer = packet.content();
		final FinsHeader header = Codecs.decodeHeader(buffer);
		final FinsCommandCode commandCode = Codecs.decodeCommandCode(buffer);
		
		switch(header.getMessageType()) {
			case RESPONSE: {
				final FinsEndCode endCode = FinsEndCode.valueOf(buffer.readShort()).orElse(FinsEndCode.UNKNOWN);
				Optional.ofNullable(this.outgoingCommands.get(header.getServiceAddress())).ifPresent(initiatingCommand -> {
					
					// Verify the command code is the same
					if (commandCode == initiatingCommand.getCommandCode()) {
						
						switch (initiatingCommand.getCommandCode()) {
							case MEMORY_AREA_READ: {
								out.add(new FinsFrame<MemoryAreaReadWordsResponse>(
										header,
										new MemoryAreaReadWordsResponse(
												endCode,
												Codecs.decodeMemoryAreaReadWordsResponse(
														buffer,
														(MemoryAreaReadCommand) initiatingCommand)
												)
										)
								);
								break;
							}
								
							default:				
						}
					}
				});
				break;
			}
			
			default:
		}
	}
	
}
