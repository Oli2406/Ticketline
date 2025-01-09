package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FrontendForwardingEndpoint {

    @PermitAll
    @RequestMapping(value = "/**")
    public void forwardToFrontend(HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        var frontendUrl =
            "https://24ws-se-pr-inso-08-acf05sgmk6doonfn65ksq.apps.student.inso-w.at/";

        var queryString = request.getQueryString();

        if (queryString != null) {
            frontendUrl += "?" + queryString;
        }

        response.sendRedirect(frontendUrl);
    }
}
