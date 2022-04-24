package com.gitops.javaagent.slack;

import com.gitops.javaagent.model.Alert;

import java.time.format.DateTimeFormatter;

public class SlackTemplate {
    private static String template = "{\n" +
            "  \"blocks\": [\n" +
            "    {\n" +
            "      \"type\": \"section\",\n" +
            "      \"text\": {\n" +
            "        \"type\": \"mrkdwn\",\n" +
            "        \"text\": \"Oops, we got a new error, please fix it ASAP. You can find the log *<%s| over here> !*\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\": \"section\",\n" +
            "      \"fields\": [\n" +
            "        {\n" +
            "          \"type\": \"mrkdwn\",\n" +
            "          \"text\": \"*App:*\\n %s\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"type\": \"mrkdwn\",\n" +
            "          \"text\": \"*Action:*\\n %s\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"type\": \"mrkdwn\",\n" +
            "          \"text\": \"*Error code:*\\n %s\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"type\": \"mrkdwn\",\n" +
            "          \"text\": \"*Time:*\\n %s\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"type\": \"mrkdwn\",\n" +
            "          \"text\": \"*Error message:*\\n %s\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"type\": \"mrkdwn\",\n" +
            "          \"text\": \"*Trace Id:*\\n %s\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"type\": \"mrkdwn\",\n" +
            "          \"text\": \"*Span Id:*\\n %s\"\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\": \"divider\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    static String linkTpl = "http://192.168.137.2:30100/explore?left={\\\"datasource\\\":\\\"Loki\\\",\\\"queries\\\":[{\\\"refId\\\":\\\"A\\\",\\\"expr\\\":\\\"{app=\\\\\\\"%s\\\\\\\", trace_id=\\\\\\\"%s\\\\\\\"}\\\"}],\\\"range\\\":{\\\"from\\\":\\\"now-1h\\\",\\\"to\\\":\\\"now\\\"}}";


    public static String content(Alert alert) {
        String link = String.format(linkTpl, alert.app, alert.traceId);
        String content = String.format(template, link, alert.app, alert.action, alert.errorCode, alert.createdTime.format(DateTimeFormatter.ISO_DATE), alert.errorMessage, alert.traceId, alert.spanId);
//        System.out.println(content);
        return content;
    }
}
