package org.ufsc.rendezvous.dictionary.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.springframework.stereotype.Service;
import org.ufsc.rendezvous.dictionary.Dictionary;
import org.ufsc.rendezvous.fragmenter.Fragment;
import org.ufsc.rendezvous.partitioner.Partition;

@Service
public class DictionaryImpl implements Dictionary {

	Map<Node, Set<Partition>> partitions = new HashMap<Node, Set<Partition>>();
	
	Map<Partition, Set<Node>> subjects = new HashMap<Partition, Set<Node>>();
	Map<Partition, Set<Node>> predicates = new HashMap<Partition, Set<Node>>();
	Map<Partition, Set<Node>> objects = new HashMap<Partition, Set<Node>>();
	
	Map<Fragment, Set<Partition>> fragments = new HashMap<Fragment, Set<Partition>>();

	@Override
	public void register(Fragment fragment, List<Partition> partitions) {
		
		for(Partition partition:partitions){
			
			fragments.put(fragment, addFragmentToSet(fragments, fragment, partition));
	
			registerTriple(fragment.getCoreTriple(), partition);
			
			for(Integer n:fragment.getTriples().keySet()){
				
				for(Triple triple:fragment.getTriples().get(n)){
					registerTriple(triple, partition);
				}
			}
		}
	}
	
	private void registerTriple(Triple triple, Partition partition){
		subjects.put(partition, addNodeToSet(subjects, partition, triple.getSubject()));
		predicates.put(partition, addNodeToSet(predicates, partition, triple.getPredicate()));
		objects.put(partition, addNodeToSet(objects, partition, triple.getObject()));
		
		partitions.put(triple.getSubject(), addPartitionToSet(triple.getSubject(), partition));
		partitions.put(triple.getPredicate(), addPartitionToSet(triple.getPredicate(), partition));
		partitions.put(triple.getObject(), addPartitionToSet(triple.getObject(), partition));
	}

	private synchronized Set<Partition> addPartitionToSet(Node resource, 
			Partition partition) {
		
	    Set<Partition> resources = partitions.get(resource);
	    
	    if(resources == null) {
	    	resources = new HashSet<Partition>();
	    	resources.add(partition);
	    } else {
	        if(!resources.contains(partition)) resources.add(partition);
	    }
		return resources;
	}
	
	private synchronized Set<Node> addNodeToSet(Map<Partition, Set<Node>> set, 
			Partition partition, 
			Node node) {
		
	    Set<Node> nodes = set.get(partition);
	    
	    if(nodes == null) {
	    	nodes = new HashSet<Node>();
	    	nodes.add(node);
	    } else {
	        if(!nodes.contains(node)) nodes.add(node);
	    }
		return nodes;
	}
	
	private synchronized Set<Partition> addFragmentToSet(Map<Fragment, Set<Partition>> fragments, 
			Fragment fragment,
			Partition partition) {
		
	    Set<Partition> partitions = fragments.get(fragment);
	    
	    if(partitions == null) {
	    	partitions = new HashSet<Partition>();
	    	partitions.add(partition);
	    } else {
	        if(!partitions.contains(partition)) partitions.add(partition);
	    }
		return partitions;
	}

	@Override
	public Set<Partition> getPartitions(Triple triple) {
		Set<Partition> allPartitions = new HashSet<Partition>();
		
		allPartitions.addAll(partitions.get(triple.getSubject()));
		allPartitions.addAll(partitions.get(triple.getPredicate()));
		allPartitions.addAll(partitions.get(triple.getObject()));
		
		return allPartitions;
	}
	
}
