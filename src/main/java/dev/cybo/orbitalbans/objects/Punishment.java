package dev.cybo.orbitalbans.objects;

import dev.cybo.orbitalbans.enums.PunishmentType;

public class Punishment {

    private final String playerName;
    private final PunishmentType punishmentType;
    private final String administrator;
    private final long createdAt;
    private long ends;
    private final String reason;

    private Punishment(Builder builder) {
        this.playerName = builder.playerName;
        this.punishmentType = builder.punishmentType;
        this.administrator = builder.administrator;
        this.createdAt = builder.createdAt;
        this.ends = builder.ends;
        this.reason = builder.reason;
    }

    public static class Builder {
        private String playerName = "Unknown";
        private final PunishmentType punishmentType;
        private String administrator = "Unknown";
        private long createdAt = 0;
        private long ends = 0;
        private String reason = "Unknown";

        public Builder(PunishmentType punishmentType) {
            this.punishmentType = punishmentType;
        }

        public Builder playerName(String playerName) {
            this.playerName = playerName;
            return this;
        }

        public Builder administrator(String administrator) {
            this.administrator = administrator;
            return this;
        }

        public Builder createdAt(long createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder ends(long ends) {
            this.ends = ends;
            return this;
        }

        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public Punishment build() {
            return new Punishment(this);
        }
    }

    public String getPlayerName() {
        return playerName;
    }

    public PunishmentType getPunishmentType() {
        return punishmentType;
    }

    public String getAdministrator() {
        return administrator;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getEnds() {
        return ends;
    }

    public void setEnds(long ends) {
        this.ends = ends;
    }

    public String getReason() {
        return reason;
    }

    public boolean isActive() {
        return ends == 0 || ends > System.currentTimeMillis();
    }
}