package pro.sky.telegrambot.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.StringJoiner;

@Entity
public class NotificationTask {
    @Id
    @SequenceGenerator(name = "notification_task_id_seq", sequenceName = "notification_task_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_task_id_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "idChat", nullable = false)
    private long idChat;

    @Column(name = "notice", nullable = false)
    private String notice;

    @Column(name = "time")
    private LocalDateTime time;

    public NotificationTask(Long id, long idChat, String notice, LocalDateTime time) {
        this.id = id;
        this.idChat = idChat;
        this.notice = notice;
        this.time = time;
    }

    public NotificationTask() {
    }

    public long getIdChat() {
        return idChat;
    }

    public void setIdChat(long idChat) {
        this.idChat = idChat;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationTask that = (NotificationTask) o;
        return getIdChat() == that.getIdChat() && getId().equals(that.getId()) && getNotice().equals(that.getNotice()) && Objects.equals(getTime(), that.getTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getIdChat(), getNotice(), getTime());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", NotificationTask.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("idChat=" + idChat)
                .add("notice='" + notice + "'")
                .add("time=" + time)
                .toString();
    }
}
