package localSupProJ.procJsoup;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class callJsoup {

	private final static String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36";

	// SSL 우회 등록
	public static void setSSL() throws NoSuchAlgorithmException, KeyManagementException {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}
			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}
		} };

		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new SecureRandom());

		HttpsURLConnection.setDefaultHostnameVerifier(
			new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			}
		);
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	}

	// jsoup 접속 후 document return
	public static Document getConnectJsoup(String urlString, String method) throws Exception{

		Document doc = null;

		try {
			if(urlString != null && !urlString.equals("")){
				// SSL 체크
				if(urlString.indexOf("https://") >= 0){
					/* ssl 우회처리를 하여 https 사이트의 문서를 가져오도록 준비 */
					setSSL();
				}

				// HTML 가져오기
				Connection conn = Jsoup.connect(urlString).header("Content-Type", "application/json;charset=UTF-8").userAgent(USER_AGENT)
				.method(Connection.Method.GET).ignoreContentType(true);

				/* post 방식으로만 문서를 보내주는 사이트를 위해 */
				if(method == null || method.equals("") || method.equals("get")){
					doc = conn.get();
				}else{
					doc = conn.post();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return doc;
	}
}
