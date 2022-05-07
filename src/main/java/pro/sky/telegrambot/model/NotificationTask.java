package pro.sky.telegrambot.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.StringJoiner;

@Entity
public class NotificationTask {
    public enum NotificationStatus{
        PENDING,
        SENT
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private long idChat;
    private String notice;
    private LocalDateTime time;
    private int idMessage;

    private LocalDateTime sentDate;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status = NotificationStatus.PENDING;

    public NotificationTask(String notice, LocalDateTime time, NotificationStatus status) {
        this.notice = notice;
        this.time = time;
        this.status = status;
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

    public LocalDateTime getSentDate() {
        return sentDate;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setSentDate(LocalDateTime sentDate) {
        this.sentDate = sentDate;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    public void markAsSent(){
        this.status = NotificationStatus.SENT;
        this.sentDate = LocalDateTime.now();
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
        return getIdChat() == that.getIdChat() && idMessage == that.idMessage && Objects.equals(getId(), that.getId())
                && Objects.equals(getNotice(), that.getNotice()) && Objects.equals(getTime(), that.getTime())
                && Objects.equals(getSentDate(), that.getSentDate()) && getStatus() == that.getStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getIdChat(), getNotice(), getTime(), idMessage, getSentDate(), getStatus());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", NotificationTask.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("idChat=" + idChat)
                .add("notice='" + notice + "'")
                .add("time=" + time)
                .add("idMessage=" + idMessage)
                .add("sentDate=" + sentDate)
                .add("status=" + status)
                .toString();
    }
}
