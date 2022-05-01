package pro.sky.telegrambot.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.StringJoiner;

@Entity
public class NotificationTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long idChat;

    private String notice;

    private LocalDateTime time;

    private int idMessage;

    public NotificationTask(Long id, long idChat, String notice, LocalDateTime time, int idMessage) {
        this.id = id;
        this.idChat = idChat;
        this.notice = notice;
        this.time = time;
        this.idMessage = idMessage;
    }

    public NotificationTask() {
    }

    public int getMessageId() {
        return idMessage;
    }

    public void setMessageId(int messageId) {
        this.idMessage = messageId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationTask that = (NotificationTask) o;
        return getIdChat() == that.getIdChat() && getMessageId() == that.getMessageId() && getId().equals(that.getId())
                && getNotice().equals(that.getNotice()) && getTime().equals(that.getTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getIdChat(), getNotice(), getTime(), getMessageId());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", NotificationTask.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("idChat=" + idChat)
                .add("notice='" + notice + "'")
                .add("time=" + time + "'")
                .add("messageId=" + idMessage)
                .toString();
    }
}
