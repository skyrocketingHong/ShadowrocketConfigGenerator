package ninja.skyrocketing.shadowrocket.resultant;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;

import java.util.TreeSet;

/**
 * @Author skyrocketing Hong
 * @Date 2020-08-03 003 11:55:25
 * @Version 1.0
 */
public class AdList {
	public static void GetAdList(String[] urls) throws InterruptedException {
		TreeSet<String> domains = new TreeSet<>();
		TreeSet<String> ips = new TreeSet<>();
		StringBuilder tmpRules = new StringBuilder();
		
		//下载规则
		for (String url : urls) {
			System.out.println("Loading \"" + url + "\".");
			boolean success = false;
			int tryTimes = 0;
			HttpResponse httpResponse;
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
				System.out.println("Error in getting \"" + url + "\".");
				return;
			}
			tmpRules.append(result).append("\n");
		}
		
		//处理规则
		String[] tmpRuleList = tmpRules.toString().split("\\n");
		for (String row : tmpRuleList) {
			row = row.trim();
			
			//跳过
			if (row.startsWith("@@") || row.equals("") || row.startsWith("!") || row.contains("$") || row.contains("##")) {
				continue;
			}
			
			//清除前缀
			row = row.replaceAll("^\\|?https?://", "");
			row = row.replaceAll("^\\|\\|", "");
			row = row.replaceAll("^\\.|/.*", "");
			row = row.replaceAll("^(127\\.0\\.0\\.1)\\s", "");
			
			//清除后缀
			row = row.replaceAll("/$|:\\d{2,5}$", "");
			row = row.replaceAll(":\\d{2,5}$", "");
			
			//不能含有的字符
			row = row.replaceAll("[/^:*]", "");
			
			//只匹配域名
			if (row.matches("^([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,9}$")) {
				domains.add(row);
			}
			//只匹配IP
			if (row.matches("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$")) {
				ips.add(row);
			}
		}
		//完成后输出规则个数
		System.out.println("Done, " + (domains.size() + ips.size()) + " rules.");
		
		//写入文件
		FileWriter domainsWriter = new FileWriter("conf/resultant/domain/ad_domain.list");
		domainsWriter.write("# Ad Block(Domain) List Build Time: " + DateUtil.date().toString() + "\n");
		for (String rule : domains) {
			domainsWriter.append(rule + "\n");
		}
		FileWriter ipsWriter = new FileWriter("conf/resultant/ip/ad_ip.list");
		ipsWriter.write("# Ad Block(IP) List Build Time: " + DateUtil.date().toString() + "\n");
		for (String rule : ips) {
			ipsWriter.append(rule + "\n");
		}
	}
}
