package org.ufsc.rendezvous.databases.document.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.ufsc.rendezvous.databases.document.DocumentDatabase;
import org.ufsc.rendezvous.fragmenter.Fragment;
import org.ufsc.rendezvous.partitioner.Partition;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

public class MongoDBDatabase implements DocumentDatabase {

	private DBCollection collection;

	private void connect(Partition partition) {

		Mongo mongo = new Mongo(partition.getHost(), partition.getPort());
		DB db = mongo.getDB(partition.getDatabaseName());

		collection = db.getCollection(partition.getCollectionName());
	}

	@Override
	public void store(Partition partition, JSONObject fragment) {

		connect(partition);

		collection.insert((DBObject) fragment);
	}

	@Override
	public List<Fragment> query(Partition partition, JSONObject query) {
		
		connect(partition);
		
		DBCursor cursor = collection.find((DBObject) query);
		
		List<Fragment> fragments = new ArrayList<Fragment>();
		
		while (cursor.hasNext()) {
			System.out.println(cursor.next());
		}
		
		return fragments;
	}

}
