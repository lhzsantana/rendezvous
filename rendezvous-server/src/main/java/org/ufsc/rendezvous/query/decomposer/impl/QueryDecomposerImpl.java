package org.ufsc.rendezvous.query.decomposer.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.BasicPattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.ufsc.rendezvous.dataset.characterizator.Characterizator;
import org.ufsc.rendezvous.fragmenter.Fragment.Type;
import org.ufsc.rendezvous.query.decomposer.QueryDecomposer;
import org.ufsc.rendezvous.query.decomposer.Shape;

public class QueryDecomposerImpl implements QueryDecomposer {

	@Autowired
	private Characterizator characterizator;
	
	private enum Direction {
		OBJECT_SUBJECT, SUBJECT_OBJECT
	}

	private static Map<Node, List<Triple>> S_PO = new HashMap<Node, List<Triple>>();
	private static Map<Node, List<Triple>> O_PS = new HashMap<Node, List<Triple>>();

	@Override
	public List<Shape> decompose(BasicPattern bgp) {

		for (Triple triple : bgp.getList()) {
			S_PO.put(triple.getSubject(), addToList(S_PO, triple));
			O_PS.put(triple.getObject(), addToList(O_PS, triple));
		}

		List<Shape> shapes = new ArrayList<Shape>();

		for (Triple triple : bgp.getList()) {

			List<Triple> bySubjects = S_PO.get(triple.getSubject());
			List<Triple> byObjects = O_PS.get(triple.getObject());

			if (bySubjects.size() > 1) {
				Shape shape = new Shape();
				shape.setTriples(expandStar(bySubjects, bySubjects, Direction.SUBJECT_OBJECT));
				shape.setType(Type.STAR_SUBJECT);

				shapes.add(shape);
				
				characterizator.addShape(triple, shape);
			}

			if (byObjects.size() > 1) {
				Shape shape = new Shape();
				shape.setTriples(expandStar(byObjects, byObjects, Direction.OBJECT_SUBJECT));
				shape.setType(Type.STAR_OBJECT);

				shapes.add(shape);
				
				characterizator.addShape(triple, shape);
			}

			Shape shape = new Shape();
			shape.setTriples(longestChain(
					findChains(new ArrayList<>(), S_PO.get(triple.getSubject()), Direction.SUBJECT_OBJECT),
					findChains(new ArrayList<>(), O_PS.get(triple.getSubject()), Direction.OBJECT_SUBJECT)
			));
			shape.setType(Type.CHAIN);
			shapes.add(shape);

			characterizator.addShape(triple, shape);
		}

		return shapes;
	}

	private List<Triple> expandStar(List<Triple> tripes, List<Triple> lastAddedTripes, Direction direction) {

		List<Triple> newTripes = new ArrayList<Triple>();

		for (Triple triple : lastAddedTripes) {

			if (direction.equals(Direction.OBJECT_SUBJECT)) {
				newTripes.addAll(S_PO.get(triple.getObject()));
			} else if (direction.equals(Direction.SUBJECT_OBJECT)) {
				newTripes.addAll(O_PS.get(triple.getSubject()));
			}
		}

		tripes.addAll(newTripes);

		expandStar(tripes, newTripes, direction);

		return tripes;
	}

	private synchronized List<Triple> addToList(Map<Node, List<Triple>> index, Triple triple) {
		List<Triple> triples = index.get(triple);

		if (triples == null) {
			triples = new ArrayList<Triple>();
			triples.add(triple);
		} else {
			if (!triples.contains(triple))
				triples.add(triple);
		}
		return triples;
	}

	private List<List<Triple>> findChains(List<Triple> chain, List<Triple> triples, Direction direction) {

		if (triples == null) {
			return null;
		}

		List<List<Triple>> chains = new ArrayList<List<Triple>>();

		for (Triple triple : triples) {

			List<Triple> newChain = new ArrayList<Triple>();
			newChain.addAll(chain);

			List<Triple> next = null;

			if (direction.equals(Direction.OBJECT_SUBJECT)) {
				next = S_PO.get(triple.getSubject());
			} else if (direction.equals(Direction.SUBJECT_OBJECT)) {
				next = O_PS.get(triple.getObject());
			}

			chains.addAll(findChains(chain, next, direction));

			chains.add(newChain);
		}

		return chains;
	}

	private List<Triple> longestChain(List<List<Triple>> chainsOS, List<List<Triple>> chainsSO) {

		List<Triple> longestChainOS = chainsOS.get(0);

		for (List<Triple> chain : chainsOS) {
			if (chain.size() > longestChainOS.size()) {
				longestChainOS = chain;
			}
		}

		List<Triple> longestChainSO = chainsSO.get(0);

		for (List<Triple> chain : chainsSO) {
			if (chain.size() > longestChainSO.size()) {
				longestChainSO = chain;
			}
		}

		longestChainSO.addAll(longestChainOS);

		return longestChainSO;
	}
}
