package at.ac.tuwien.sepr.groupphase.backend.config;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * ServletFilter to log every request.
 */
public class LogFilter extends OncePerRequestFilter {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final DecimalFormat REQUEST_RUNTIME_FORMAT = new DecimalFormat("#.###", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    private static final Long NANOSECONDS_PER_MS = 1000_000L;
    private static final List<String> MUTED_PATHS = Arrays.asList(
        "/swagger-ui/",
        "/swagger.yaml"
    );


    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        var runtime = -1L;
        var shouldLog = shouldLog(request);
        if (shouldLog) {
            populateMdc(request);
            beforeRequest(request);
        }
        try {
            //keep timestamp
            runtime = System.nanoTime();
            //do the work
            filterChain.doFilter(request, response);
        } catch (ServletException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            //runtime = end - start
            runtime = System.nanoTime() - runtime;
            if (shouldLog) {
                afterRequest(request, response, runtime);
            }
            MDC.clear();
        }
    }

    private void beforeRequest(HttpServletRequest request) {
        var b = getUrlString(">>> ", request);
        var agent = request.getHeader("User-Agent");
        if (agent != null) {
            b.append(" UA=").append(agent);
        }
        logWithRightCategory(200, b.toString());
    }

    private void afterRequest(HttpServletRequest request, HttpServletResponse response, Long runtime) {
        var b = getUrlString("<<< ", request);
        var logStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        if (response != null) {
            logStatus = response.getStatus();
            MDC.put("status", "" + logStatus);
            b.append(" status=").append(logStatus);
        } else {
            b.append(" NO RESPONSE");
        }
        var time = REQUEST_RUNTIME_FORMAT.format(runtime / NANOSECONDS_PER_MS);
        MDC.put("duration", time);
        b.append(" time=").append(time).append("ms");
        logWithRightCategory(logStatus, b.toString());
    }

    private void populateMdc(HttpServletRequest request) {
        var forwarded = request.getHeader("X-Forwarded-For");
        //ip of client
        MDC.put("ip", forwarded != null ? forwarded : request.getRemoteAddr());
        //correlation-id if none is set
        if (MDC.get("r") == null) {
            MDC.put("r", generateRequestId());
        }
        MDC.put("http_request_method", request.getMethod());
        MDC.put("http_request_url", request.getRequestURI());
        MDC.put("http_request_query", request.getQueryString());
        MDC.put("http_request_ua", request.getHeader("User-Agent"));
    }

    private String generateRequestId() {
        var uuid = UUID.randomUUID().toString();
        uuid = uuid.substring(uuid.lastIndexOf("-") + 1);
        return uuid;
    }

    private StringBuilder getUrlString(String prefix, HttpServletRequest request) {
        var b = new StringBuilder(prefix)
            .append(request.getMethod())
            .append(" ")
            .append(request.getRequestURI());
        var qs = request.getQueryString();
        if (qs != null) {
            b.append("?").append(qs);
        }
        return b;
    }

    private boolean shouldLog(HttpServletRequest request) {
        //Log everything in TRACE
        if (LOG.isTraceEnabled()) {
            return true;
        }

        //is the url muted?
        var url = request.getRequestURI();
        return MUTED_PATHS.stream().noneMatch(url::startsWith);
    }

    private void logWithRightCategory(int status, String logMsg) {
        var x = status / 100;
        switch (x) {
            case 2, 3 -> LOG.info(logMsg);
            case 1, 4 -> LOG.warn(logMsg);
            default -> LOG.error(logMsg);
        }
    }

}
