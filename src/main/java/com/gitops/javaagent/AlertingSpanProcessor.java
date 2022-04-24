/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.gitops.javaagent;

import com.gitops.javaagent.model.Alert;
import com.gitops.javaagent.slack.SlackClient;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.api.config.Config;
import io.opentelemetry.sdk.trace.ReadWriteSpan;
import io.opentelemetry.sdk.trace.ReadableSpan;
import io.opentelemetry.sdk.trace.SpanProcessor;

import java.util.Objects;

/**
 * See <a
 * href="https://github.com/open-telemetry/opentelemetry-specification/blob/master/specification/trace/sdk.md#span-processor">
 * OpenTelemetry Specification</a> for more information about {@link SpanProcessor}.
 *
 * @see MyAutoConfigurationCustomizerProvider
 */
public class AlertingSpanProcessor implements SpanProcessor {
    private final static String ERROR_CODE = "error_code";

    @Override
    public void onStart(Context parentContext, ReadWriteSpan span) {
    }

    @Override
    public boolean isStartRequired() {
        return false;
    }

    @Override
    public void onEnd(ReadableSpan span) {
        String errorCode = span.getAttribute(AttributeKey.stringKey(ERROR_CODE));
        if (Objects.nonNull(errorCode)) {
            Config config = Config.get();
            String serviceName = config.getString("otel.service.name");
            Alert alert = new Alert(serviceName, span);
            SlackClient.send(alert);
        }
    }

    @Override
    public boolean isEndRequired() {
        return true;
    }
}
