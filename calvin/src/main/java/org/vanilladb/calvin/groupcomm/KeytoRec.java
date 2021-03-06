package org.vanilladb.calvin.groupcomm;

import java.io.Serializable;

import org.vanilladb.calvin.cache.CachedRecord;
import org.vanilladb.calvin.sql.RecordKey;

public class KeytoRec implements Serializable{
	private static final long serialVersionUID = -114514L;
	public RecordKey key;
	public CachedRecord rec;
	public long srcTxNum;
	public long destTxNum;

	public KeytoRec(RecordKey key, long srcTxNum, long destTxNum, CachedRecord rec) {
		this.key = key;
		this.rec = rec;
		this.srcTxNum = srcTxNum;
		this.destTxNum = destTxNum;
	}
}
