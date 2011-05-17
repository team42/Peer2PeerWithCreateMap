package commonProtocol.packet;

public class BSRequestPacket extends CPPacket {

	public BSRequestPacket(){
		this.command = "bsrequest";
		this.version = 1;
	}
	
	@Override
	public String generateCPPacket() {
		return this.command+"|"+this.version;
	}


}
