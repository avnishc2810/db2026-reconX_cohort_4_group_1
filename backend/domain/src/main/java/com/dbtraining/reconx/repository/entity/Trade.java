package com.dbtraining.reconx.repository.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "trades")
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tradeRef;

    private LocalDate tradeDate;

    private Long instrumentId;

    private Long counterpartyId;

    private Integer quantity;

    private Double price;

    private String status;              // ⭐ REQUIRED FOR ADV066
    private String updatedBy;           // ⭐ REQUIRED FOR ADV066
    private LocalDateTime updatedAt;    // ⭐ REQUIRED FOR ADV066

    private LocalDateTime deletedAt;    // ⭐ Required for softDelete

    // ----- Getters -----

    public Long getId() {
        return id;
    }

    public String getTradeRef() {
        return tradeRef;
    }

    public LocalDate getTradeDate() {
        return tradeDate;
    }

    public Long getInstrumentId() {
        return instrumentId;
    }

    public Long getCounterpartyId() {
        return counterpartyId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Double getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    // ----- Setters -----

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    // ----- Soft delete -----

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}
