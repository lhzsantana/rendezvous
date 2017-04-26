package org.ufsc.rendezvous.dataset.characterizator;

import org.apache.jena.graph.Triple;
import org.ufsc.rendezvous.fragmenter.Fragment;
import org.ufsc.rendezvous.fragmenter.Fragment.Type;
import org.ufsc.rendezvous.query.decomposer.Shape;

public interface Characterizator {

	Integer getHopSize(Triple coreTriple, Type type);

	Fragment.Type getTypicalShape(Triple coreTriple);

	void addShape(Triple triple, Shape shape);

}
