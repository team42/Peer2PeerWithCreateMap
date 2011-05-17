package commonProtocol;

import internals.LocalRegionalServer;
import internals.Wait;

public class AliveSender extends Thread{
	
	public AliveSender(){
		Thread thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();
	}
	
	@Override
	public void run(){
		Wait.milliSec(3000);
		while(true){
			LocalRegionalServer.commonProtoUDP.sendAliveToAllNodes();
			//Should be 60.000
			Wait.milliSec(10000);			
		}	
	}
}
