package ninja.skyrocketing.shadowrocket.build;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;

/**
 * @Author skyrocketing Hong
 * @Date 2020-08-03 003 14:02:14
 * @Version 1.0
 */
public class BuildRule {
	//生成GFW规则
	public static void GFWRule() {
		FileReader gfwDomainReader = new FileReader("conf/resultant/domain/gfw_domain.list");
		FileReader gfwIPReader = new FileReader("conf/resultant/ip/gfw_ip.list");
		FileReader gfwDirectReader = new FileReader("conf/resultant/direct/gfw_direct.list");
		
		FileWriter gfwWriter = new FileWriter("conf/conf_result/gfw_rule.conf");
		
		gfwWriter.write("# Convert from GFW List" + "\n");
		for (String line : gfwDomainReader.readLines()) {
			if (line.startsWith("#")) {
				continue;
			}
			if(line.contains(".")){
				gfwWriter.append("DOMAIN-SUFFIX," + line + ",PROXY,force-remote-dns" + "\n");
			} else {
				gfwWriter.append("DOMAIN-KEYWORD," + line + ",PROXY,force-remote-dns" + "\n");
			}
		}
		for (String line : gfwDirectReader.readLines()) {
			if (line.startsWith("#")) {
				continue;
			}
			if(line.contains(".")){
				gfwWriter.append("DOMAIN-SUFFIX," + line + ",DIRECT" + "\n");
			} else {
				gfwWriter.append("DOMAIN-KEYWORD," + line + ",DIRECT" + "\n");
			}
		}
		for (String line : gfwIPReader.readLines()) {
			if (line.startsWith("#")) {
				continue;
			}
			gfwWriter.append("IP-CIDR," + line + "/32,PROXY" + "\n");
		}
	}
	
	//生成广告屏蔽规则
	public static void AdBlockRule() {
		FileReader adDomainReader = new FileReader("conf/resultant/domain/ad_domain.list");
		FileReader adIPReader = new FileReader("conf/resultant/ip/ad_ip.list");
		
		FileWriter adRuleWriter = new FileWriter("conf/conf_result/ad_rule.conf");
		FileWriter adHostWriter = new FileWriter("conf/conf_result/ad_host.conf");
		
		adHostWriter.write("# Convert from Ad Block Domains" + "\n");
		adRuleWriter.write("# Convert from Ad Block IP List" + "\n");
		for (String line : adDomainReader.readLines()) {
			if (line.startsWith("#")) {
				continue;
			}
			if(line.contains(".")){
				adHostWriter.append(line + " = 127.0.0.1" + "\n");
			} else {
				adRuleWriter.append("DOMAIN-KEYWORD," + line + ",REJECT" + "\n");
			}
		}
		
		for (String line : adIPReader.readLines()) {
			if (line.startsWith("#")) {
				continue;
			}
			adRuleWriter.append("IP-CIDR," + line + "/32,REJECT" + "\n");
		}
	}
	
	//生成完整规则
	public static void Build() {
		GFWRule();
		AdBlockRule();
		
		String[] buildConfigs = {
				"0.head",
				"1.rule",
				"2.host",
				"3.url_rewrite"
		};
		String[] confRuleResults = {
				"gfw_rule",
				"ad_rule"
		};
		String[] confHostResults = {
				"ad_host"
		};
		
		FileWriter confWriter = new FileWriter("conf/Shadowrocket.rules.conf");
		confWriter.write("# Build Time: " + DateUtil.date().toString() + "\n");
		
		for (String buildConfig : buildConfigs) {
			FileReader buildReader = new FileReader("conf/build/" + buildConfig + ".conf");
			confWriter.append(buildReader.readString() + "\n");
			if (buildConfig.contains("rule")) {
				for (String confRuleResult : confRuleResults){
					FileReader confRuleReader = new FileReader("conf/conf_result/" + confRuleResult + ".conf");
					confWriter.append(confRuleReader.readString() + "\n");
				}
			}
			if (buildConfig.contains("host")) {
				for (String confHostResult : confHostResults){
					FileReader confHostReader = new FileReader("conf/conf_result/" + confHostResult + ".conf");
					confWriter.append(confHostReader.readString() + "\n");
				}
			}
		}
		
		confWriter.append("# skyrocketing Hong https://skyrocketing.ninja");
	}
}
