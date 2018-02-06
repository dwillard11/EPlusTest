package ca.manitoulin.mtd.util;

import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JsonConvertHelper {
	private static final Logger log = Logger.getLogger(JsonConvertHelper.class);

	public static String toJSONString(List list)
	{
		if (list == null) {
			return "null";
		}
		boolean first = true;
		StringBuffer sb = new StringBuffer();
		Iterator iter = list.iterator();

		sb.append('[');
		while (iter.hasNext()) {
			if (first)
				first = false;
			else {
				sb.append(',');
			}
			Object value = iter.next();
			if (value == null) {
				sb.append("null");
				continue;
			}
			sb.append(toJSONString(value));
		}
		sb.append(']');
		return sb.toString();
	}


	public static String toJSONString(Object value)
	{
		if (value == null) {
			return "null";
		}
		if ((value instanceof String)) {
			return "\"" + escape((String)value) + "\"";
		}
		if ((value instanceof Double)) {
			if ((((Double)value).isInfinite()) || (((Double)value).isNaN())) {
				return "null";
			}
			return value.toString();
		}

		if ((value instanceof Float)) {
			if ((((Float)value).isInfinite()) || (((Float)value).isNaN())) {
				return "null";
			}
			return value.toString();
		}

		if ((value instanceof Number)) {
			return value.toString();
		}
		if ((value instanceof Boolean)) {
			return value.toString();
		}

		if ((value instanceof Map)) {
			return toJSONString((Map)value);
		}
       /* if ((value instanceof List)) {
            return toJSONString((List)value);
        }*/
		return value.toString();
	}

	public static  String toJSONString(Map map)
	{
		if (map == null) {
			return "null";
		}
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		Iterator iter = map.entrySet().iterator();

		sb.append('{');
		while (iter.hasNext()) {
			if (first)
				first = false;
			else {
				sb.append(',');
			}
			Map.Entry entry = (Map.Entry)iter.next();
			toJSONString(String.valueOf(entry.getKey()), entry.getValue(), sb);
		}
		sb.append('}');
		return sb.toString();
	}

	public static String toJSONString(String key, Object value, StringBuffer sb) {
		sb.append('"');
		if (key == null)
			sb.append("null");
		else
			escape(key, sb);
		sb.append('"').append(':');

		sb.append(toJSONString(value));

		return sb.toString();
	}


	public static String escape(String s)
	{
		if (s == null)
			return null;
		StringBuffer sb = new StringBuffer();
		escape(s, sb);
		return sb.toString();
	}

	public static void escape(String s, StringBuffer sb)
	{
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			switch (ch) {
				case '"':
					sb.append("\\\"");
					break;
				case '\\':
					sb.append("\\\\");
					break;
				case '\b':
					sb.append("\\b");
					break;
				case '\f':
					sb.append("\\f");
					break;
				case '\n':
					sb.append("\\n");
					break;
				case '\r':
					sb.append("\\r");
					break;
				case '\t':
					sb.append("\\t");
					break;
				case '/':
					sb.append("\\/");
					break;
				default:
					if (((ch >= 0) && (ch <= '\037')) ) {
						String ss = Integer.toHexString(ch);
						sb.append("\\u");
						for (int k = 0; k < 4 - ss.length(); k++) {
							sb.append('0');
						}
						sb.append(ss.toUpperCase());
					}
					else {
						sb.append(ch);
					}
			}
		}
	}
}
