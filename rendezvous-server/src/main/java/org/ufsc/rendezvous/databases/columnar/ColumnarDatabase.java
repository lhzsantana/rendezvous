package org.ufsc.rendezvous.databases.columnar;

import java.util.List;

import org.apache.jena.graph.Triple;
import org.ufsc.rendezvous.fragmenter.ColumnarFragment;
import org.ufsc.rendezvous.partitioner.Partition;

public interface ColumnarDatabase {
	
	public void store(Partition partition, ColumnarFragment fragment);
	
	public List<Triple> query(Partition partition, List<Triple> chain);
	
	
}
