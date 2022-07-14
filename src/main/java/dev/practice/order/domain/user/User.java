package dev.practice.order.domain.user;

import dev.practice.order.domain.AbstractEntity;
import dev.practice.order.domain.order.Order;
import dev.practice.order.domain.partner.Partner;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Getter
@NoArgsConstructor
@Entity
public class User extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column
    private String picture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToOne(cascade = CascadeType.ALL, fetch = LAZY)
    @JoinColumn(name = "partner_id") //foregin key
    private Partner partner;

    @Builder
    public User(String name, String email, String picture, Role role, Partner partner) {
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.role = role;
        this.partner = partner;
    }

    public User update(String name, String picture) {
        this.name = name;
        this.picture = picture;

        return this;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }
}
