package ninja.skyrocketing;

import ninja.skyrocketing.shadowrocket.build.BuildRule;
import ninja.skyrocketing.shadowrocket.resultant.AdList;
import ninja.skyrocketing.shadowrocket.resultant.GFWList;

/**
 * @Author skyrocketing Hong
 * @Date 2020-08-03 003 11:52:54
 * @Version 1.0
 */
public class RunApplication {
	private static final String[] adRulesUrl = {
			"https://filters.adtidy.org/extension/chromium/filters/11.txt",
			"https://filters.adtidy.org/extension/chromium/filters/224.txt",
			"https://raw.githubusercontent.com/AdAway/adaway.github.io/master/hosts.txt",
			"https://pgl.yoyo.org/adservers/serverlist.php?hostformat=adblockplus&showintro=1&mimetype=plaintext",
			"https://someonewhocares.org/hosts/hosts",
			"https://raw.githubusercontent.com/easylist/easylist/master/easyprivacy/easyprivacy_trackingservers.txt",
			"https://danny0838.github.io/content-farm-terminator/files/blocklist/content-farms.txt",
			"https://www.malwaredomainlist.com/hostslist/hosts.txt",
			"https://gitee.com/xinggsf/Adblock-Rule/raw/master/rule.txt",
			"https://gitee.com/xinggsf/Adblock-Rule/raw/master/mv.txt",
			"https://raw.githubusercontent.com/user1121114685/koolproxyR_rule_list/master/kpr_our_rule.txt",
			"https://raw.githubusercontent.com/jdlingyu/ad-wars/master/hosts"
	};
	private static final String gfwRulesList = "https://raw.githubusercontent.com/gfwlist/gfwlist/master/gfwlist.txt";
	
	public static void main(String[] args) throws InterruptedException {
		System.out.println("Getting GFW List ...");
		GFWList.FormatRules(GFWList.GetRules(gfwRulesList));
		System.out.println("Getting Ad Block List ...");
		AdList.GetAdList(adRulesUrl);
		System.out.println("Generating configuration ...");
		BuildRule.Build();
	}
}
