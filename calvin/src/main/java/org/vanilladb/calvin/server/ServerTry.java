package org.vanilladb.calvin.server;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vanilladb.comm.server.VanillaCommServer;
import org.vanilladb.comm.server.VanillaCommServerListener;
import org.vanilladb.comm.view.ProcessType;

public class ServerTry implements VanillaCommServerListener {
	private static Logger logger = Logger.getLogger(ServerTry.class.getName());
	
	private static final BlockingQueue<Serializable> msgQueue =
			new LinkedBlockingDeque<Serializable>();
	
	public static void main(String[] args) {
		if (logger.isLoggable(Level.INFO))
			logger.info("Initializing the server...");
		
		int selfId = Integer.parseInt(args[0]);
		VanillaCommServer server = new VanillaCommServer(selfId, new ServerTry());
		new Thread(server).start();
		createClientRequestHandler(server);
	}

	private static void createClientRequestHandler(
			final VanillaCommServer server) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						//send request to vanillacomm
						Serializable message = msgQueue.take();
						server.sendTotalOrderMessage(message);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

		}).start();
	}

	@Override
	public void onServerReady() {
		if (logger.isLoggable(Level.INFO))
			logger.info("The server is ready!");
	}

	@Override
	public void onServerFailed(int failedServerId) {
		if (logger.isLoggable(Level.SEVERE))
			logger.severe("Server " + failedServerId + " failed");
	}

	@Override
	public void onReceiveP2pMessage(ProcessType senderType, int senderId, Serializable message) {
		if (senderType == ProcessType.CLIENT) {
			//receive request from bench client 
			try {
				msgQueue.put(message);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onReceiveTotalOrderMessage(long serialNumber, Serializable message) {
		//send to scheduler
		System.out.println("Received a total order message: " + message
				+ ", serial number: " + serialNumber);
	}
}
