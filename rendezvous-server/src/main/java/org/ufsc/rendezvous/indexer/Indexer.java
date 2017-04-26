package org.ufsc.rendezvous.indexer;

import java.util.List;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;

public interface Indexer {

	public void index(Triple triple);

	public List<Triple> getFromObject(Node object);

	public List<Triple> getFromSubject(Node subject);

}
