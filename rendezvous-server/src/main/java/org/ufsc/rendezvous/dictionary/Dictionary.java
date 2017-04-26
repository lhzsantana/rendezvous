package org.ufsc.rendezvous.dictionary;

import java.util.List;
import java.util.Set;

import org.apache.jena.graph.Triple;
import org.ufsc.rendezvous.fragmenter.Fragment;
import org.ufsc.rendezvous.partitioner.Partition;

public interface Dictionary {

	void register(Fragment fragment, List<Partition> partition);

	Set<Partition> getPartitions(Triple triple);

}
