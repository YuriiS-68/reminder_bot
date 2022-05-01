package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ForceReply;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.dao.NoticeRepository;
import pro.sky.telegrambot.model.NotificationTask;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.IllegalFormatException;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final TelegramBot telegramBot;
    private final NoticeRepository noticeRepository;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, NoticeRepository noticeRepository) {
        this.telegramBot = telegramBot;
        this.noticeRepository = noticeRepository;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);

            Message message = update.message();
            long chatId = message.chat().id();
            int messageId = message.messageId();
            if (update.message() != null && update.message().text().equals("/start")){
                String text = "Hello! I`m glad to see you!";
                sendMessage(chatId, text, messageId);
            } else {
                try{
                    noticeRepository.save(getMappingNotice(message));
                }catch (IllegalFormatException | DateTimeParseException e){
                    String text = "Your message format is not supported";
                    sendMessage(chatId, text, messageId);
                    logger.error("Saving to DB failed:", e);
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    @Scheduled(cron = "${interval-in-cron}")
    public void findAndSendNoticeByTime(){
        LocalDateTime time = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        List<NotificationTask> notices = noticeRepository.getNotificationTaskByTimeEquals(time);

        for (NotificationTask notice : notices){
            sendMessage(notice.getIdChat(), notice.getNotice(), notice.getMessageId());
        }
    }

    private NotificationTask getMappingNotice(Message message){
        if (message != null){
            NotificationTask notice = new NotificationTask();
            notice.setIdChat(message.chat().id());
            notice.setNotice(message.text().substring(getIndexForSeparate(message)).trim());
            LocalDateTime dateTime = LocalDateTime.parse(message.text().substring(0, getIndexForSeparate(message)).trim(),
                    DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            notice.setTime(dateTime);
            System.out.println("message.messageId() = " + message.messageId());
            notice.setMessageId(message.messageId());
            return notice;
        }
        throw new NullPointerException("Message is not exist");
    }

    private int getIndexForSeparate(Message message){
        int index = 0;
        for (int i = message.text().toCharArray().length - 1; i > 0 ; i--) {
            if (Character.isLetter(message.text().toCharArray()[i])){
                index = i;
            }
        }
        return index;
    }

    private void sendMessage(long chatId, String text, int messageId){
        logger.info("Method sendMessage has been run: {}, {}, {}", chatId, text, messageId);
        SendMessage request = new SendMessage(chatId, text)
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .replyToMessageId(messageId)
                .replyMarkup(new ForceReply());

        SendResponse sendResponse = telegramBot.execute(request);
        if (!sendResponse.isOk()){
            int codeError = sendResponse.errorCode();
            String description = sendResponse.description();
            logger.info("code of error: {}", codeError);
            logger.info("description -: {}", description);
        }
    }
}
