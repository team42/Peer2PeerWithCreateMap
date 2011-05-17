package commonProtocol.packet;

public class LoginPacket extends CPPacket {

	public String ip;
	public int port;
	
	public LoginPacket(int version, int port, String ip) {
		this.version = version;
		this.port = port;
		this.ip = ip;
	}
	
	public LoginPacket(String packet, String ip) throws Exception {
		String[] regEx = new String[3]; 
		regEx = packet.split("\\|");
		this.command = regEx[0];
		this.version = Integer.parseInt(regEx[1]);
		this.port = Integer.parseInt(regEx[2]);
		this.ip = ip;
	}
	
	@Override
	public String generateCPPacket() {
			String command = "login";
			String message = command + "|" + Math.abs(version) + "|" + Math.abs(port);
			return message;
	}
}
