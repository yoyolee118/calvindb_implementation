package org.vanilladb.calvin.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vanilladb.calvin.cache.CalvinCacheMgr;
import org.vanilladb.calvin.groupcomm.server.ConnMgr;
import org.vanilladb.calvin.metadata.MetadataMgr;
import org.vanilladb.calvin.recovery.CalvinLogMgr;
import org.vanilladb.calvin.scheduler.CalvinScheduler;
import org.vanilladb.core.query.planner.Planner;
import org.vanilladb.core.query.planner.QueryPlanner;
import org.vanilladb.core.query.planner.UpdatePlanner;
import org.vanilladb.core.query.planner.index.IndexUpdatePlanner;
import org.vanilladb.core.query.planner.opt.HeuristicQueryPlanner;
import org.vanilladb.core.server.VanillaDb;
import org.vanilladb.core.server.task.TaskMgr;
import org.vanilladb.core.sql.storedprocedure.SampleStoredProcedureFactory;
import org.vanilladb.core.sql.storedprocedure.StoredProcedureFactory;
import org.vanilladb.core.storage.file.FileMgr;
import org.vanilladb.core.storage.log.LogMgr;
import org.vanilladb.core.storage.metadata.CatalogMgr;
import org.vanilladb.core.storage.metadata.statistics.StatMgr;
import org.vanilladb.core.storage.tx.Transaction;
import org.vanilladb.core.storage.tx.TransactionMgr;
import org.vanilladb.core.storage.tx.recovery.CheckpointTask;
import org.vanilladb.core.storage.tx.recovery.RecoveryMgr;
import org.vanilladb.core.util.CoreProperties;
import org.vanilladb.core.util.Profiler;

/**
 * The class that provides system-wide static global values. These values must
 * be initialized by the method {@link #init(String) init} before use. The
 * methods {@link #initConnMgr(int) initConnMgr},
 * provide limited
 * initialization, and are useful for debugging purposes.
 */
public class Calvin extends VanillaDb{

	// Logger
	private static Logger logger = Logger.getLogger(VanillaDb.class.getName());
	
	private static int nodeId;
	
	private static ConnMgr connMgr;
	private static MetadataMgr metaMgr;
	private static CalvinLogMgr calvinLogMgr;
	private static CalvinScheduler scheduler;
	private static CalvinCacheMgr cacheMgr;

	public static void init(String dirName, int id) {
		nodeId = id;

		if (logger.isLoggable(Level.INFO))
			logger.info("Calvin initializing...");

		// initialize core modules
		VanillaDb.init(dirName);
		
		// initialize Calvin modules
		initCacheMgr();
		initMetaMgr();
		initScheduler();
		initConnMgr(nodeId);
		initCalvinLogMgr();
	}
	
	public static void init(String dirName, int id, StoredProcedureFactory factory) {
		nodeId = id;

		if (logger.isLoggable(Level.INFO))
			logger.info("Calvin initializing...");

		// initialize core modules
		VanillaDb.init(dirName, factory);
		
		// initialize Calvin modules
		initCacheMgr();
		initMetaMgr();
		initScheduler();
		initConnMgr(nodeId);
		initCalvinLogMgr();
	}
	
	/**
	 * initializer 
	 */
	
	static void initConnMgr(int id) {
		connMgr = new ConnMgr(id);
	}
	
	static void initMetaMgr() {
		metaMgr = new MetadataMgr();
	}
	
	static void initCalvinLogMgr() {
		calvinLogMgr = new CalvinLogMgr();
	}
	
	static void initScheduler() {
		scheduler = new CalvinScheduler();
		taskMgr().runTask(scheduler);
	}
	
	static void initCacheMgr() {
		cacheMgr = new CalvinCacheMgr();
	}
	
	/**
	 * getter
	 */
	
	public static ConnMgr connMgr() {
		return connMgr;
	}
	
	public static MetadataMgr metaMgr() {
		return metaMgr;
	}
	
	public static CalvinLogMgr calvinLogMgr() {
		return calvinLogMgr;
	}
	
	public static CalvinScheduler scheduler() {
		return scheduler;
	}
	
	public static CalvinCacheMgr cacheMgr() {
		return cacheMgr;
	}
	
	public static int server_id() {
		return nodeId;
	}
	
}
