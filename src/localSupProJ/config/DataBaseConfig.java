package localSupProJ.config;

import java.io.InputStream;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

/*
 * 1. 모든 마이바티스 애플리케이션은 SqlSessionFactory 인스턴스를 사용
 * 2. SqlSessionFactory인스턴스는 SqlSessionFactoryBuilder를 사용하여 만들수 있다.
 * 3. 클래스패스 자원을 사용하는 것을 추천하나 파일 경로로부터 만들어진 InputStream인스턴스를 사용할 수도 있다. 
 * 마이바티스는 클래스패스와 다른 위치에서 자원을 로드하는 것으로 좀더 쉽게 해주는 Resources 라는 유틸성 클래스를 가지고 있다. 
 */

public class DataBaseConfig {
	private String mybatis_config_path = null;
	private SqlSession mFactorySession = null;
	
	public DataBaseConfig() {
		try {
			mybatis_config_path = "localSupProJ/config/mybatis-config.xml";
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static DataBaseConfig getInstnace() {
		return LazyHolder.INSTANCE;
	}
	
	private SqlSessionFactory sqlSessionFactory() throws Exception{
		InputStream inputStream = Resources.getResourceAsStream(mybatis_config_path);
		SqlSessionFactory bean = new SqlSessionFactoryBuilder().build(inputStream);
		return bean;
	}
	
	public SqlSession openSqlSession() throws Exception{
		if(mFactorySession == null){
			SqlSessionFactory factory = sqlSessionFactory();
			mFactorySession = factory.openSession();
		}
		return mFactorySession;
	}
	
	public void closeSqlSession(){
		if(mFactorySession != null){
			mFactorySession.close();
		}
		mFactorySession = null;
	}
	
	private static class LazyHolder{
		private static final DataBaseConfig INSTANCE = new DataBaseConfig();
	}
}
