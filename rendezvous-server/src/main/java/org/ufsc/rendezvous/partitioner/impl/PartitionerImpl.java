package org.ufsc.rendezvous.partitioner.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.graph.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.ufsc.rendezvous.api.services.Partitioner;
import org.ufsc.rendezvous.dictionary.Dictionary;
import org.ufsc.rendezvous.fragmenter.Fragment;
import org.ufsc.rendezvous.partitioner.Partition;
import org.ufsc.rendezvous.query.decomposer.Shape;

public class PartitionerImpl implements Partitioner {

	private static List<Partition> partitions = null;

	{
		partitions = new ArrayList<Partition>();
	}

	@Autowired
	private Dictionary dictionary;

	@Override
	public List<Partition> getPartitions() {
		return partitions;
	}

	@Override
	public List<Partition> getPartitions(Shape shape) {

		List<Partition> partitions = new ArrayList<Partition>();

		for (Triple triple : shape.getTriples()) {
			partitions.addAll(dictionary.getPartitions(triple));
		}

		return partitions;
	}

	@Override
	public List<Partition> getPartitions(Fragment fragment) {
		// TODO Auto-generated method stub
		return null;
	}

}
