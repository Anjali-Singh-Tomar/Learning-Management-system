package org.employdemy.library.lms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class NotificationSettings {

    @Id
    private Long userId;

    @OneToOne
    @JoinColumn(name = "user_id")
    @MapsId
    private User user;

    private Boolean dueDateReminder;

    private Boolean newBooksReminder;

    private Boolean receiveEmail;

    public NotificationSettings(User user,
                                Boolean dueDateReminder,
                                Boolean newBooksReminder,
                                Boolean receiveEmail) {
        this.user = user;
        this.dueDateReminder = dueDateReminder;
        this.newBooksReminder = newBooksReminder;
        this.receiveEmail = receiveEmail;
    }
}
