package org.ufsc.rendezvous.indexer.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.springframework.stereotype.Service;
import org.ufsc.rendezvous.indexer.Indexer;

@Service
public class IndexerImpl implements Indexer{
	
	private static Map<Node, List<Triple>> S_PO = new HashMap<Node, List<Triple>>();
	private static Map<Node, List<Triple>> O_PS = new HashMap<Node, List<Triple>>();

	@Override
	public void index(Triple triple) {
		S_PO.put(triple.getSubject(), addToList(S_PO, triple));
		O_PS.put(triple.getObject(),  addToList(O_PS, triple));
	}

	@Override
	public List<Triple> getFromObject(Node object) {
		return O_PS.get(object);
	}

	@Override
	public List<Triple> getFromSubject(Node subject) {
		return S_PO.get(subject);
	}
	
	private synchronized List<Triple> addToList(Map<Node, List<Triple>> index, Triple triple) {
	    List<Triple> triples = index.get(triple);
	    
	    if(triples == null) {
	    	triples = new ArrayList<Triple>();
	    	triples.add(triple);
	    } else {
	        if(!triples.contains(triple)) triples.add(triple);
	    }
		return triples;
	}

}
