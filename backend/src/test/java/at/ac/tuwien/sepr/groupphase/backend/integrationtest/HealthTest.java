package at.ac.tuwien.sepr.groupphase.backend.integrationtest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class HealthTest {

  @Autowired
  private MockMvc mockMvc;

  /*
  It is very important that /health returns HTTP 200 for the k8s deployment. Otherwise, the deployment will be considered unhealthy
   and be terminated and restarted. This would waste resources of the shared k8s cluster which is not tolerated.

   In case you want to secure your endpoints (e.g. via spring web-security), define an exception for /health. It must be publicly accessible
   without any form of authentication in our infrastructure.
   */
  @Test
  public void getHealthReturns200() throws Exception {
    MvcResult mvcResult = this.mockMvc.perform(get("/health"))
        .andReturn();

    assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
  }
}
