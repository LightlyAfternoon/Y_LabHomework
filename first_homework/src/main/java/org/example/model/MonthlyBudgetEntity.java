package org.example.model;

import java.sql.Date;
import java.util.UUID;

public class MonthlyBudgetEntity {
    private final UUID uuid;
    private final Date date;

    public MonthlyBudgetEntity() {
        this.uuid = UUID.randomUUID();
        this.date = new Date(System.currentTimeMillis());
    }

    public MonthlyBudgetEntity(UUID uuid, Date date) {
        this.uuid = uuid;
        this.date = date;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Date getDate() {
        return date;
    }
}