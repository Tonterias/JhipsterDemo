package com.demo.webapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.demo.webapp.domain.Appuser} entity. This class is used
 * in {@link com.demo.webapp.web.rest.AppuserResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /appusers?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AppuserCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter date;

    private StringFilter insuranceCompany;

    private BigDecimalFilter balance;

    private StringFilter country;

    private LongFilter userId;

    private Boolean distinct;

    public AppuserCriteria() {}

    public AppuserCriteria(AppuserCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.date = other.date == null ? null : other.date.copy();
        this.insuranceCompany = other.insuranceCompany == null ? null : other.insuranceCompany.copy();
        this.balance = other.balance == null ? null : other.balance.copy();
        this.country = other.country == null ? null : other.country.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public AppuserCriteria copy() {
        return new AppuserCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public InstantFilter getDate() {
        return date;
    }

    public InstantFilter date() {
        if (date == null) {
            date = new InstantFilter();
        }
        return date;
    }

    public void setDate(InstantFilter date) {
        this.date = date;
    }

    public StringFilter getInsuranceCompany() {
        return insuranceCompany;
    }

    public StringFilter insuranceCompany() {
        if (insuranceCompany == null) {
            insuranceCompany = new StringFilter();
        }
        return insuranceCompany;
    }

    public void setInsuranceCompany(StringFilter insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    public BigDecimalFilter getBalance() {
        return balance;
    }

    public BigDecimalFilter balance() {
        if (balance == null) {
            balance = new BigDecimalFilter();
        }
        return balance;
    }

    public void setBalance(BigDecimalFilter balance) {
        this.balance = balance;
    }

    public StringFilter getCountry() {
        return country;
    }

    public StringFilter country() {
        if (country == null) {
            country = new StringFilter();
        }
        return country;
    }

    public void setCountry(StringFilter country) {
        this.country = country;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public LongFilter userId() {
        if (userId == null) {
            userId = new LongFilter();
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AppuserCriteria that = (AppuserCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(date, that.date) &&
            Objects.equals(insuranceCompany, that.insuranceCompany) &&
            Objects.equals(balance, that.balance) &&
            Objects.equals(country, that.country) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, insuranceCompany, balance, country, userId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AppuserCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (date != null ? "date=" + date + ", " : "") +
            (insuranceCompany != null ? "insuranceCompany=" + insuranceCompany + ", " : "") +
            (balance != null ? "balance=" + balance + ", " : "") +
            (country != null ? "country=" + country + ", " : "") +
            (userId != null ? "userId=" + userId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
