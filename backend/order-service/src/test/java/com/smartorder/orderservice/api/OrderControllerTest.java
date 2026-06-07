package com.smartorder.orderservice.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.smartorder.orderservice.application.service.OrderCommandService;
import com.smartorder.orderservice.application.service.OrderQueryService;
import com.smartorder.orderservice.infrastructure.security.SecurityConfig;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrderController.class)
@Import(SecurityConfig.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockitoBean
    private OrderCommandService commandService;
    @MockitoBean
    private OrderQueryService queryService;
    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void post_withoutToken_isUnauthorized() throws Exception {
        mvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"items\":[{\"productId\":\"" + UUID.randomUUID() + "\",\"quantity\":1}]}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void post_withJwt_createsOrder() throws Exception {
        UUID id = UUID.randomUUID();
        when(commandService.create(any())).thenReturn(id);

        mvc.perform(post("/orders").with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"items\":[{\"productId\":\"" + UUID.randomUUID() + "\",\"quantity\":2}]}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/orders/" + id));
    }

    @Test
    void post_withJwt_emptyItems_isBadRequest() throws Exception {
        mvc.perform(post("/orders").with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"items\":[]}"))
                .andExpect(status().isBadRequest());
    }
}
