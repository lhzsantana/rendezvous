package org.ufsc.rendezvous.api.services;

import java.util.List;

import org.ufsc.rendezvous.fragmenter.Fragment;
import org.ufsc.rendezvous.partitioner.Partition;
import org.ufsc.rendezvous.query.decomposer.Shape;

public interface Partitioner {

	List<Partition> getPartitions();

	List<Partition> getPartitions(Shape shape);

	List<Partition> getPartitions(Fragment fragment);

}
