package org.ufsc.rendezvous.api.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.ufsc.rendezvous.api.util.QueryResponse;
import org.ufsc.rendezvous.api.util.StorageResponse;

@RestController
public class ServicesController {

	private static Logger logger = LoggerFactory.getLogger(ServicesController.class);

	@Autowired
	private StoringManager storingManager;

	public ResponseEntity<QueryResponse> queryRequest(String query) {

		logger.debug("Responding to query {}", query);

		return ResponseEntity.status(HttpStatus.OK).body(null);

	}
	
	public @ResponseBody ResponseEntity<StorageResponse> store(String s, String p, String o) {

		logger.debug("Storing triple {} {} {}", s, p, o);

		storingManager.storeTriple(stringToTriple(s, p, o));

		return ResponseEntity.status(HttpStatus.OK).body(null);
	}

	private Triple stringToTriple(String s, String p, String o) {

		Resource subject = ResourceFactory.createResource(s);
		Resource predicate = ResourceFactory.createResource(p);
		Resource object = ResourceFactory.createResource(o);

		return new Triple(subject.asNode(), predicate.asNode(), object.asNode());
	}

	private static <T> List<List<T>> getPages(Collection<T> c, Integer pageSize) {
		if (c == null)
			return Collections.emptyList();
		List<T> list = new ArrayList<T>(c);
		if (pageSize == null || pageSize <= 0 || pageSize > list.size())
			pageSize = list.size();
		int numPages = (int) Math.ceil((double) list.size() / (double) pageSize);
		List<List<T>> pages = new ArrayList<List<T>>(numPages);
		for (int pageNum = 0; pageNum < numPages;)
			pages.add(list.subList(pageNum * pageSize, Math.min(++pageNum * pageSize, list.size())));
		return pages;
	}
}