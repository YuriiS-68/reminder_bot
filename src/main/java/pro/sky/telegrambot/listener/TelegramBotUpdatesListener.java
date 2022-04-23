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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
            if (update.message() != null && update.message().text().equals("/start")){
                long chatId = message.chat().id();
                String text = "Hello! I`m glad to see you!";
                sendMessage(chatId, text);
            } else {
                noticeRepository.save(getMappingNotice(message));
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    @Scheduled(cron = "${interval-in-cron}")
    public void findAndSendNoticeByTime(){
        LocalDateTime time = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        NotificationTask notice = noticeRepository.getNotificationTaskByTimeEquals(time);
        if (notice != null){
            sendMessage(notice.getIdChat(), notice.getNotice());
            noticeRepository.deleteById(notice.getId());
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

    private void sendMessage(long chatId, String text){
        logger.info("Method sendMessage has been run: {}, {}", chatId, text);
        SendMessage request = new SendMessage(chatId, text)
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .replyToMessageId(1)
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
