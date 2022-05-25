package com.st.authlight.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.util.Objects;

import static java.util.Objects.hash;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    @Id
    private Long id;
    private String role;
    private String login;

    @ToString.Exclude
    private String password;

    private Boolean passwordExpired;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User a)) return false;
        return Objects.equals(getId(), a.getId());
    }

    @Override
    public int hashCode() {
        return hash(id);
    }
}