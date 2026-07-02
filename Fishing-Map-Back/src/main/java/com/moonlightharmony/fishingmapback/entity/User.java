package com.moonlightharmony.fishingmapback.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id"}),
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
@Builder(toBuilder = true)
public class User extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String userId;

    @Column(nullable = false, length = 40)
    private String username;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(length = 20)
    private String phoneNumber;
    @Column(length = 20)
    private String language;
    @Column(length = 10)
    private String useYn;

    @Column(length = 400)
    private String note;

    /*변경 메서드*/
    public void update(String username, String email,String password ,String phoneNumber,
                      String language, String useYn, String note) {
        this.password = password;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.language = language;
        this.useYn = useYn;
        this.note = note;
    }

}
