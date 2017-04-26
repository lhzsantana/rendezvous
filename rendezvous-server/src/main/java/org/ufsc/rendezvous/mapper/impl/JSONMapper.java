package org.ufsc.rendezvous.mapper.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.ufsc.rendezvous.fragmenter.Fragment;
import org.ufsc.rendezvous.mapper.DocumentMapper;
import org.ufsc.rendezvous.query.decomposer.Shape;

@Service
public class JSONMapper implements DocumentMapper {
	
	private static final String SUBJECT = "subject";
	private static final String OBJECT = "object";

	@Override
	public JSONObject map(Shape shape) {

		JSONObject obj = new JSONObject();


		return obj;
	}

	public JSONObject map(Fragment fragment) {
		
		JSONObject obj = new JSONObject();
		
		try {
			
			if(fragment.getSize()==0){
				obj.put(SUBJECT, fragment.getCoreTriple().getSubject().getName());
				obj.put(fragment.getCoreTriple().getPredicate().getName(),
						fragment.getCoreTriple().getSubject().getName());
			}else{
				obj = map(SUBJECT, fragment, fragment.getCoreTriple(), fragment.getTriples().get(1), 1);
			}
		} catch (JSONException e) {
			
		}

		return obj;
	}
	
	private JSONObject map(String labelDirection, 
			Fragment fragment, 
			Triple coreTriple, 
			Set<Triple> triples, 
			Integer currentHop) throws JSONException {

		JSONObject obj = new JSONObject();

		if(labelDirection.equals(SUBJECT)){
			obj.put(labelDirection, fragment.getCoreTriple().getSubject().getName());
			obj.put(fragment.getCoreTriple().getPredicate().getName(),
					fragment.getCoreTriple().getObject().getName());
			
		}else{
			obj.put(labelDirection, fragment.getCoreTriple().getObject().getName());
			obj.put(fragment.getCoreTriple().getPredicate().getName(),
					fragment.getCoreTriple().getSubject().getName());
		}
		
		Set<Triple> nextTriples= fragment.getTriples().get(currentHop+1);
		Map<Node, Set<Triple>> S_PO = new HashMap<Node, Set<Triple>>();
		Map<Node, Set<Triple>> O_PS = new HashMap<Node, Set<Triple>>();

		for(Triple triple:nextTriples){
			S_PO.put(triple.getSubject(), addToList(S_PO, triple));
			O_PS.put(triple.getObject(), addToList(O_PS, triple));
		}

		String label="";
		Node resource=null;
		
		for(Triple triple:triples){
			
			if(triple==coreTriple){
				continue;
			}

			if(S_PO.get(triple.getSubject()).size()>0 || O_PS.get(triple.getObject()).size()>0){
				if(S_PO.get(triple.getSubject()).size()>0){
					obj.put(triple.getPredicate().getLiteralValue().toString(), map(SUBJECT, fragment, triple,nextTriples, currentHop+1));
				}
				
				if(O_PS.get(triple.getObject()).size()>0){
					obj.put(triple.getPredicate().getLiteralValue().toString(), map(OBJECT, fragment, triple,nextTriples, currentHop+1));
				}
			}else{
				
				label=triple.getPredicate().getLiteralValue().toString();
			
				if(triple.getSubject()==coreTriple.getSubject() || triple.getObject()==coreTriple.getSubject()){
					if(labelDirection.equals(SUBJECT)){
						resource=triple.getObject();
					}else{
						resource=triple.getSubject();
					}
				}
	
				if(triple.getSubject()==coreTriple.getObject() || triple.getObject()==coreTriple.getObject()){
					if(labelDirection.equals(SUBJECT)){
						resource=triple.getSubject();
					}else{
						resource=triple.getObject();
					}
				}
				
				obj.put(label, resource);
			}
		}
		
		return obj;
	}

	private synchronized Set<Triple> addToList(Map<Node, Set<Triple>> s_PO, Triple triple) {
	    Set<Triple> triples = s_PO.get(triple);
	    
	    if(triples == null) {
	    	triples = new HashSet<Triple>();
	    	triples.add(triple);
	    } else {
	        if(!triples.contains(triple)) triples.add(triple);
	    }
		return triples;
	}

}
