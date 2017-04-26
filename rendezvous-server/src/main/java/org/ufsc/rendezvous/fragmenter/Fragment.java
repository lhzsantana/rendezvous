package org.ufsc.rendezvous.fragmenter;

import java.util.Map;
import java.util.Set;

import org.apache.jena.graph.Triple;

public class Fragment {
	
	private Triple coreTriple;
	
	private Map<Integer, Set<Triple>> triples;
	
	private Integer size;
	
	private Type type;
	

	public Triple getCoreTriple() {
		return coreTriple;
	}

	public void setCoreTriple(Triple coreTriple) {
		this.coreTriple = coreTriple;
	}

	public Map<Integer, Set<Triple>> getTriples() {
		return triples;
	}

	public void setTriples(Map<Integer, Set<Triple>> triples) {
		this.triples = triples;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}
	
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
	public enum Type{
		STAR_SUBJECT, STAR_OBJECT, CHAIN, COMPLEX, SIMPLE, UNKNOW
	}
}
