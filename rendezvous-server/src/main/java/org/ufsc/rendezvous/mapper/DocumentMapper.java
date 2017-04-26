package org.ufsc.rendezvous.mapper;

import org.json.JSONObject;
import org.ufsc.rendezvous.query.decomposer.Shape;

public interface DocumentMapper {

	JSONObject map(Shape shape);

}
