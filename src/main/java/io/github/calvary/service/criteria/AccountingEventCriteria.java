package io.github.calvary.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link io.github.calvary.domain.AccountingEvent} entity. This class is used
 * in {@link io.github.calvary.web.rest.AccountingEventResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /accounting-events?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AccountingEventCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LocalDateFilter eventDate;

    private LongFilter eventTypeId;

    private LongFilter dealerId;

    private Boolean distinct;

    public AccountingEventCriteria() {}

    public AccountingEventCriteria(AccountingEventCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.eventDate = other.eventDate == null ? null : other.eventDate.copy();
        this.eventTypeId = other.eventTypeId == null ? null : other.eventTypeId.copy();
        this.dealerId = other.dealerId == null ? null : other.dealerId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public AccountingEventCriteria copy() {
        return new AccountingEventCriteria(this);
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

    public LocalDateFilter getEventDate() {
        return eventDate;
    }

    public LocalDateFilter eventDate() {
        if (eventDate == null) {
            eventDate = new LocalDateFilter();
        }
        return eventDate;
    }

    public void setEventDate(LocalDateFilter eventDate) {
        this.eventDate = eventDate;
    }

    public LongFilter getEventTypeId() {
        return eventTypeId;
    }

    public LongFilter eventTypeId() {
        if (eventTypeId == null) {
            eventTypeId = new LongFilter();
        }
        return eventTypeId;
    }

    public void setEventTypeId(LongFilter eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public LongFilter getDealerId() {
        return dealerId;
    }

    public LongFilter dealerId() {
        if (dealerId == null) {
            dealerId = new LongFilter();
        }
        return dealerId;
    }

    public void setDealerId(LongFilter dealerId) {
        this.dealerId = dealerId;
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
        final AccountingEventCriteria that = (AccountingEventCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(eventDate, that.eventDate) &&
            Objects.equals(eventTypeId, that.eventTypeId) &&
            Objects.equals(dealerId, that.dealerId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, eventDate, eventTypeId, dealerId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AccountingEventCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (eventDate != null ? "eventDate=" + eventDate + ", " : "") +
            (eventTypeId != null ? "eventTypeId=" + eventTypeId + ", " : "") +
            (dealerId != null ? "dealerId=" + dealerId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
