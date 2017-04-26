package org.ufsc.rendezvous.api.services;

import java.util.List;

import org.apache.jena.graph.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.ufsc.rendezvous.databases.columnar.ColumnarDatabase;
import org.ufsc.rendezvous.databases.document.DocumentDatabase;
import org.ufsc.rendezvous.dictionary.Dictionary;
import org.ufsc.rendezvous.fragmenter.ColumnarFragment;
import org.ufsc.rendezvous.fragmenter.Fragment;
import org.ufsc.rendezvous.fragmenter.Fragment.Type;
import org.ufsc.rendezvous.fragmenter.Fragmenter;
import org.ufsc.rendezvous.mapper.ColumnarMapper;
import org.ufsc.rendezvous.mapper.impl.JSONMapper;
import org.ufsc.rendezvous.partitioner.Partition;

@Service
public class StoringManager {

	@Autowired
	private Fragmenter fragmenter;

	@Autowired
	private ColumnarMapper columnarMapper;

	@Autowired
	private JSONMapper documentMapper;

	@Autowired
	private Dictionary dictionary;

	@Autowired
	private Partitioner partitioner;

	@Autowired
	private ColumnarDatabase columnarDatabase;

	@Autowired
	private DocumentDatabase documentDatabase;

	@Value("${defaults.number_partitions}")
	private static final Integer NUMBER_PARTITIONS = 3;

	@Async
	public void storeTriple(Triple triple) {

		List<Fragment> fragments = fragmenter.fragment(triple);

		for (Fragment fragment : fragments) {
			
			List<Partition> partitions = partitioner.getPartitions();
			
			for(Partition partition: partitions){
			
				if (fragment.getType() == Type.STAR_OBJECT || fragment.getType() == Type.STAR_SUBJECT ) {
					documentDatabase.store(partition, documentMapper.map(fragment));
				}else if (fragment.getType() == Type.CHAIN) {
					columnarDatabase.store(partition, (ColumnarFragment) columnarMapper.map(fragment));
				}
			}
			
			dictionary.register(fragment, partitions);
		}
	}
}
