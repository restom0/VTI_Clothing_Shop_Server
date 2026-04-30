package vn.vti.clothing_shop.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SwaggerDocumentationIntegrationTest extends ControllerIntegrationTestSupport {
	@Autowired
	private MockMvc mockMvc;

	@Test
	void shouldExposeProfessionalOpenApiDefinition() throws Exception {
		mockMvc.perform(get("/v3/api-docs").accept(MediaType.APPLICATION_JSON))
		       .andExpect(status().isOk())
		       .andExpect(jsonPath("$.info.title").value("VTI Clothing Shop API"))
		       .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.type").value("http"))
		       .andExpect(jsonPath("$.tags[*].name", hasItem("Catalog")))
		       .andExpect(jsonPath("$.tags[*].name", hasItem("Commerce")))
		       .andExpect(jsonPath("$.paths['/oauth2/authorization/google'].get.summary").value("Start Google OAuth2 login"));
	}

	@Test
	void shouldExposeSwaggerUi() throws Exception {
		mockMvc.perform(get("/swagger-ui.html"))
		       .andExpect(status().is3xxRedirection());
	}
}
