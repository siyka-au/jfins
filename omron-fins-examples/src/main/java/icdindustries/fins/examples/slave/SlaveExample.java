package icdindustries.fins.examples.slave;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.bessel.fins.FinsEndCode;
import io.bessel.fins.MemoryAreaWriteCommandHandler;
import io.bessel.fins.commands.FinsMemoryAreaWriteCommand;
import io.bessel.fins.commands.FinsMemoryAreaWriteResponse;
import io.bessel.fins.commands.FinsMemoryAreaWriteWordCommand;
import io.bessel.fins.slave.FinsNettyTcpSlave;

public class SlaveExample {

	final static Logger logger = LoggerFactory.getLogger(SlaveExample.class);
	
	public static void main(String... args) throws Exception {
		FinsNettyTcpSlave server = new FinsNettyTcpSlave("ics-triton", 9600);
		
		server.setMemoryAreaWriteHandler(new MemoryAreaWriteCommandHandler() {
			
			@Override
			public FinsMemoryAreaWriteResponse handle(FinsMemoryAreaWriteCommand command) {
				Optional<FinsEndCode> endCode = Optional.empty();
				
				final String formatText = "%s(%s)";
				
				if (command instanceof FinsMemoryAreaWriteWordCommand) {
					FinsMemoryAreaWriteWordCommand wordCommand = (FinsMemoryAreaWriteWordCommand) command;
					Short value = wordCommand.getItems().get(0);
					
					Boolean alarmState = (value & 1) == 1;
					
					String eventText = String.format(formatText + " => 0x%x", "Unknown_Event", alarmState,
							command.getIoAddress().getAddress());
					
					String siteId = "1000";
					String alarmName = "Unknown Alarm";
					
					switch (command.getIoAddress().getMemoryArea()) {
					default:
						break;
						
					case DM_WORD:
						switch (command.getIoAddress().getAddress()) {
						default:
							break;
							
						case 20000:
							endCode = Optional.of(FinsEndCode.NORMAL_COMPLETION);
							alarmName = "Mains Fail";
							eventText = String.format(formatText, "Mains_Fail", alarmState);
							break;

						case 20001:
							endCode = Optional.of(FinsEndCode.NORMAL_COMPLETION);
							alarmName = "Smoke Detector Alarm";
							eventText = String.format(formatText, "Smoke_Detector", alarmState);
							break;

						case 20002:
							endCode = Optional.of(FinsEndCode.NORMAL_COMPLETION);
							alarmName = "External Sensor Fault";
							eventText = String.format(formatText, "External_Sensor_Fault", alarmState);
							break;

						case 20003:
							endCode = Optional.of(FinsEndCode.NORMAL_COMPLETION);
							alarmName = "Internal Sensor Fault";
							eventText = String.format(formatText, "Internal_Sensor_Fault", alarmState);
							break;

						case 20004:
							endCode = Optional.of(FinsEndCode.NORMAL_COMPLETION);
							alarmName = "Battery Rack Temperature Sensor Fault";
							eventText = String.format(formatText, "Battery_Rack_Temperature_Sensor_Fault", alarmState);
							break;

						case 20005:
							endCode = Optional.of(FinsEndCode.NORMAL_COMPLETION);
							alarmName = "Floor Temperature Sensor Fault";
							eventText = String.format(formatText, "Floor_Temperature_Sensor_Fault", alarmState);
							break;

						case 20006:
							endCode = Optional.of(FinsEndCode.NORMAL_COMPLETION);
							alarmName = "Entry Door Open";
							eventText = String.format(formatText, "Entry_Door_Open", alarmState);
							break;

						case 20007:
							endCode = Optional.of(FinsEndCode.NORMAL_COMPLETION);
							alarmName = "Battery Rack Temperature Alarm";
							eventText = String.format(formatText, "Battery_Rack_Temperature_Alarm", alarmState);
							break;

						case 20008:
							endCode = Optional.of(FinsEndCode.NORMAL_COMPLETION);
							alarmName = "PLC Low Battery Alarm";
							eventText = String.format(formatText, "PLC_Low_Battery", alarmState);
							break;

						case 20009:
							endCode = Optional.of(FinsEndCode.NORMAL_COMPLETION);
							alarmName = "AC1 Fault";
							eventText = String.format(formatText, "AC1_Fault", alarmState);
							break;

						case 20010:
							endCode = Optional.of(FinsEndCode.NORMAL_COMPLETION);
							alarmName = "AC2 Fault";
							eventText = String.format(formatText, "AC2_Fault", alarmState);
							break;

						case 20011:
							endCode = Optional.of(FinsEndCode.NORMAL_COMPLETION);
							alarmName = "AC3 Fault";
							eventText = String.format(formatText, "AC3_Fault", alarmState);
							break;

						case 20012:
							endCode = Optional.of(FinsEndCode.NORMAL_COMPLETION);
							alarmName = "Shelter High Temperature Alarm";
							eventText = String.format(formatText, "Shelter_High_Temperature_Alarm", alarmState);
							break;

						case 20013:
							endCode = Optional.of(FinsEndCode.NORMAL_COMPLETION);
							alarmName = "AC1 Run";
							eventText = String.format(formatText, "AC1_Run", alarmState);
							break;

						case 20014:
							endCode = Optional.of(FinsEndCode.NORMAL_COMPLETION);
							alarmName = "AC1 Run";
							eventText = String.format(formatText, "AC2_Run", alarmState);
							break;

						case 20015:
							endCode = Optional.of(FinsEndCode.NORMAL_COMPLETION);
							alarmName = "AC1 Run";
							eventText = String.format(formatText, "AC3_Run", alarmState);
							break;

						case 20017:
							endCode = Optional.of(FinsEndCode.NORMAL_COMPLETION);
							alarmName = "Battery Rack Temperature Alarm";
							eventText = String.format(formatText, "Battery_Rack_Temperature_Alarm", alarmState);
							break;
						}
						break;
					}

					logger.info(eventText);
				}
				
				return new FinsMemoryAreaWriteResponse(endCode.orElse(FinsEndCode.WRITE_NOT_POSSIBLE__PROTECTED));
			}
		});
		
		server.start();
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				server.shutdown();
			}
		});
	}

}

