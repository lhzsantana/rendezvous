package org.ufsc.rendezvous.databases.document;

import java.util.List;

import org.json.JSONObject;
import org.ufsc.rendezvous.fragmenter.Fragment;
import org.ufsc.rendezvous.partitioner.Partition;

public interface DocumentDatabase {
	
	public void store(Partition partition, JSONObject fragment);

	public List<Fragment> query(Partition partition, JSONObject query);

}
