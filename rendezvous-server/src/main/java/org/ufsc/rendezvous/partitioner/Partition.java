package org.ufsc.rendezvous.partitioner;

import com.mongodb.MongoOptions;

public interface Partition {

	String getNode();

	String getDatabaseName();

	String getHost();

	MongoOptions getPort();

	String getCollectionName();

}
