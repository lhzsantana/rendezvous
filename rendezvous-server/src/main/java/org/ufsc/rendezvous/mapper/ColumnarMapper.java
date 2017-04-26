package org.ufsc.rendezvous.mapper;

import org.ufsc.rendezvous.fragmenter.ColumnarFragment;
import org.ufsc.rendezvous.fragmenter.Fragment;

public interface ColumnarMapper extends DocumentMapper {

	ColumnarFragment map(Fragment fragment);

}
