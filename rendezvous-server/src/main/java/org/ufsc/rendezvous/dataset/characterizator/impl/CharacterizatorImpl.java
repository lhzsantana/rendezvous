package org.ufsc.rendezvous.dataset.characterizator.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.springframework.beans.factory.annotation.Value;
import org.ufsc.rendezvous.dataset.characterizator.Characterizator;
import org.ufsc.rendezvous.fragmenter.Fragment.Type;
import org.ufsc.rendezvous.query.decomposer.Shape;

public class CharacterizatorImpl implements Characterizator {

	@Value("${defaults.hop_size}")
	private static final Integer DEFAULT_HOP_SIZE = 3;
	
	private static Map<Node, List<List<Triple>>> starShaped = new HashMap<Node, List<List<Triple>>>();
	private static Map<Node, List<List<Triple>>> chainShaped = new HashMap<Node, List<List<Triple>>>();

	@Override
	public Integer getHopSize(Triple coreTriple, Type type) {
		if (type == Type.STAR_SUBJECT) {
			return getMaxDiameter(starShaped.get(coreTriple.getSubject()));
		} else if (type == Type.STAR_OBJECT) {
			return getMaxDiameter(starShaped.get(coreTriple.getObject()));
		} else if (type == Type.CHAIN) {
			return getMaxChain(chainShaped.get(coreTriple.getPredicate()));
		}
		
		return DEFAULT_HOP_SIZE;
	}

	@Override
	public Type getTypicalShape(Triple coreTriple) {

		Integer subjectStarShapedSize = starShaped.get(coreTriple.getSubject()).size();
		Integer objectStarShapedSize = starShaped.get(coreTriple.getObject()).size();
		Integer chainShapedSize = chainShaped.get(coreTriple.getPredicate()).size();

		if (subjectStarShapedSize > objectStarShapedSize && subjectStarShapedSize > chainShapedSize) {
			return Type.STAR_SUBJECT;
		} else if (objectStarShapedSize > subjectStarShapedSize && objectStarShapedSize > chainShapedSize) {
			return Type.STAR_OBJECT;
		} else if (chainShapedSize > objectStarShapedSize && chainShapedSize > objectStarShapedSize) {
			return Type.CHAIN;
		}

		return Type.UNKNOW;
	}

	private Integer getMaxDiameter(List<List<Triple>> patterns) {
		int max = 0;

		for (List<Triple> pattern : patterns) {
			if (pattern.size() > max) {
				max = pattern.size();
			}
		}

		return max;
	}

	private Integer getMaxChain(List<List<Triple>> patterns) {
		int max = 0;

		for (List<Triple> pattern : patterns) {
			if (pattern.size() > max) {
				max = pattern.size();
			}
		}

		return max;
	}

	@Override
	public void addShape(Triple triple, Shape shape) {

		if(shape.getType()==Type.STAR_OBJECT || shape.getType()==Type.STAR_SUBJECT ){
			starShaped.put(triple.getSubject(), addToList(starShaped, shape.getTriples()));
		} else if(shape.getType()==Type.CHAIN ){
			chainShaped.put(triple.getPredicate(), addToList(chainShaped, shape.getTriples()));
		}
	}
	
	private synchronized List<List<Triple>> addToList(Map<Node, List<List<Triple>>> index, List<Triple> list) {
	    List<List<Triple>> triples = index.get(list);
	    
	    if(triples == null) {
	    	ArrayList<Triple> listOfTriples = new ArrayList<Triple>();
	    	listOfTriples.addAll(list);
	    	ArrayList<Triple> listOfLists = new ArrayList<Triple>();
	    	triples.add(listOfTriples);
	    } else {
	        if(!triples.contains(list)) triples.add(list);
	    }
		return triples;
	}
}
