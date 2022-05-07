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
import pro.sky.telegrambot.servise.NotificationTaskServiceImpl;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.IllegalFormatException;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {
    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private static final String START_CMD = "/start";
    private static final String GREETINGS_USER = "Hello! I`m glad to see you!\nYou can send a message in the " +
            "following format: dd.MM.yyyy HH:mm text";
    private static final String INVALID_MESSAGE = "Your message format is not supported";
    private final TelegramBot telegramBot;
    private final NoticeRepository noticeRepository;
    private final NotificationTaskServiceImpl notificationTaskService;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, NoticeRepository noticeRepository,
                                      NotificationTaskServiceImpl notificationTaskService) {
        this.telegramBot = telegramBot;
        this.noticeRepository = noticeRepository;
        this.notificationTaskService = notificationTaskService;
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
            if (update.message() != null && update.message().text().equals(START_CMD)){
                sendMessage(chatId, GREETINGS_USER, messageId);
            } else {
                try{
                    noticeRepository.save(notificationTaskService.getMappingNotice(message));
                }catch (IllegalFormatException | DateTimeParseException e){
                    sendMessage(chatId, INVALID_MESSAGE, messageId);
                    logger.error("Saving to DB failed:", e);
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    @Scheduled(cron = "${interval-daily-cron}")
    public void deleteOutdatedNotices(){
        logger.info("Method deleteOutdatedNotices was started");
        List<NotificationTask> outdatedNotices = noticeRepository.getOutdatedNotificationTask();
        noticeRepository.deleteAll(outdatedNotices);
    }

    @Scheduled(cron = "${interval-in-cron}")
    public void findAndSendNoticeByTime(){
        LocalDateTime time = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        List<NotificationTask> notices = noticeRepository.getNotificationTaskByTimeEquals(time);

        for (NotificationTask notice : notices){
            sendMessage(notice.getIdChat(), notice.getNotice(), notice.getMessageId());
            notice.markAsSent();
        }
        noticeRepository.saveAll(notices);
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
