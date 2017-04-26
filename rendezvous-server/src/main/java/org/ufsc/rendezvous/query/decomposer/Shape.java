package org.ufsc.rendezvous.query.decomposer;

import java.util.List;

import org.apache.jena.graph.Triple;
import org.ufsc.rendezvous.fragmenter.Fragment.Type;

public class Shape {
	
	private List<Triple> triples;
	private Type type;
	
	public List<Triple> getTriples() {
		return triples;
	}
	public void setTriples(List<Triple> triples) {
		this.triples = triples;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
}
