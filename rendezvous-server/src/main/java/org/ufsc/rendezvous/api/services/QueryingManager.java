package org.ufsc.rendezvous.api.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.BasicPattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ufsc.rendezvous.databases.columnar.ColumnarDatabase;
import org.ufsc.rendezvous.databases.document.DocumentDatabase;
import org.ufsc.rendezvous.fragmenter.Fragment;
import org.ufsc.rendezvous.fragmenter.Fragment.Type;
import org.ufsc.rendezvous.mapper.DocumentMapper;
import org.ufsc.rendezvous.partitioner.Partition;
import org.ufsc.rendezvous.query.decomposer.QueryDecomposer;
import org.ufsc.rendezvous.query.decomposer.Shape;

@Service
public class QueryingManager {

	@Autowired
	private DocumentMapper documentMapper;
	
	@Autowired
	private Partitioner partitioner;

	@Autowired
	private ColumnarDatabase columnarDatabase;

	@Autowired
	private DocumentDatabase documentDatabase;

	@Autowired
	private QueryDecomposer queryDecomposer;

	public Set<Triple> queryBGP(BasicPattern bgp) {
		
		Set<Triple> response = new HashSet<Triple>();

		for (Shape shape : queryDecomposer.decompose(bgp)) {

			List<Partition> partitions = partitioner.getPartitions(shape);
			
			if(shape.getType()==Type.STAR_OBJECT || shape.getType()==Type.STAR_SUBJECT){
				for(Partition partition:partitions){
					for(Fragment fragment:documentDatabase.query(partition, documentMapper.map(shape))){
						for(Set<Triple> triples:fragment.getTriples().values()){
							response.addAll(triples);	
						}
					}
				}
				
			}else if(shape.getType()==Type.CHAIN){
				for(Partition partition:partitions){
					response.addAll(columnarDatabase.query(partition, shape.getTriples()));
				}
			}
		}
		
		return response;
	}
}
