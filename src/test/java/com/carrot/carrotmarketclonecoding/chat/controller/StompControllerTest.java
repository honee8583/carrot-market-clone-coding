package com.carrot.carrotmarketclonecoding.chat.controller;

import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.CHAT_ROOM_NOT_FOUND;
import static com.carrot.carrotmarketclonecoding.common.response.FailedMessage.NOT_MEMBER_OF_CHAT_ROOM;
import static org.assertj.core.api.Assertions.assertThat;

import com.carrot.carrotmarketclonecoding.MongoTestContainerTest;
import com.carrot.carrotmarketclonecoding.auth.dto.JwtVO;
import com.carrot.carrotmarketclonecoding.auth.util.JwtUtil;
import com.carrot.carrotmarketclonecoding.chat.domain.ChatRoom;
import com.carrot.carrotmarketclonecoding.chat.dto.ChatMessageRequestDto;
import com.carrot.carrotmarketclonecoding.chat.helper.StompFrameHandlerImpl;
import com.carrot.carrotmarketclonecoding.chat.helper.StompSessionHandler;
import com.carrot.carrotmarketclonecoding.chat.repository.ChatRoomRepository;
import com.carrot.carrotmarketclonecoding.common.response.ResponseResult;
import com.carrot.carrotmarketclonecoding.member.domain.Member;
import com.carrot.carrotmarketclonecoding.member.domain.enums.Role;
import com.carrot.carrotmarketclonecoding.member.repository.MemberRepository;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.AbstractMessageConverter;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class StompControllerTest extends MongoTestContainerTest {

    private String wsUrl;
    private BlockingQueue<ChatMessageRequestDto> messages;
    private BlockingQueue<ResponseResult> errors;
    private ChatMessageRequestDto chatRequest;
    private String token;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private MemberRepository memberRepository;

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        wsUrl = "ws://localhost:" + port + "/ws-connect";
        setUpData();

        messages = new LinkedBlockingQueue<>();
        errors = new LinkedBlockingQueue<>();
        chatRequest = ChatMessageRequestDto.builder()
                .senderId(1111L)
                .receiverId(2222L)
                .message("Hello World!")
                .roomNum("1111")
                .build();
        token = jwtUtil.createAccessToken(1111L, Role.USER);
    }

    private void setUpData() {
        Member sender = Member.builder()
                .authId(1111L)
                .build();
        Member receiver = Member.builder()
                .authId(2222L)
                .build();
        memberRepository.save(sender);
        memberRepository.save(receiver);

        ChatRoom chatRoom = ChatRoom.builder()
                .roomNum("1234")
                .sender(sender)
                .receiver(receiver)
                .build();
        chatRoomRepository.save(chatRoom);
    }

    @AfterEach
    public void tearDown() {
        chatRoomRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("웹소켓 연결 및 메시지 전송 테스트")
    void websocketChatTest() throws Exception {
        // given
        WebSocketStompClient stompClient = createClient(new MappingJackson2MessageConverter());
        StompSession session = connect(stompClient);

        StompHeaders subscribeHeaders = createHeader("/topic/chat.1234", token);
        session.subscribe(subscribeHeaders, new StompFrameHandlerImpl(new ChatMessageRequestDto(), messages) {});

        // when
        StompHeaders stompHeaders = createHeader("/publish/chat.1234", token);
        session.send(stompHeaders, chatRequest);

        // then
        ChatMessageRequestDto receivedMessage = messages.poll(1, TimeUnit.SECONDS);
        assertThat(receivedMessage).isNotNull();
        assertThat(receivedMessage.getMessage()).isEqualTo(chatRequest.getMessage());
    }

    @Test
    @DisplayName("JWT 토큰 없이 구독 시 예외 발생 테스트")
    void websocketSubscribeWithoutJwtTokenTest() throws Exception {
        // given
        WebSocketStompClient stompClient = createClient(new StringMessageConverter());
        StompSession session = connect(stompClient);

        // when
        session.subscribe("/topic/chat.1234", new StompFrameHandlerImpl<>(new ResponseResult(), errors) {});

        // then
        ResponseResult message = errors.poll(1, TimeUnit.SECONDS);
        assertThat(message).isNotNull();
        assertThat(message.getStatus()).isEqualTo(401);
        assertThat(message.getResult()).isFalse();
        assertThat(message.getMessage()).isEqualTo("토큰이 존재하지 않습니다!");
    }

    @Test
    @DisplayName("JWT 토큰이 유효하지 않을 경우 예외 발생 테스트")
    void websocketSubscribeWithJwtTokenInvalidTest() throws Exception {
        // given
        WebSocketStompClient stompClient = createClient(new StringMessageConverter());
        StompSession session = connect(stompClient);

        // when
        StompHeaders subscribeHeaders = createHeader("/topic/chat.1234", "Invalid Token");
        session.subscribe(subscribeHeaders, new StompFrameHandlerImpl<>(new ResponseResult(), errors) {});

        // then
        ResponseResult message = errors.poll(1, TimeUnit.SECONDS);
        assertThat(message).isNotNull();
        assertThat(message.getStatus()).isEqualTo(401);
        assertThat(message.getResult()).isFalse();
        assertThat(message.getMessage()).isEqualTo("잘못된 토큰입니다!");
    }

    @Test
    @DisplayName("존재하지 않는 채팅방으로 메시지를 전송할경우 예외 발생")
    void chatRoomNotExistsTest() throws Exception {
        // given
        WebSocketStompClient stompClient = createClient(new MappingJackson2MessageConverter());
        StompSession session = connect(stompClient);

        StompHeaders subscribeHeaders = createHeader("/topic/chat.1111", token);
        session.subscribe(subscribeHeaders, new StompFrameHandlerImpl<>(new ChatMessageRequestDto(), messages) {});

        StompHeaders errorHeaders = createHeader("/user/queue/errors", token);
        session.subscribe(errorHeaders, new StompFrameHandlerImpl<>(new ResponseResult(), errors));

        // when
        StompHeaders stompHeaders = createHeader("/publish/chat.1111", token);
        session.send(stompHeaders, chatRequest);

        // then
        ResponseResult message = errors.poll(1, TimeUnit.SECONDS);
        assertThat(message).isNotNull();
        assertThat(message.getStatus()).isEqualTo(400);
        assertThat(message.getResult()).isFalse();
        assertThat(message.getMessage()).isEqualTo(CHAT_ROOM_NOT_FOUND.getMessage());
    }


    @Test
    @DisplayName("채팅방에 포함되지않은 사용자에게 메시지를 전송할경우 예외 발생")
    void receiverNotInChatRoomTest() throws Exception {
        // given
        WebSocketStompClient stompClient = createClient(new MappingJackson2MessageConverter());
        StompSession session = connect(stompClient);

        StompHeaders subscribeHeaders = createHeader("/topic/chat.1111", token);
        session.subscribe(subscribeHeaders, new StompFrameHandlerImpl<>(new ChatMessageRequestDto(), messages) {});

        StompHeaders errorHeaders = createHeader("/user/queue/errors", token);
        session.subscribe(errorHeaders, new StompFrameHandlerImpl<>(new ResponseResult(), errors));

        // when
        chatRequest.setReceiverId(3333L);
        StompHeaders stompHeaders = createHeader("/publish/chat.1234", token);
        session.send(stompHeaders, chatRequest);

        // then
        ResponseResult message = errors.poll(1, TimeUnit.SECONDS);
        assertThat(message).isNotNull();
        assertThat(message.getStatus()).isEqualTo(403);
        assertThat(message.getResult()).isFalse();
        assertThat(message.getMessage()).isEqualTo(NOT_MEMBER_OF_CHAT_ROOM.getMessage());
    }

    private WebSocketStompClient createClient(AbstractMessageConverter converter) {
        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(converter);
        return stompClient;
    }

    private StompSession connect(WebSocketStompClient stompClient) throws Exception {
        return stompClient.connectAsync(wsUrl, new StompSessionHandler(errors))
                .get(1, TimeUnit.SECONDS);
    }

    private StompHeaders createHeader(String destination, String token) {
        StompHeaders subscribeHeaders = new StompHeaders();
        subscribeHeaders.setDestination(destination);
        subscribeHeaders.add(JwtVO.HEADER, token);
        return subscribeHeaders;
    }
}