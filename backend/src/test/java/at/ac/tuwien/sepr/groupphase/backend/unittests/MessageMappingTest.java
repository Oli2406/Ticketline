package at.ac.tuwien.sepr.groupphase.backend.unittests;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import at.ac.tuwien.sepr.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailedMessageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleMessageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.MessageMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Message;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class MessageMappingTest implements TestData {

    private final Message message =
        Message.MessageBuilder.aMessage()
            .withId(ID)
            .withTitle(TEST_NEWS_TITLE)
            .withSummary(TEST_NEWS_SUMMARY)
            .withText(TEST_NEWS_TEXT)
            .withPublishedAt(TEST_NEWS_PUBLISHED_AT)
            .build();
    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void givenNothing_whenMapDetailedMessageDtoToEntity_thenEntityHasAllProperties() {
        DetailedMessageDto detailedMessageDto = messageMapper.messageToDetailedMessageDto(message);
        assertAll(
            () -> assertEquals(ID, detailedMessageDto.getId()),
            () -> assertEquals(TEST_NEWS_TITLE, detailedMessageDto.getTitle()),
            () -> assertEquals(TEST_NEWS_SUMMARY, detailedMessageDto.getSummary()),
            () -> assertEquals(TEST_NEWS_TEXT, detailedMessageDto.getText()),
            () -> assertEquals(TEST_NEWS_PUBLISHED_AT, detailedMessageDto.getPublishedAt()));
    }

    @Test
    public void
    givenNothing_whenMapListWithTwoMessageEntitiesToSimpleDto_thenGetListWithSizeTwoAndAllProperties() {
        List<Message> messages = new ArrayList<>();
        messages.add(message);
        messages.add(message);

        List<SimpleMessageDto> simpleMessageDtos = messageMapper.messageToSimpleMessageDto(
            messages);
        assertEquals(2, simpleMessageDtos.size());
        SimpleMessageDto simpleMessageDto = simpleMessageDtos.get(0);
        assertAll(
            () -> assertEquals(ID, simpleMessageDto.getId()),
            () -> assertEquals(TEST_NEWS_TITLE, simpleMessageDto.getTitle()),
            () -> assertEquals(TEST_NEWS_SUMMARY, simpleMessageDto.getSummary()),
            () -> assertEquals(TEST_NEWS_PUBLISHED_AT, simpleMessageDto.getPublishedAt()));
    }
}
