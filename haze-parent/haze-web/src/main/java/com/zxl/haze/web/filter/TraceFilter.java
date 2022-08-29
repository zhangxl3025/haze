package com.zxl.haze.web.filter;


import com.zxl.haze.core.context.AppContext;
import com.zxl.haze.core.trace.TraceContext;
import com.zxl.haze.web.util.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String traceJson = RequestUtil.get(request, TraceContext.TRACE_JSON);
            if (traceJson == null) {
                String referer = RequestUtil.get(request, "referer");
                if (StringUtils.hasText(referer)) {
                    TraceContext.traceSpan(referer);
                    traceJson = TraceContext.getTraceJson();
                }
            }
            TraceContext.traceSpan(traceJson, request.getServletPath());
            AppContext.HEADER_MAP.set(RequestUtil.getHeaderMap(request));
            filterChain.doFilter(request, response);
        } finally {
            try {
                AppContext.HEADER_MAP.remove();
            }finally {
                TraceContext.traceEnd();
            }
        }
    }

}
