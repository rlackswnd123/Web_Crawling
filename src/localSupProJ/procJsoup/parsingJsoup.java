package localSupProJ.procJsoup;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class parsingJsoup {

	static Logger log = LoggerFactory.getLogger(parsingJsoup.class);

	public static ArrayList<HashMap<String, String>> parsingList (int urlGubun) throws Exception{
		String[] urlStrings = {
							  "http://www.seoulmaeul.org/programs/user/support/list.asp?tabgbn=4", //서울시 마을공동체 종합지원센터 공모사업 게시판
							  "https://openapt.seoul.go.kr/boardForm/selectBoardList.do?returnUrl=/portal/dMenu/supportNews/supportList.open&query=poSupportNews.poSupportNewsSelect&bbsId=siGu:suSch:suStat", //서울시 공동주택통합정보마당 알림마당 > 지원소식 게시판
							  "https://www.daejeonmaeul.kr/app/contest/index?md_id=business_contest", //대전광역시 공동체공모사업 신청 및 접수 게시판
							  "http://ggmaeul.or.kr/base/board/list?boardManagementNo=8&menuLevel=3&menuNo=1",//경기도마을공동체지원센터 공지사항 > 공모사업 게시판
							  "http://www.gwmaeul.org/bbs/list?brd=notice&sca=지원사업", //강원도 마을공동체 종합지원센터 공지사항 > 지원사업 게시판
							  "http://www.gwmaeul.org/bbs/list?brd=notice&sca=공고", //강원도 마을공동체 종합지원센터 공지사항 > 공고 게시판
							  "http://www.urcb.or.kr/web/cityUniversity/list.do?mId=52" //부산광역시 도시재생지원센터 사업안내 > 공모사업신청
							  };

		ArrayList<HashMap<String, String>> resultList = new ArrayList<>();
		ArrayList<HashMap<String, String>> resultDetailList = new ArrayList<>();
		HashMap<String, String> resultMap;
		HashMap<String, String> resultDetailMap;
		Elements tableElements = null;

		/*
	     *	Document 클래스 : 연결해서 얻어온 HTML 전체 문서
	     *	Element 클래스  : Documnet의 HTML 요소
	     *	Elements 클래스 : Element가 모인 자료형
		 */
		Document doc = callJsoup.getConnectJsoup(urlStrings[urlGubun],"get"); // Jsoup.connect -> Document return

		/* 각 지자체 사이트 별로 문서 파싱(목록을 가져와서 각각의 상세 URL 을 수집한후 상세 URL로 접근하여 상세 내용을 파싱) */
		if(urlGubun==0){

			log.info(">> 서울시 마을공동체 종합지원센터-> 지원사업 -> 신청 및 접수 -> 전체사업");

			tableElements = doc.select("#programList tbody tr .entry_summary a"); // 게시물 리스트 파싱

			for (Element tbElement : tableElements) {

				resultMap = new HashMap<String, String>();

				resultMap.put("urlGubun", String.valueOf(urlGubun));
				resultMap.put("subject", tbElement.select("h4").text());
				resultMap.put("detailUrl", "http://www.seoulmaeul.org/programs/user/support/"+tbElement.attr("href"));
				resultMap.put("readKey", resultMap.get("detailUrl").substring(resultMap.get("detailUrl").indexOf("idx=")+4,resultMap.get("detailUrl").indexOf("&tabgbn")));

				log.info(resultMap.get("urlGubun") + "번 사이트 목록: "+ resultMap.get("subject") +" : "+ resultMap.get("detailUrl"));

				resultDetailMap = new HashMap<String, String>();

				doc = callJsoup.getConnectJsoup(resultMap.get("detailUrl"),"get"); // Jsoup.connect -> Document return

				tableElements = doc.select(".entry_content section"); 			   // 각각의 게시물 리스트의 상세 URL을 통한 상세 페이지 정보 파싱

				resultDetailMap.put("urlGubun", resultMap.get("urlGubun"));
				resultDetailMap.put("readKey", resultMap.get("readKey"));
				resultDetailMap.put("subject", resultMap.get("subject"));
				resultDetailMap.put("detailUrl", resultMap.get("detailUrl"));
				resultDetailMap.put("SIDO","서울특별시");
				resultDetailMap.put("제목", tableElements.select("header h3").html());
				resultDetailMap.put("작성자", tableElements.select("header .entry_meta .author strong").html());
				resultDetailMap.put("작성일", tableElements.select("header .entry_meta .date").html());

				for (Element tbDetailElement : tableElements.select(".tableInfo table tbody tr")) { // 첨부파일, 담당자, 연락처 관련 파싱

					/* th 값을 key, td 값을 value 로 처리 */
					if(tbDetailElement.select("th").eq(0).text().equals("첨부파일")){
						int i=1;
						for (Element fileElement : tbDetailElement.select("td strong a")) {
							resultDetailMap.put("첨부파일"+i, fileElement.text().substring(0,fileElement.text().indexOf(" [")));
							resultDetailMap.put("첨부파일"+i+"URL", "http://www.seoulmaeul.org"+fileElement.attr("href"));
							i++;
						}
					}else{
						resultDetailMap.put(tbDetailElement.select("th").eq(0).text(), tbDetailElement.select("td").eq(0).text());
					}

					/* 담당자, 연락처 분리 */
					if(!tbDetailElement.select("th").eq(1).text().equals("")){
						if(tbDetailElement.select("th").eq(1).text().equals("담당자")){
							resultDetailMap.put(tbDetailElement.select("th").eq(1).text(), tbDetailElement.select("td").eq(1).text().substring(0,tbDetailElement.select("td").eq(1).text().indexOf("0")));
							resultDetailMap.put("담당자 연락처", tbDetailElement.select("td").eq(1).text().substring(tbDetailElement.select("td").eq(1).text().indexOf("0")));
						}else{
							resultDetailMap.put(tbDetailElement.select("th").eq(1).text(), tbDetailElement.select("td").eq(1).text());
						}
					}
				}

				resultDetailMap.put("내용", tableElements.select("article").html());

				resultDetailList.add(resultDetailMap);

				Thread.sleep(1000);
			}
		}else if(urlGubun==1){

			log.info(">> 서울시 공동주택통합정보마당 알림마당 > 지원소식 게시판");

			// 목록 정보 가져오기
			tableElements = doc.select("form[name=subForm] table tbody tr .tdTitle a");

			String supportInfoSn = "";

			for (Element tbElement : tableElements) {
				resultMap = new HashMap<String, String>();
				supportInfoSn = tbElement.attr("onclick");
				supportInfoSn = supportInfoSn.substring(supportInfoSn.indexOf("'")+1,supportInfoSn.indexOf(")")-1);
				resultMap.put("urlGubun", String.valueOf(urlGubun));
				resultMap.put("subject", tbElement.text());
				resultMap.put("detailUrl", "https://openapt.seoul.go.kr/boardForm/boardDetail.do?returnUrl=/portal/dMenu/supportNews/supportDetail.open&query=poSupportNews.poSupportNewsDetail&supportInfoSn="+supportInfoSn);
				resultMap.put("readKey", supportInfoSn);
				resultList.add(resultMap);
			}

			String fileInfo = "";
			String[] fileInfos = null;
			String atchFileId = "";
			String fileSn = "";
			for(HashMap<String, String> siteMap : resultList){
				resultDetailMap = new HashMap<String, String>();
				log.info(siteMap.get("urlGubun") + "번 사이트 목록: "+ siteMap.get("subject") +" : "+ siteMap.get("detailUrl"));

				doc = callJsoup.getConnectJsoup(siteMap.get("detailUrl"),"post");
				tableElements = doc.select(".readBbsWrap");

				resultDetailMap.put("urlGubun", siteMap.get("urlGubun"));
				resultDetailMap.put("readKey", siteMap.get("readKey"));
				resultDetailMap.put("subject", siteMap.get("subject"));
				resultDetailMap.put("detailUrl", siteMap.get("detailUrl"));
				resultDetailMap.put("SIDO","서울특별시");
				resultDetailMap.put("제목", tableElements.select(".readTitle").html());
				resultDetailMap.put("작성자", "");
				resultDetailMap.put("작성일", "");

				for (Element tbElement : tableElements.select(".readSubTx dl")) {
					// dt 값을 key, dd 값을 value 로 처리
					if(tbElement.select("dt").text().equals("등록일")){
						resultDetailMap.put("작성일", tbElement.select("dd").text());
					}else if(tbElement.select("dt").text().equals("연락처")){
						resultDetailMap.put("담당자 연락처", tbElement.select("dd").text());
					}else if(tbElement.select("dt").text().equals("첨부파일")){

					}else{
						resultDetailMap.put(tbElement.select("dt").text(), tbElement.select("dd").text());
					}
				}

				int i=1;
				for (Element fileElement : tableElements.select("#egov_file_view_table tbody tr td a")) {
					resultDetailMap.put("첨부파일"+i, fileElement.text().substring(0,fileElement.text().lastIndexOf("[")));
					fileInfo = fileElement.attr("href");
					fileInfo = fileInfo.replace("javascript:fn_egov_downFile(", "");
					fileInfo = fileInfo.replace(")", "");
					fileInfo = fileInfo.replace("'", "");
					fileInfos = fileInfo.split(",");
					atchFileId = fileInfos[0];
					fileSn = fileInfos[1];
					resultDetailMap.put("첨부파일"+i+"URL", "https://openapt.seoul.go.kr/open/FileDown.do?atchFileId="+atchFileId+"&fileSn="+fileSn);

					i++;
				}

				resultDetailMap.put("내용", tableElements.select(".readContent").html());

				resultDetailList.add(resultDetailMap);
				Thread.sleep(1000);
			}

		}else if(urlGubun==2){

			log.info(">> 대전광역시 공동체공모사업 신청 및 접수 게시판");

			// 목록 정보 가져오기
			tableElements = doc.select(".business-contest-list > ul li a");

			for (Element tbElement : tableElements) {
				resultMap = new HashMap<String, String>();
				resultMap.put("urlGubun", "2");
				resultMap.put("subject", tbElement.select(".cont .title").text());
				resultMap.put("detailUrl", "https://www.daejeonmaeul.kr"+tbElement.attr("href"));
				resultMap.put("readKey", resultMap.get("detailUrl").substring(resultMap.get("detailUrl").indexOf("code=")+5,resultMap.get("detailUrl").indexOf("&page")));
				resultList.add(resultMap);
			}

			for(HashMap<String, String> siteMap : resultList){
				resultDetailMap = new HashMap<String, String>();
				log.info(siteMap.get("urlGubun") + "번 사이트 목록: "+ siteMap.get("subject") +" : "+ siteMap.get("detailUrl"));

				doc = callJsoup.getConnectJsoup(siteMap.get("detailUrl"),"get");
				tableElements = doc.select(".contest-view");

				resultDetailMap.put("urlGubun", siteMap.get("urlGubun"));
				resultDetailMap.put("readKey", siteMap.get("readKey"));
				resultDetailMap.put("detailUrl", siteMap.get("detailUrl"));
				resultDetailMap.put("SIDO","대전광역시");
				resultDetailMap.put("사업구분", tableElements.select(".view-header .busi-type span").text());
				resultDetailMap.put("subject", tableElements.select(".view-header dl dt h3").text());
				resultDetailMap.put("제목", tableElements.select(".view-header dl dt h3").text());
				resultDetailMap.put("작성자", tableElements.select(".view-header dl dd ul li").eq(0).text());
				resultDetailMap.put("작성일", tableElements.select(".view-header dl dd ul li").eq(1).text());

				for (Element tbElement : tableElements.select(".view-body .contest-info ul li")) {
					if(tbElement.html().indexOf(">") <= -1){
						resultDetailMap.put(tbElement.select("strong").text(), tbElement.text());
					}else{
						if(tbElement.select("strong").text().equals("발표일자")){
							resultDetailMap.put("결과발표", tbElement.html().substring(tbElement.html().lastIndexOf(">")+2));
						}else if(tbElement.select("strong").text().equals("사업대상")){
							resultDetailMap.put("신청자격", tbElement.html().substring(tbElement.html().lastIndexOf(">")+2));
						}else if(tbElement.select("strong").text().equals("참여 최소인원")){
							resultDetailMap.put("참여최소인원", tbElement.html().substring(tbElement.html().lastIndexOf(">")+2));
						}else if(tbElement.select("strong").text().equals("연락처")){
							resultDetailMap.put("담당자 연락처", tbElement.html().substring(tbElement.html().lastIndexOf(">")+2));
						}else if(tbElement.select("strong").text().equals("모집기간")){
							resultDetailMap.put("접수기간", tbElement.html().substring(tbElement.html().lastIndexOf(">")+2));
						}else if(tbElement.select("strong").text().equals("지원예산")){
							resultDetailMap.put("지원금", tbElement.html().substring(tbElement.html().lastIndexOf(">")+2));
						}else{
							resultDetailMap.put(tbElement.select("strong").text(), tbElement.html().substring(tbElement.html().lastIndexOf(">")+2));
						}
					}
				}

				int i=1;
				for (Element fileElement : tableElements.select(".view-body .contest-cont").eq(0).select(".document-list li a") ) {
					resultDetailMap.put("첨부파일"+i, fileElement.select("div").text());
					resultDetailMap.put("첨부파일"+i+"URL", "https://www.daejeonmaeul.kr"+fileElement.attr("href"));
					i++;
				}

				resultDetailMap.put("내용", tableElements.select(".view-body .contest-cont").eq(1).select(".cont #contents").html());

				resultDetailList.add(resultDetailMap);
				Thread.sleep(1000);
			}
		}else if(urlGubun==3){

			log.info(">> 경기도마을공동체지원센터 공지사항 > 공모사업 게시판");

			// 목록 정보 가져오기
			tableElements = doc.select(".board_list tbody tr");

			String nttId = "";
			for (Element tbElement : tableElements) {
				resultMap = new HashMap<String, String>();
				nttId = tbElement.select(".tit a").attr("href");
				nttId = nttId.substring(nttId.indexOf("boardNo=")+8,nttId.indexOf("&searchCategory"));
				resultMap.put("urlGubun", "3");
				resultMap.put("subject", tbElement.select(".tit a").text());
				resultMap.put("detailUrl", tbElement.select(".tit a").attr("href"));
				resultMap.put("readKey", nttId);
				resultMap.put("작성자", tbElement.select(".writer").text());
				resultList.add(resultMap);
			}

			for(HashMap<String, String> siteMap : resultList){
				resultDetailMap = new HashMap<String, String>();
				log.info(siteMap.get("urlGubun") + "번 사이트 목록: "+ siteMap.get("subject") +" : "+ siteMap.get("detailUrl"));

				doc = callJsoup.getConnectJsoup(siteMap.get("detailUrl"),"get");
				tableElements = doc.select(".board_view");

				resultDetailMap.put("urlGubun", siteMap.get("urlGubun"));
				resultDetailMap.put("readKey", siteMap.get("readKey"));
				resultDetailMap.put("subject", siteMap.get("subject"));
				resultDetailMap.put("detailUrl", siteMap.get("detailUrl"));
				resultDetailMap.put("작성자", siteMap.get("작성자"));
				resultDetailMap.put("SIDO","경기도");
				resultDetailMap.put("제목", tableElements.select(".board_view_top .tit").html().substring(tableElements.select(".board_view_top .tit").html().lastIndexOf(">")+1));
				resultDetailMap.put("작성일", tableElements.select(".board_view_top .info .each").eq(0).text().substring(tableElements.select(".board_view_top .info .each").eq(0).text().indexOf(":")+1));

				int i=1;
				for (Element fileElement : tableElements.select(".board_view_file .file_box .file_each a") ) {
					resultDetailMap.put("첨부파일"+i, fileElement.text());
					resultDetailMap.put("첨부파일"+i+"URL", fileElement.attr("href"));
					i++;
				}
				resultDetailMap.put("내용", tableElements.select(".board_view_con .editor_view").html().replaceAll("src=\"/upload", "src=\"http://ggmaeul.or.kr/upload") );
				resultDetailList.add(resultDetailMap);
				Thread.sleep(1000);
			}

		}else if(urlGubun==4){

			log.info(">> 강원도 마을공동체 종합지원센터 공지사항 > 지원사업 게시판");

			// 목록 정보 가져오기
			tableElements = doc.select(".table-style table tbody tr td a");

			for (Element tbElement : tableElements) {
				resultMap = new HashMap<String, String>();
				resultMap.put("urlGubun", "4");
				resultMap.put("subject", tbElement.text());
				resultMap.put("detailUrl", "http://www.gwmaeul.org"+tbElement.attr("href"));
				resultMap.put("readKey", resultMap.get("detailUrl").substring(resultMap.get("detailUrl").indexOf("poid=")+5,resultMap.get("detailUrl").length()));
				resultList.add(resultMap);
			}

			for(HashMap<String, String> siteMap : resultList){
				resultDetailMap = new HashMap<String, String>();
				log.info(siteMap.get("urlGubun") + "번 사이트 목록: "+ siteMap.get("subject") +" : "+ siteMap.get("detailUrl"));

				doc = callJsoup.getConnectJsoup(siteMap.get("detailUrl"),"get");
				tableElements = doc.select(".table-style");

				resultDetailMap.put("urlGubun", siteMap.get("urlGubun"));
				resultDetailMap.put("readKey", siteMap.get("readKey"));
				resultDetailMap.put("subject", siteMap.get("subject"));
				resultDetailMap.put("detailUrl", siteMap.get("detailUrl"));
				resultDetailMap.put("SIDO","강원도");
				resultDetailMap.put("제목", tableElements.select(".post_title").text());
				resultDetailMap.put("작성자", tableElements.select("table tbody tr").eq(1).select("td").eq(0).text());
				resultDetailMap.put("작성일", tableElements.select("table tbody tr").eq(1).select("td").eq(1).text());

				int i=1;
				for (Element fileElement : tableElements.select("table tbody tr").eq(2).select("td").eq(0).select("ul li a") ) {
					resultDetailMap.put("첨부파일"+i, fileElement.text().substring(0,fileElement.text().lastIndexOf("(")));
					resultDetailMap.put("첨부파일"+i+"URL", "http://www.gwmaeul.org"+fileElement.attr("href"));
					i++;
				}
				resultDetailMap.put("내용", tableElements.select("table tbody tr").eq(3).select("td .view_cont").html().replaceAll("src=\"/uploads", "src=\"http://www.gwmaeul.org/uploads") );

				resultDetailList.add(resultDetailMap);
				Thread.sleep(1000);
			}

		}else if(urlGubun==5){

			log.info(">> 강원도 마을공동체 종합지원센터 공지사항 > 공고 게시판");

			// 목록 정보 가져오기
			tableElements = doc.select(".table-style table tbody tr td a");

			for (Element tbElement : tableElements) {
				resultMap = new HashMap<String, String>();
				resultMap.put("urlGubun", "5");
				resultMap.put("subject", tbElement.text());
				resultMap.put("detailUrl", "http://www.gwmaeul.org"+tbElement.attr("href"));
				resultMap.put("readKey", resultMap.get("detailUrl").substring(resultMap.get("detailUrl").indexOf("poid=")+5,resultMap.get("detailUrl").length()));
				resultList.add(resultMap);
			}

			for(HashMap<String, String> siteMap : resultList){
				resultDetailMap = new HashMap<String, String>();
				log.info(siteMap.get("urlGubun") + "번 사이트 목록: "+ siteMap.get("subject") +" : "+ siteMap.get("detailUrl"));

				doc = callJsoup.getConnectJsoup(siteMap.get("detailUrl"),"get");
				tableElements = doc.select(".table-style");

				resultDetailMap.put("urlGubun", siteMap.get("urlGubun"));
				resultDetailMap.put("readKey", siteMap.get("readKey"));
				resultDetailMap.put("subject", siteMap.get("subject"));
				resultDetailMap.put("detailUrl", siteMap.get("detailUrl"));
				resultDetailMap.put("SIDO","강원도");
				resultDetailMap.put("제목", tableElements.select(".post_title").text());
				resultDetailMap.put("작성자", tableElements.select("table tbody tr").eq(1).select("td").eq(0).text());
				resultDetailMap.put("작성일", tableElements.select("table tbody tr").eq(1).select("td").eq(1).text());

				int i=1;
				for (Element fileElement : tableElements.select("table tbody tr").eq(2).select("td").eq(0).select("ul li a") ) {
					resultDetailMap.put("첨부파일"+i, fileElement.text().substring(0,fileElement.text().lastIndexOf("(")));
					resultDetailMap.put("첨부파일"+i+"URL", "http://www.gwmaeul.org"+fileElement.attr("href"));
					i++;
				}
				resultDetailMap.put("내용", tableElements.select("table tbody tr").eq(3).select("td .view_cont").html().replaceAll("src=\"/uploads", "src=\"http://www.gwmaeul.org/uploads") );

				resultDetailList.add(resultDetailMap);
				Thread.sleep(1000);
			}

		}else if(urlGubun==6){

			log.info(">> 부산광역시 도시재생지원센터 사업안내 > 공모사업신청");

			// 목록 정보 가져오기
			tableElements = doc.select(".listTypeA tbody tr .subject a");

			for (Element tbElement : tableElements) {
				resultMap = new HashMap<String, String>();
				resultMap.put("urlGubun", "6");
				resultMap.put("subject", tbElement.text());
				resultMap.put("detailUrl", "http://www.urcb.or.kr/web/cityUniversity/"+tbElement.attr("href"));
				resultMap.put("readKey", resultMap.get("detailUrl").substring(resultMap.get("detailUrl").indexOf("cuIdx=")+6,resultMap.get("detailUrl").length()));
				resultList.add(resultMap);
			}

			for(HashMap<String, String> siteMap : resultList){
				resultDetailMap = new HashMap<String, String>();
				log.info(siteMap.get("urlGubun") + "번 사이트 목록: "+ siteMap.get("subject") +" : "+ siteMap.get("detailUrl"));

				doc = callJsoup.getConnectJsoup(siteMap.get("detailUrl"),"get");
				tableElements = doc.select(".viewTypeA");

				resultDetailMap.put("urlGubun", siteMap.get("urlGubun"));
				resultDetailMap.put("readKey", siteMap.get("readKey"));
				resultDetailMap.put("subject", siteMap.get("subject"));
				resultDetailMap.put("detailUrl", siteMap.get("detailUrl"));
				resultDetailMap.put("SIDO","부산광역시");
				resultDetailMap.put("제목", tableElements.select("h2").text());

				for (Element tbElement : tableElements.select(".infor li")) {
					if(tbElement.select(".name").text().equals("신청서식")){
						int i=1;
						for (Element fileElement : tableElements.select(".fn_con_file li a") ) {
							resultDetailMap.put("첨부파일"+i, fileElement.text());
							resultDetailMap.put("첨부파일"+i+"URL", "http://www.urcb.or.kr/web/cityUniversity/"+fileElement.attr("href"));
							i++;
						}
					}else{
						if(tbElement.select(".name").text().equals("결과발표일")){
							resultDetailMap.put("결과발표", tbElement.html().substring(tbElement.html().lastIndexOf(">")+1));
						}else if(tbElement.select(".name").text().equals("전화번호")){
							resultDetailMap.put("담당자 연락처", tbElement.html().substring(tbElement.html().lastIndexOf(">")+1));
						}else if(tbElement.select(".name").text().equals("접수기간")){
							resultDetailMap.put("접수기간", tbElement.html().substring(tbElement.html().lastIndexOf(">")+1).replaceAll("&nbsp;", ""));
						}else{
							resultDetailMap.put(tbElement.select(".name").text(), tbElement.html().substring(tbElement.html().lastIndexOf(">")+1));
						}
					}
				}

				resultDetailMap.put("내용", tableElements.select(".con").html());

				resultDetailList.add(resultDetailMap);
				Thread.sleep(1000);
			}

		}
		return resultDetailList;
	}
}
