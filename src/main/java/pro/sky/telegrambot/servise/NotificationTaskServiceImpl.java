package pro.sky.telegrambot.servise;

import com.pengrad.telegrambot.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
@Service
public class NotificationTaskServiceImpl implements NotificationTaskService{

    private final Logger logger = LoggerFactory.getLogger(NotificationTaskServiceImpl.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");



    public NotificationTask getMappingNotice(Message message) {
        logger.info("Method getMappingNotice was started: {}", message);
        if (message != null){
            NotificationTask notice = new NotificationTask();
            notice.setIdChat(message.chat().id());
            notice.setNotice(message.text().substring(getIndexForSeparate(message)).trim());
            LocalDateTime dateTime = LocalDateTime.parse(message.text().substring(0, getIndexForSeparate(message)).trim(),
                    DATE_TIME_FORMATTER);
            notice.setTime(dateTime);
            notice.setMessageId(message.messageId());
            LocalDateTime currentTime = LocalDateTime.now();
            notice.setSentDate(currentTime);
            return notice;
        }
        throw new NullPointerException("Message is not exist");
    }

    private int getIndexForSeparate(Message message){
        logger.info("Method getIndexForSeparate was started: {}", message);
        int index = 0;
        for (int i = message.text().toCharArray().length - 1; i > 0 ; i--) {
            if (Character.isLetter(message.text().toCharArray()[i])){
                index = i;
            }
        }
        return index;
    }
}
