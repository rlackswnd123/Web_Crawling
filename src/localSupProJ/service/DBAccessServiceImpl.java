package localSupProJ.service;

import java.util.HashMap;
import org.apache.ibatis.session.SqlSession;

public class DBAccessServiceImpl implements DBAccessService{
	private SqlSession session;

	public DBAccessServiceImpl( SqlSession session){
		this.session = session;
	}

	@Override
	public void mergeData(HashMap<String, String> params, String sqlId) throws Exception {
		session.update(sqlId , params);
		session.commit();
	}

	@Override
	public void updateData(HashMap<String, String> params, String sqlId) throws Exception {
		session.update(sqlId , params);
		session.commit();
	}

}