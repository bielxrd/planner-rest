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
import java.util.List;

@Service
@RequiredArgsConstructor
public class SQSConsumerService {

    private final AmazonSQSAsync sqs;

    private final EmailService emailService;

    private final AmazonSQSAsync amazonSQSAsync;

    @Value("${QUEUE_URL}")
    private String queueUrl;

    @SqsListener(value = "planner-email-queue")
    public void consumeMessagesFromQueue(Message message)  {
        System.out.println(message);

            String emailBody = message.body();
            String type = message.messageAttributes().get("type").stringValue();

            System.out.println(emailBody + " email");
            System.out.println(type + " type");

            Email email = new Email();
            email.setFrom("plannerspringtest@gmail.com");
            email.setTo(Collections.singletonList(emailBody));

            switch (type) {
                case "activity_queue":
                    email.setSubject("Nova atividade cadastrada.");
                    email.setBody("Uma nova atividade foi cadastrada na sua viagem");
                    this.emailService.sendActivityCreatedNotificationEmail(email);
                    break;
                case "link_queue":
                    email.setSubject("Novo link cadastrado");
                    email.setBody("Novo link foi cadastrado na sua viagem");
                    break;
            }

            sqs.deleteMessage(queueUrl, message.receiptHandle());
    }
}
