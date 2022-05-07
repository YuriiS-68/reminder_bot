package pro.sky.telegrambot.servise;

import com.pengrad.telegrambot.model.Message;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;

@Service
public interface NotificationTaskService {

    NotificationTask getMappingNotice(Message message);
}
