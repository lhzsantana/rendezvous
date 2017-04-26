package org.ufsc.rendezvous.fragmenter;

import java.util.List;

import org.apache.jena.graph.Triple;

public interface Fragmenter {
	
	public List<Fragment> fragment(Triple triple);

}
