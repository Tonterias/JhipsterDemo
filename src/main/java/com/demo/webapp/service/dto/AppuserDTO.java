package com.demo.webapp.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.demo.webapp.domain.Appuser} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AppuserDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant date;

    private String insuranceCompany;

    private BigDecimal balance;

    private String country;

    private UserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(String insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AppuserDTO)) {
            return false;
        }

        AppuserDTO appuserDTO = (AppuserDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, appuserDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AppuserDTO{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", insuranceCompany='" + getInsuranceCompany() + "'" +
            ", balance=" + getBalance() +
            ", country='" + getCountry() + "'" +
            ", user=" + getUser() +
            "}";
    }
}
