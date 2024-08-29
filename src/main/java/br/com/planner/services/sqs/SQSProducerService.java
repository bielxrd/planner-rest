package br.com.planner.services.sqs;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SQSProducerService {

    private final AmazonSQSAsync sqs;

    @Value("${QUEUE_URL}")
    private String queueUrl;

    public SendMessageResult sendEmailToQueue(String email, String type, String tripId) {
        Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
        MessageAttributeValue messageAttributeValue = new MessageAttributeValue()
                .withStringValue(type)
                .withDataType("String");

        MessageAttributeValue messageAttributeTripId = new MessageAttributeValue()
                .withStringValue(tripId)
                .withDataType("String");

        messageAttributes.put("type", messageAttributeValue);
        messageAttributes.put("trip_id", messageAttributeTripId);

        SendMessageRequest messageRequest = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(email)
                .withMessageAttributes(messageAttributes);

        SendMessageResult messageResult = sqs.sendMessage(messageRequest);

        if (messageResult.getMessageId() != null) {
            return messageResult;
        }
        return null;
    }
}
