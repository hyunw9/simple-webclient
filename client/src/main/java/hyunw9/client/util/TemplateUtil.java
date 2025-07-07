package hyunw9.client.util;

import java.util.Map;

public final class TemplateUtil {
	private TemplateUtil() {
	}

	public static String resolve(String template, Map<String, Object> vars) {
		String r = template;
		for (var e : vars.entrySet()) {
			r = r.replace('{' + e.getKey() + '}', String.valueOf(e.getValue()));
		}
		return r;
	}
}
