package org.ufsc.rendezvous.databases.columnar.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.ufsc.rendezvous.databases.columnar.ColumnarDatabase;
import org.ufsc.rendezvous.fragmenter.ColumnarFragment;
import org.ufsc.rendezvous.partitioner.Partition;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

public class CassandraDatabase implements ColumnarDatabase {

	private Session session;

	private Cluster cluster;

	private void connect(Partition partition) {
		
		cluster = Cluster.builder().addContactPoint(partition.getNode()).build();

		session = cluster.connect();
	}

	@Override
	public void store(Partition partition, ColumnarFragment fragment) {
		
		connect(partition);
		
		store(fragment.getCoreTriple());

		for (Set<Triple> hopTriples : fragment.getTriples().values()) {
			for (Triple triple : hopTriples) {
				store(triple);
			}
		}
	}

	private void store(Triple triple) {

		if (!familyExists(triple.getPredicate().getLocalName())) {
		    session.execute("CREATE TABLE "+
		    		triple.getPredicate().getLocalName()+
		    		" (SUBJECT text, PREDICATE text);");
		}

	    session.execute("INSERT INTO "+
	    		triple.getPredicate().getLocalName()+
	    		" (SUBJECT, PREDICATE) "
	            + "VALUES ("
	            + triple.getSubject().getName()
	            + ","
	            + triple.getObject().getName()
	            + ");");
	}

	private boolean familyExists(String predicate) {
		
		ResultSet resultSet = session.execute("SELECT table_name  FROM system_schema.tables WHERE keyspace_name='"+predicate+"';");
		
		return !resultSet.all().isEmpty();
	}

	@Override
	public List<Triple> query(Partition partition, List<Triple> chain) {
		
		connect(partition);
		
		List<String> current = new ArrayList<String>();
		current.add("");

		ResultSet resultSet = null;
		
		for(Triple triple: chain){

			List<String> subjects = new ArrayList<String>();
			
			for(String subject:current){

				resultSet = session.execute("SELECT SUBJECT, OBJECT FROM " + 
						triple.getPredicate().getLocalName()+subject);
				
				for(Row row : resultSet.all()){
					subjects.add(" WHERE SUBJECT='"+row.getString("OBJECT")+"';");
				}
			}
			
			current=subjects;
		}

		List<Triple> triples = new ArrayList<Triple>();
		for(Row row : resultSet.all()){
			Resource subject = ResourceFactory.createResource(row.getString("SUBJECT"));
			Resource object = ResourceFactory.createResource(row.getString("OBJECT"));
			
			triples.add(new Triple(subject.asNode(), chain.get(chain.size()).getPredicate(), object.asNode()));
		}
		
		return triples;
	}

}
