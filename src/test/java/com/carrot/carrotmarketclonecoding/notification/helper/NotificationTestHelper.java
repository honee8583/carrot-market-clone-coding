package com.carrot.carrotmarketclonecoding.notification.helper;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.carrot.carrotmarketclonecoding.util.ControllerTestUtil;
import com.carrot.carrotmarketclonecoding.util.ResultFields;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;

public class NotificationTestHelper extends ControllerTestUtil {

    private final NotificationRequestHelper requestHelper;
    private final NotificationRestDocsHelper restDocsHelper;

    public NotificationTestHelper(MockMvc mvc, RestDocumentationResultHandler restDocs) {
        this.requestHelper = new NotificationRequestHelper(mvc);
        this.restDocsHelper = new NotificationRestDocsHelper(restDocs);
    }

    public void assertConnectSuccess() throws Exception {
        requestHelper.requestConnect()
                .andExpect(status().isOk())
                .andDo(restDocsHelper.createConnectSuccessDocument());
    }

    public void assertGetAllNotificationsSuccess(ResultFields resultFields) throws Exception {
        assertResponseResult(requestHelper.requestGetAllNotifications(), resultFields)
                .andExpect(jsonPath("$.data.size()", equalTo(2)))
                .andDo(restDocsHelper.createGetAllNotificationSuccessDocument());
    }

    public void assertGetAllNotificationsFail(ResultFields resultFields) throws Exception {
        assertResponseResult(requestHelper.requestGetAllNotifications(), resultFields)
                .andExpect(jsonPath("$.data", equalTo(null)))
                .andDo(restDocsHelper.createResponseResultDocument());
    }

    public void assertReadNotification(ResultFields resultFields) throws Exception {
        assertResponseResult(requestHelper.requestRead(), resultFields)
                .andExpect(jsonPath("$.data", equalTo(null)))
                .andDo(restDocsHelper.createReadNotificationDocument());
    }
}
