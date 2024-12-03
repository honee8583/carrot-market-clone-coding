package com.carrot.carrotmarketclonecoding;

import com.carrot.carrotmarketclonecoding.auth.config.WithCustomMockUser;
import com.carrot.carrotmarketclonecoding.auth.controller.KakaoLoginController;
import com.carrot.carrotmarketclonecoding.auth.service.LoginService;
import com.carrot.carrotmarketclonecoding.board.controller.BoardController;
import com.carrot.carrotmarketclonecoding.board.controller.BoardLikeController;
import com.carrot.carrotmarketclonecoding.board.controller.SearchKeywordController;
import com.carrot.carrotmarketclonecoding.board.helper.board.BoardDtoFactory;
import com.carrot.carrotmarketclonecoding.board.helper.boardlike.BoardLikeDtoFactory;
import com.carrot.carrotmarketclonecoding.board.service.SearchKeywordService;
import com.carrot.carrotmarketclonecoding.board.service.impl.BoardLikeServiceImpl;
import com.carrot.carrotmarketclonecoding.board.service.impl.BoardServiceImpl;
import com.carrot.carrotmarketclonecoding.category.controller.CategoryController;
import com.carrot.carrotmarketclonecoding.category.service.impl.CategoryServiceImpl;
import com.carrot.carrotmarketclonecoding.chat.controller.ChatController;
import com.carrot.carrotmarketclonecoding.chat.helper.ChatDtoFactory;
import com.carrot.carrotmarketclonecoding.chat.service.impl.ChatMessageServiceImpl;
import com.carrot.carrotmarketclonecoding.chat.service.impl.ChatRoomServiceImpl;
import com.carrot.carrotmarketclonecoding.keyword.controller.KeywordController;
import com.carrot.carrotmarketclonecoding.keyword.helper.KeywordDtoFactory;
import com.carrot.carrotmarketclonecoding.keyword.service.impl.KeywordServiceImpl;
import com.carrot.carrotmarketclonecoding.member.controller.MemberController;
import com.carrot.carrotmarketclonecoding.member.helper.MemberDtoFactory;
import com.carrot.carrotmarketclonecoding.member.service.impl.MemberServiceImpl;
import com.carrot.carrotmarketclonecoding.notification.controller.NotificationController;
import com.carrot.carrotmarketclonecoding.notification.helper.NotificationDtoFactory;
import com.carrot.carrotmarketclonecoding.notification.service.SseEmitterService;
import com.carrot.carrotmarketclonecoding.notification.service.impl.NotificationServiceImpl;
import com.carrot.carrotmarketclonecoding.util.RestDocsTestUtil;
import com.carrot.carrotmarketclonecoding.word.controller.WordController;
import com.carrot.carrotmarketclonecoding.word.service.impl.WordServiceImpl;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(value = {
        BoardDtoFactory.class,
        BoardLikeDtoFactory.class,
        KeywordDtoFactory.class,
        MemberDtoFactory.class,
        NotificationDtoFactory.class,
        ChatDtoFactory.class
})
@ActiveProfiles("test")
@WithCustomMockUser
@WebMvcTest(controllers = {
        BoardController.class,
        BoardLikeController.class,
        SearchKeywordController.class,
        CategoryController.class,
        KeywordController.class,
        MemberController.class,
        NotificationController.class,
        WordController.class,
        ChatController.class,
        KakaoLoginController.class
})
public abstract class ControllerTest extends RestDocsTestUtil {

    @MockBean
    protected BoardLikeServiceImpl boardLikeService;

    @MockBean
    protected BoardServiceImpl boardService;

    @MockBean
    protected SearchKeywordService searchKeywordService;

    @MockBean
    protected CategoryServiceImpl categoryService;

    @MockBean
    protected KeywordServiceImpl keywordService;

    @MockBean
    protected MemberServiceImpl memberService;

    @MockBean
    protected SseEmitterService sseEmitterService;

    @MockBean
    protected NotificationServiceImpl notificationService;

    @MockBean
    protected WordServiceImpl wordService;

    @MockBean
    protected ChatRoomServiceImpl chatRoomService;

    @MockBean
    protected ChatMessageServiceImpl chatMessageService;

    @MockBean
    protected LoginService loginService;

}
