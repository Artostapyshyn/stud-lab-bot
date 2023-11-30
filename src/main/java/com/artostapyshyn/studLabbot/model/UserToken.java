package com.artostapyshyn.studLabbot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_tokens")
public class UserToken {
    @Id
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "token", nullable = false)
    private String token;

}