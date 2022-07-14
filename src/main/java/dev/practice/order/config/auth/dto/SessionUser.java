package dev.practice.order.config.auth.dto;

import dev.practice.order.domain.partner.Partner;
import dev.practice.order.domain.user.Role;
import dev.practice.order.domain.user.User;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SessionUser implements Serializable {

    private Long id;
    private String name;
    private String email;
    private String picture;
    private Role role;
    private Partner partner;

    public SessionUser(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.picture = user.getPicture();
        this.role = user.getRole();
        this.id = user.getId();
        this.partner = user.getPartner();
    }
}
