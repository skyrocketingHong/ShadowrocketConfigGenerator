package ninja.skyrocketing.shadowrocket.resultant;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;

import java.util.TreeSet;

/**
 * @Author skyrocketing Hong
 * @Date 2020-08-03 003 10:40:15
 * @Version 1.0
 */
public class GFWList {
	//获取解码后的规则
	public static String GetRules(String url) throws InterruptedException {
		boolean success = false;
		int tryTimes = 0;
		HttpResponse httpResponse = null;
		String result = "";
		while (tryTimes < 5 && !success) {
			httpResponse = HttpRequest.get(url).execute();
			if (httpResponse.isOk()) {
				success = true;
				result = httpResponse.body();
				break;
			} else {
				Thread.sleep(10);
				tryTimes += 1;
			}
		}
		if (!success) {
			System.out.println("Error");
			return null;
		}
		return Base64.decodeStr(result).replaceAll("\\n", "\n");
	}
	
	//将规则转换为纯关键字
	public static void FormatRules(String rules) {
		String[] ruleList = rules.split("\\n");
		TreeSet<String> domains = new TreeSet<>();
		TreeSet<String> ips = new TreeSet<>();
		TreeSet<String> direct = new TreeSet<>();
		for (String row : ruleList) {
			row = row.trim();
			
			//跳过
			if (row.equals("") || row.startsWith("!") || row.matches("\\[AutoProxy .*\\]|^/.*/$")) {
				continue;
			} else if (row.startsWith("@@") || row.matches("^/.*/$")) {
				row = row.replaceAll("^@@\\|{1,2}(http(s)?\\://)?", "").replaceAll("/.*", "");
				direct.add(row);
				continue;
			}
			
			//清除前缀
			row = row.replaceAll("^\\|?https?://|^\\|\\||^\\.|/.*", "");
			
			//清除后缀
			row = row.replaceAll("/$", "");
			//只匹配域名
			if (row.matches("^([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,9}$")) {
				domains.add(row);
			}
			//只匹配IP
			if (row.matches("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$")) {
				ips.add(row);
			}
		}
		//写入文件
		FileWriter domainsWriter = new FileWriter("conf/resultant/domain/gfw_domain.list");
		domainsWriter.write("# GFW List Build Time: " + DateUtil.date().toString() + "\n");
		for (String rule : domains) {
			domainsWriter.append(rule + "\n");
		}
		FileWriter ipsWriter = new FileWriter("conf/resultant/ip/gfw_ip.list");
		ipsWriter.write("# GFW List Build Time: " + DateUtil.date().toString() + "\n");
		for (String rule : ips) {
			ipsWriter.append(rule + "\n");
		}
		FileWriter directWriter = new FileWriter("conf/resultant/direct/gfw_direct.list");
		directWriter.write("# GFW List Build Time: " + DateUtil.date().toString() + "\n");
		for (String rule : direct) {
			directWriter.append(rule + "\n");
		}
	}
}
