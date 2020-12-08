package localSupProJ.service;

import java.util.HashMap;

public interface DBAccessService {

	public void mergeData(HashMap<String , String> params, String sqlId) throws Exception;
	public void updateData(HashMap<String , String> params, String sqlId) throws Exception;
}