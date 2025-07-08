package hyunw9.client.util;

import java.util.Map;

public final class TemplateUtil {
	private TemplateUtil() {
	}

	public static String resolve(String template, Map<String, Object> vars) {
		String resolve = template;
		for (var entry : vars.entrySet()) {
			resolve = resolve.replace('{' + entry.getKey() + '}', String.valueOf(entry.getValue()));
		}
		return resolve;
	}
}
