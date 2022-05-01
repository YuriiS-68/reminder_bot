package pro.sky.telegrambot.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambot.model.NotificationTask;

import java.time.LocalDateTime;
import java.util.List;

public interface NoticeRepository extends JpaRepository<NotificationTask, Long> {
    List<NotificationTask> getNotificationTaskByTimeEquals(LocalDateTime time);
}
