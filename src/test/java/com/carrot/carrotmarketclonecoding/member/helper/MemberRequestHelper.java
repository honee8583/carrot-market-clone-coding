package com.carrot.carrotmarketclonecoding.member.helper;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;

import com.carrot.carrotmarketclonecoding.util.ControllerRequestUtil;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

public class MemberRequestHelper extends ControllerRequestUtil {

    private static final String MEMBER_REQUEST_URL = "/profile";

    private final MockMvc mvc;

    public MemberRequestHelper(MockMvc mvc) {
        this.mvc = mvc;
    }

    public ResultActions requestUpdateProfile(MockMultipartFile profileImage, MockMultipartFile updateRequest) throws Exception {
        return mvc.perform(requestWithCsrfAndSetContentType(
                        multipart(MEMBER_REQUEST_URL)
                                .file(profileImage)
                                .file(updateRequest)
                                .with(request -> {
                                    request.setMethod("PATCH");
                                    return request;
                                }),
                        MediaType.MULTIPART_FORM_DATA
                )
        );
    }

    public ResultActions requestGetProfileDetail() throws Exception {
        return mvc.perform(get(MEMBER_REQUEST_URL));
    }
}
