package pro.sky.telegrambot.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambot.model.NotificationTask;

import java.time.LocalDateTime;

public interface NoticeRepository extends JpaRepository<NotificationTask, Long> {
    NotificationTask getNotificationTaskByTimeEquals(LocalDateTime time);

    @Override
    void deleteById(Long id);
}
