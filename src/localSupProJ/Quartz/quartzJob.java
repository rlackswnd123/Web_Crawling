package localSupProJ.Quartz;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import localSupProJ.dao.dataJsoup;
import localSupProJ.procJsoup.parsingJsoup;

/*
 * callLocalSupPro.java에서 Job에 지정된 트리거가 작동하면 스케줄러의 워커 쓰레드에 의해 execute()메소드가 호출
 * 메소드에 매개변수로 전달되는 JobExecutionContext객체가 런타임 환경에 대한 정보를 제공
 * parsingJsoup.parsingList(0)); //서울시 마을공동체 종합지원센터 공모사업 게시판
 * parsingJsoup.parsingList(1)); //서울시 공동주택통합정보마당 알림마당 > 지원소식 게시판
 * parsingJsoup.parsingList(2)); //대전광역시 공동체공모사업 신청 및 접수 게시판
 * parsingJsoup.parsingList(3)); //경기도마을공동체지원센터 공지사항 > 공모사업 게시판
 * parsingJsoup.parsingList(4)); //강원도 마을공동체 종합지원센터 공지사항 > 지원사업 게시판
 * parsingJsoup.parsingList(5)); //강원도 마을공동체 종합지원센터 공지사항 > 공고 게시판
 * parsingJsoup.parsingList(6)); //부산광역시 도시재생지원센터 사업안내 > 공모사업신청
 */

public class quartzJob implements Job{

	static Logger log = LoggerFactory.getLogger(quartzJob.class);

	public void execute(JobExecutionContext context) throws JobExecutionException {

		log.info("웹 크롤링 시작시각 : " + new Date());

		ArrayList<ArrayList<HashMap<String, String>>> resultDetailList = new ArrayList<>();

		try {
			/* jsop를 통해 웹사이트 문서 정보를 파싱하여 DB처리를 진행한다. */
			for(int i=0; i<7; i++)
				resultDetailList.add(parsingJsoup.parsingList(i));

			dataJsoup.procDataJsoup(resultDetailList);

		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("웹 크롤링 종료시각 : " + new Date());
	}
}
