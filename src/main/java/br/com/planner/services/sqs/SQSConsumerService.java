package br.com.planner.services.sqs;

import br.com.planner.dto.email.Email;
import br.com.planner.services.email.EmailService;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class SQSConsumerService {

    private final AmazonSQSAsync sqs;

    private final EmailService emailService;

    @Value("${QUEUE_URL}")
    private String queueUrl;

    @SqsListener(value = "planner-email-queue")
    public void consumeMessagesFromQueue(Message message)  {
        System.out.println(message);

            String emailBody = message.body();
            String type = message.messageAttributes().get("type").stringValue();
            String tripId = message.messageAttributes().get("trip_id").stringValue();

            System.out.println(emailBody + " email");
            System.out.println(type + " type");

            Email email = new Email();
            email.setFrom("plannerspringtest@gmail.com");
            email.setTo(Collections.singletonList(emailBody));

            this.emailService.sendEmailFromConsumer(email, type, tripId);

            sqs.deleteMessage(queueUrl, message.receiptHandle());
    }
}
