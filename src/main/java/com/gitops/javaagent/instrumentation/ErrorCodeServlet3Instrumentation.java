/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.gitops.javaagent.instrumentation;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

import static net.bytebuddy.matcher.ElementMatchers.namedOneOf;

public class ErrorCodeServlet3Instrumentation implements TypeInstrumentation {
    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return AgentElementMatchers.hasSuperType(
                namedOneOf("javax.servlet.http.HttpServlet"));
    }

    @Override
    public void transform(TypeTransformer typeTransformer) {
        typeTransformer.applyAdviceToMethod(
                namedOneOf("service")
                        .and(
                                ElementMatchers.takesArgument(
                                        0, ElementMatchers.named("javax.servlet.ServletRequest")))
                        .and(
                                ElementMatchers.takesArgument(
                                        1, ElementMatchers.named("javax.servlet.ServletResponse")))
                        .and(ElementMatchers.isPublic()),
                this.getClass().getName() + "$ErrorCodeServlet3Advice");
    }

    @SuppressWarnings("unused")
    public static class ErrorCodeServlet3Advice {

        @Advice.OnMethodExit(suppress = Throwable.class)
        public static void onExit(@Advice.Argument(value = 1) ServletResponse response) {
            if (!(response instanceof HttpServletResponse)) {
                return;
            }
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            if (httpServletResponse.containsHeader("error_code")) {
                String errorCode = httpServletResponse.getHeader("error_code");
                Span current = Span.current();
                if (Objects.nonNull(current)) {
                    current.setAttribute("error_code", errorCode);
                }
            }
        }
    }
}
