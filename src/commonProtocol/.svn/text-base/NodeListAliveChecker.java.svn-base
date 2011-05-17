package commonProtocol;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import internals.Database;
import internals.LocalRegionalServer;
import internals.Log;
import internals.Wait;

public class NodeListAliveChecker extends Thread {
	
	final int AliveTimeOut = 30000;

	List<Node> nodeLinkList = NodeList.getInstance().nodeLinkList;
	ArrayList<Integer> removeList = new ArrayList<Integer>();
	Log log = Log.getInstance();

	public NodeListAliveChecker() {
		Thread thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * Iterates the nodelist and looks for old nodes, removes them and updates the database
	 */
	public void run() {
		// initial delay
		Wait.milliSec(2000);
		while (true) {
			nodeLinkList = NodeList.getInstance().nodeLinkList;
			boolean listUpdated = false;
			ListIterator<Node> itr = nodeLinkList.listIterator();
			while (itr.hasNext()) {
				Node node = itr.next();
				long result = System.currentTimeMillis() - node.aliveTimeStamp;
				if (result > AliveTimeOut && !(node.ip.equals(LocalRegionalServer.serverIPAddress) && node.udpPort == LocalRegionalServer.commonProtoUDPPort)) {
					log.printAndLog("Removing node because of no update received: " + node);
					node = null;
					itr.remove();
					listUpdated = true;
				}
			}
			if (listUpdated)
				Database.getInstance().saveNodeList(NodeList.getInstance().getNodes());
			Wait.milliSec(5000);
		}
	}
}
