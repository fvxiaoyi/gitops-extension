/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.gitops.javaagent.instrumentation;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.util.Objects;

import static net.bytebuddy.matcher.ElementMatchers.namedOneOf;

public class DispatcherServletInstrumentation implements TypeInstrumentation {
    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return AgentElementMatchers.hasSuperType(
                namedOneOf("org.springframework.web.servlet.DispatcherServlet"));
    }

    @Override
    public void transform(TypeTransformer typeTransformer) {
        typeTransformer.applyAdviceToMethod(
                namedOneOf("processDispatchResult")
                        .and(
                                ElementMatchers.takesArgument(
                                        0, ElementMatchers.named("javax.servlet.http.HttpServletRequest")))
                        .and(
                                ElementMatchers.takesArgument(
                                        1, ElementMatchers.named("javax.servlet.http.HttpServletResponse")))
                        .and(
                                ElementMatchers.takesArgument(
                                        2, ElementMatchers.named("org.springframework.web.servlet.HandlerExecutionChain")))
                        .and(
                                ElementMatchers.takesArgument(
                                        3, ElementMatchers.named("org.springframework.web.servlet.ModelAndView")))
                        .and(
                                ElementMatchers.takesArgument(
                                        4, ElementMatchers.named("java.lang.Exception")))
                        .and(ElementMatchers.isPrivate()),
                this.getClass().getName() + "$ProcessDispatchResultAdvice");
    }

    @SuppressWarnings("unused")
    public static class ProcessDispatchResultAdvice {

        @Advice.OnMethodExit(suppress = Throwable.class)
        public static void onExit(@Advice.Argument(value = 4) Exception exception) {
            if (Objects.nonNull(exception)) {
                Span current = Span.current();
                if (Objects.nonNull(current)) {
                    current.setStatus(StatusCode.ERROR, exception.getMessage());
                    current.setAttribute("error_message", exception.getMessage());
                }
            }
        }
    }
}
