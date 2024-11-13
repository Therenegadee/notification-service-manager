package com.github.therenegade.notification.manager.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextPlaceholderReplacingUtil {

    public static String replaceAllPlaceholdersInText(String templatedText, Map<String, Object> params) {
        StringBuilder message = new StringBuilder(templatedText);
        List<Object> valueList = new ArrayList<>();

        Pattern pattern = Pattern.compile("\\$\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(templatedText);

        while (matcher.find()) {
            String key = matcher.group(1);

            String paramName = "${" + key + "}";
            int index = message.indexOf(paramName);
            if (index != -1) {
                message.replace(index, index + paramName.length(), "%s");
                valueList.add(params.get(key));
            }
        }
        return String.format(message.toString(), valueList.toArray());
    }

}
