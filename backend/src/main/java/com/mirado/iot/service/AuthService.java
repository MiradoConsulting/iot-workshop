package com.mirado.iot.service;

import com.mirado.iot.model.ChargePointId;
import com.mirado.iot.model.Rfid;

import javax.inject.Singleton;
import java.util.Optional;
import java.util.Set;

@Singleton
public class AuthService {
    private final Set<Rfid> rfids;

    public AuthService(Set<Rfid> rfids) {
        this.rfids = rfids;
    }

    public boolean auth(Rfid rfid, ChargePointId chargePointId) {
        return rfids.contains(rfid);
    }

    public void put(Rfid rfid) {
        rfids.add(rfid);
    }

    public void delete(Rfid rfid) {
        rfids.remove(rfid);
    }

    public Optional<Rfid> get(Rfid rfid) {
        return rfids.contains(rfid) ? Optional.of(rfid) : Optional.empty();
    }

    public Set<Rfid> getAll() {
        return rfids;
    }
}
