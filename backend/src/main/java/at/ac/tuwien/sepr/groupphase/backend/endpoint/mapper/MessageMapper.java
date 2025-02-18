package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.DetailedMessageDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.MessageInquiryDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.SimpleMessageDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Message;
import java.util.List;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper
public interface MessageMapper {

    @Named("simpleMessage")
    SimpleMessageDto messageToSimpleMessageDto(Message message);

    /**
     * This is necessary since the SimpleMessageDto misses the text property and the collection
     * mapper can't handle missing fields.
     */
    @IterableMapping(qualifiedByName = "simpleMessage")
    List<SimpleMessageDto> messageToSimpleMessageDto(List<Message> message);

    DetailedMessageDto messageToDetailedMessageDto(Message message);

    Message detailedMessageDtoToMessage(DetailedMessageDto detailedMessageDto);

    Message messageInquiryDtoToMessage(MessageInquiryDto messageInquiryDto);

    MessageInquiryDto messageToMessageInquiryDto(Message message);
}
