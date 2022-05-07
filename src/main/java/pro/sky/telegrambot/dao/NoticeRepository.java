package pro.sky.telegrambot.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.telegrambot.model.NotificationTask;

import java.time.LocalDateTime;
import java.util.List;

public interface NoticeRepository extends JpaRepository<NotificationTask, Long> {
    List<NotificationTask> getNotificationTaskByTimeEquals(LocalDateTime time);
    @Query("FROM NotificationTask WHERE sentDate < CURRENT_TIMESTAMP AND status = 'SENT'")
    List<NotificationTask> getOutdatedNotificationTask();
}
