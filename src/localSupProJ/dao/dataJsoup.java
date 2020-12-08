package localSupProJ.dao;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import localSupProJ.config.DataBaseConfig;
import localSupProJ.service.DBAccessService;
import localSupProJ.service.DBAccessServiceImpl;

public class dataJsoup {

	static Logger log = LoggerFactory.getLogger(dataJsoup.class);

	public static void procDataJsoup(ArrayList<ArrayList<HashMap<String, String>>> siteDetailList) throws Exception{

		SqlSession session = null;

		DBAccessServiceImpl dbAccessService = null;

		HashMap<String, String> paramMap = new HashMap<>();

		String[] sqlId = {"mergeData","mergeBoard","pidBoard"};		// 실행시킬 DBAccess.xml 쿼리 id

		try{
			session = DataBaseConfig.getInstnace().openSqlSession();

			dbAccessService = new DBAccessServiceImpl(session); 	// session Setter()

			for(ArrayList<HashMap<String, String>> siteDetail : siteDetailList) {
				for(HashMap<String, String> siteMap : siteDetail){
					paramMap = new HashMap<String, String>();
					paramMap = parsingColumn(siteMap);
					/*System.out.println("-------------------------------------------------"); 파싱한 키값을 DB컬럼명으로 변환 후 로그 확인*/
					//mergeData(dbAccessService,paramMap,sqlId[0]); /* DB저장 시 주석 해제 */
					//mergeData(dbAccessService,paramMap,sqlId[1]); /* DB저장 시 주석 해제 */
				}
				//updateData(dbAccessService,paramMap,sqlId[2]); /* DB저장 시 주석 해제 */
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			session.close();
		}
	}

	private static HashMap<String, String> parsingColumn(HashMap<String, String> siteMap) throws Exception{

		String hmKey = "";

		String hmVal = "";

		HashMap<String, String> fileMap = new HashMap<>();

		HashMap<String, String> fileUrlMap = new HashMap<>();

		HashMap<String, String> returnMap = new HashMap<>();

		String attachFiles = "";

		String attachFileUrls = "";

		for (HashMap.Entry<String, String> entry : siteMap.entrySet()) {
			hmKey = entry.getKey();
			hmVal = entry.getValue();

            switch(hmKey){ //파싱한 키값을 DB컬럼명으로 변환
			 	case "readKey" : returnMap.put("readKey",hmVal);break;
			 	case "detailUrl" : returnMap.put("detailUrl",hmVal);break;
			 	case "subject" : returnMap.put("SUBJECT",hmVal);break;
			 	case "SIDO" : returnMap.put("SIDO",hmVal);break;
				case "내용" : returnMap.put("CONTENT",hmVal);break;
				case "사업구분" : returnMap.put("PROJ_GUBUN",hmVal);break;
				case "사업명" : returnMap.put("PROJ_NAME",hmVal);break;
				case "사업단계" : returnMap.put("PROJ_STEP",hmVal);break;
				case "사업기간" : returnMap.put("PROJ_PERIOD",hmVal);break;
				case "신청자격" : returnMap.put("PROJ_TARGET",hmVal);break;
				case "사업개요" : returnMap.put("PROJ_SUMMARY",hmVal);break;
				case "지원금" : returnMap.put("SUPPORT_MONEY",hmVal);break;
				case "참여최소인원" : returnMap.put("PART_NUM",hmVal);break;
				case "작성자" : returnMap.put("REG_POST_NAME",hmVal);break;
				case "작성일" : returnMap.put("REG_POST_DATE",hmVal);break;
				case "구분" : returnMap.put("GUBUN",hmVal);break;
				case "접수구분" : returnMap.put("RECEIPT_GUBUN",hmVal);break;
				case "접수기간" : returnMap.put("RECEIPT_PERIOD",hmVal);break;
				case "접수방법" : returnMap.put("RECEIPT_METHOD",hmVal);break;
				case "결과발표" : returnMap.put("RESULT_DATE",hmVal);break;
				case "주관기관" : returnMap.put("ORGANIZATION",hmVal);break;
				case "담당부서" : returnMap.put("PROJ_DEPT",hmVal);break;
				case "담당자" : returnMap.put("PROJ_CHARGE",hmVal);break;
				case "담당자 연락처" : returnMap.put("PROJ_CONTACT",hmVal);break;
				case "첨부파일1" : fileMap.put("1", hmVal);break;
				case "첨부파일2" : fileMap.put("2", hmVal);break;
				case "첨부파일3" : fileMap.put("3", hmVal);break;
				case "첨부파일4" : fileMap.put("4", hmVal);break;
				case "첨부파일5" : fileMap.put("5", hmVal);break;
				case "첨부파일6" : fileMap.put("6", hmVal);break;
				case "첨부파일7" : fileMap.put("7", hmVal);break;
				case "첨부파일8" : fileMap.put("8", hmVal);break;
				case "첨부파일9" : fileMap.put("9", hmVal);break;
				case "첨부파일1URL" : fileUrlMap.put("1", hmVal);break;
				case "첨부파일2URL" : fileUrlMap.put("2", hmVal);break;
				case "첨부파일3URL" : fileUrlMap.put("3", hmVal);break;
				case "첨부파일4URL" : fileUrlMap.put("4", hmVal);break;
				case "첨부파일5URL" : fileUrlMap.put("5", hmVal);break;
				case "첨부파일6URL" : fileUrlMap.put("6", hmVal);break;
				case "첨부파일7URL" : fileUrlMap.put("7", hmVal);break;
				case "첨부파일8URL" : fileUrlMap.put("8", hmVal);break;
				case "첨부파일9URL" : fileUrlMap.put("9", hmVal);break;
            }
		}
		/* 파싱한 키값을 DB컬럼명으로 변환 후 로그 확인
		 * for(HashMap.Entry<String, String> test : returnMap.entrySet()) {
			if(test.getKey()!="CONTENT") {
				System.out.println("[key]:" + test.getKey() + ", [value]:" + test.getValue());
			}
        }*/
		if(fileMap.size()>0){
			for(int i=1; i<=fileMap.size(); i++){
				if(i != 1){
					attachFiles += "|";
					attachFileUrls += "|";
				}
				attachFiles += fileMap.get(String.valueOf(i));
				attachFileUrls += fileUrlMap.get(String.valueOf(i));
			}
		}
		returnMap.put("INFO_ATTACH_FILE", attachFiles);
		returnMap.put("INFO_ATTACH_FILE_URL", attachFileUrls);
		return returnMap;
	}

	private static void mergeData(DBAccessService service, HashMap<String, String> paramMap, String sqlId) throws Exception{
		service.mergeData(paramMap, sqlId);
	}

	private static void updateData(DBAccessService service, HashMap<String, String> paramMap, String sqlId) throws Exception{
		service.updateData(paramMap, sqlId);
	}
}