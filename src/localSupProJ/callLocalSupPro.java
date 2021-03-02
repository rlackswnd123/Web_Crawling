package localSupProJ;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import localSupProJ.Quartz.quartzJob;

/*
 * slf4j  : common_log, log4j 등등의 로깅 framework를 통합해서 사용할 수 있는 로깅 framework
 * log4j  : java 기반의 로깅 처리 라이브러리
 * Quartz : 표준 자바 프로그램으로 만들어진 작업을 지정된 일정에 따라 실행시키는데 사용하는 Java 패키지(Java용 작업 스케줄러)
 */
// Source-tree test
public class callLocalSupPro{

	static Logger log = LoggerFactory.getLogger(callLocalSupPro.class);

	public static void main(String[] args){

		log.info("웹 크롤링 스케줄러 시작");

		SchedulerFactory schedulerFactory = new StdSchedulerFactory(); //스케줄러를 사용하려면 먼저 SchedulerFactory를 사용해서 스케줄러 인스턴스 생성

		try {
			Scheduler scheduler = schedulerFactory.getScheduler(); 	   //스케줄러를 가져옴

			JobDetail job = newJob(quartzJob.class).withIdentity("CommunityCrawlingJob", Scheduler.DEFAULT_GROUP).build();

			/*
			 * Quartz는 몇가지 트리거를 제공
			 *  1.SimpleTrigger는 특정한 시점에 한번만 실행되거나 특정한 시점 이후에 N번 반복 실행되는 경우에 적합
			 *  2.CronTrigger는 캘린더와 유사한 방법으로 실행되는 경우에 적합. 예를 들어 매주 금요일 정오나 매월 10일 10시 15분과 같은 일정을 추가 가능.
			 *    "초, 분, 시, 일, 월, 요일, 년(생략가능)"
			 *    "*"로 와일드카드를 지정. 월에 "*"를 지정하면 "모든 월"을 의미, 만약 요일에 "*"를 지정하면 "모든 요일"
			 *    "/"는 값의 증가를 지정하는데 사용. 예를 들어 분에 "0/15"를 지정하면 "매 시간 0분에서 시작해서 15분마다"라는 의미 "3,23,43"으로 지정한 것과 같은 효과
			 *    "?"는 일과 요일에 지정할 수 있으며 "값을 지정하지 않음"을 의미. 둘 중 하나는 값을 지정하면서 다른 하나는 값을 지정하지 않을 때 사용할 수 있습니다.
			 */

			Trigger trigger = newTrigger().withIdentity("CrawlingTrigger", Scheduler.DEFAULT_GROUP).withSchedule(cronSchedule("0 37 23 * * ?")).build();

			scheduler.scheduleJob(job, trigger); //가져온 scheduler에 실행시킬 job을 trigger에 설정한 일정에 따라 실행하도록 추가

			scheduler.start();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}