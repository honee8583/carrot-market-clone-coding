package com.carrot.carrotmarketclonecoding.notification.helper;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carrot.carrotmarketclonecoding.util.ControllerRequestUtil;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

public class NotificationRequestHelper extends ControllerRequestUtil {

    private static final String CONNECT_URL = "/connect";
    private static final String NOTIFICATION_URL = "/notification";

    private final MockMvc mvc;

    public NotificationRequestHelper(MockMvc mvc) {
        this.mvc = mvc;
    }

    public ResultActions requestConnect() throws Exception {
        return mvc.perform(get(CONNECT_URL)
                    .header("lastEventId", "12345"))
                .andExpect(status().isOk());
    }

    public ResultActions requestGetAllNotifications() throws Exception {
        return mvc.perform(get(NOTIFICATION_URL));
    }

    public ResultActions requestRead() throws Exception {
        return mvc.perform(requestWithCsrf(patch(NOTIFICATION_URL + "/{id}", 1L)));
    }
}
