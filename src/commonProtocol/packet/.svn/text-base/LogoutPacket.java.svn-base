package commonProtocol.packet;

public class LogoutPacket extends CPPacket {

	public int port;
	
	public LogoutPacket(int version, int port) {
		this.version = version;
		this.port = port;
	}
	
	public LogoutPacket(String packet) throws Exception{
		String[] regEx = new String[3]; 
		regEx = packet.split("\\|");
		this.command = regEx[0];
		this.version = Integer.parseInt(regEx[1]);
		this.port = Integer.parseInt(regEx[2]);
	}
	
	@Override
	public String generateCPPacket() {
			String command = "logout";
			String message = command + "|" + Math.abs(version) + "|" + Math.abs(port);
			return message;
	}
}
