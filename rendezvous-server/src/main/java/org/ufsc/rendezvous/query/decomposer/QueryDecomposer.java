package org.ufsc.rendezvous.query.decomposer;

import java.util.List;

import org.apache.jena.sparql.core.BasicPattern;

public interface QueryDecomposer {

	List<Shape> decompose(BasicPattern bgp);

}
