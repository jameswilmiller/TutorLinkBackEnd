package com.tl.tutor_link.support;

import com.tl.tutor_link.user.model.Role;
import com.tl.tutor_link.user.model.User;

import java.util.HashSet;
import java.util.Set;

/**
 * Builder for User test data. Provides sensible defaults that can be
 * overridden per-test via fluent methods.
 * Usage:
 *   User user = UserFixtures.aUser().build();
 *   User tutor = UserFixtures.aTutor().withEmail("t@test.com").build();
 */
public class UserFixtures {

    private Long id = 1L;
    private String firstname = "Alice";
    private String lastname = "Test";
    private String username = "alice";
    private String email = "alice@test.com";
    private String password = "hashedPassword";
    private boolean enabled = true;
    private Set<Role> roles = new HashSet<>(Set.of(Role.STUDENT));

    public static UserFixtures aUser() {
        return new UserFixtures();
    }

    public static UserFixtures aTutor() {
        return new UserFixtures().withRoles(Role.STUDENT, Role.TUTOR);
    }

    public UserFixtures withId(Long id) {
        this.id = id;
        return this;
    }

    public UserFixtures withEmail(String email) {
        this.email = email;
        return this;
    }

    public UserFixtures withUsername(String username) {
        this.username = username;
        return this;
    }

    public UserFixtures withRoles(Role... roles) {
        this.roles = new HashSet<>(Set.of(roles));
        return this;
    }

    public UserFixtures unverified() {
        this.enabled = false;
        return this;
    }

    public User build() {
        User user = new User(firstname, lastname, username, email, password);
        user.setId(id);
        user.setEnabled(enabled);
        user.getRoles().addAll(roles);
        return user;
    }
}