package org.ufsc.rendezvous.fragmenter.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.jena.graph.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.ufsc.rendezvous.dataset.characterizator.Characterizator;
import org.ufsc.rendezvous.fragmenter.Fragment;
import org.ufsc.rendezvous.fragmenter.Fragment.Type;
import org.ufsc.rendezvous.fragmenter.Fragmenter;
import org.ufsc.rendezvous.indexer.Indexer;

public class FragmenterImpl implements Fragmenter {

	@Autowired
	private Characterizator characterizator;
	
	@Autowired
	private Indexer indexer;

	@Override
	public List<Fragment> fragment(Triple triple) {
		
		indexer.index(triple);

		List<Fragment> fragments = new ArrayList<Fragment>();
		
		Type shape = characterizator.getTypicalShape(triple);
		
		if(shape==Type.UNKNOW || shape==Type.COMPLEX){
			fragments.add(createFragment(triple,  Type.STAR_OBJECT));
			fragments.add(createFragment(triple,  Type.STAR_SUBJECT));
			fragments.add(createFragment(triple,  Type.CHAIN));
		}else{
			fragments.add(createFragment(triple, shape));
		}
		
		return fragments;
	}

	private Fragment createFragment(Triple triple, Fragment.Type type) {

		Fragment fragment = new Fragment();

		fragment.setType(type);
		fragment.setCoreTriple(triple);
		fragment.setSize(characterizator.getHopSize(fragment.getCoreTriple(), type));

		HashMap<Integer, Set<Triple>> triples = new HashMap<Integer, Set<Triple>>();
		triples.put(fragment.getSize(), expand(fragment.getSize(), fragment.getCoreTriple()));
		fragment.setTriples(triples);

		return fragment;
	}

	private Set<Triple> expand(Integer size, Triple coreTriple) {

		Set<Triple> allTriples = new HashSet<Triple>();
		
		if(size==0){
			return allTriples;
		}
		
		List<Triple> border = new ArrayList<Triple>();
		List<Triple> triplesFromObject = indexer.getFromObject(coreTriple.getObject());
		List<Triple> triplesFromSubject = indexer.getFromSubject(coreTriple.getSubject());

		border.addAll(triplesFromObject);
		border.addAll(triplesFromSubject);
		
		allTriples.addAll(border);
		
		for(Triple triple: border){
			allTriples.addAll(expand(--size, triple));
		}
		
		return allTriples;
	}
}
