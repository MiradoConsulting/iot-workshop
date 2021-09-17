package com.mirado.iot.service;

import com.mirado.iot.event.*;
import com.mirado.iot.model.ChargePointId;
import com.mirado.iot.model.Rfid;
import com.mirado.iot.model.Session;
import com.mirado.iot.model.SessionId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.time.Instant;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Optional.empty;

@Singleton
public class SessionService {
    private static final Logger LOG = LoggerFactory.getLogger(SessionService.class);
    private final AuthService authService;
    private final ChargepointClient chargepointClient;
    private final HashMap<SessionId, List<SessionEvent>> store;

    public SessionService(AuthService authService, ChargepointClient chargepointClient) {
        this.authService = authService;
        this.chargepointClient = chargepointClient;
        this.store = new HashMap<>();
    }

    public void put(SessionEvent event) {
        store.putIfAbsent(event.sessionId(), new ArrayList<>());
        store.get(event.sessionId()).add(event);
    }

    public Optional<Session> get(SessionId sessionId) {
        return Optional.ofNullable(store.get(sessionId))
                .map(es -> fromEvents(sessionId, es));
    }

    public Optional<SessionId> remoteSession(ChargePointId chargePointId, Rfid rfid) {
        LOG.debug("Remote session request received: cp: {}; rfid: {}", chargePointId, rfid);
        boolean isAuthed = authService.auth(rfid, chargePointId);
        if (isAuthed) {
            SessionId sessionId = new SessionId(UUID.randomUUID());
            put(new SessionCreated(sessionId, rfid, chargePointId));
            put(new SessionStarted(sessionId, Instant.now()));
            chargepointClient.start(chargePointId, sessionId);
            return Optional.ofNullable(sessionId);
        }
        return Optional.empty();
    }

    public boolean remoteStop(SessionId sessionId) {
        LOG.debug("Remote session stop request received: {}", sessionId);
        Optional<Session> mSession = Optional.ofNullable(store.get(sessionId)).map(s -> fromEvents(sessionId, s));
        Optional<Rfid> mRfid = mSession.flatMap(Session::rfid);
        mRfid.ifPresent(r -> authStop(sessionId, r));
        return mRfid.isPresent();
    }

    public void authSession(ChargePointId chargePointId, Rfid rfid) {
        LOG.debug("Auth session request received: cp: {}; rfid: {}", chargePointId, rfid);
        boolean isAuthed = authService.auth(rfid, chargePointId);
        if (isAuthed) {
            SessionId sessionId = new SessionId(UUID.randomUUID());
            put(new SessionCreated(sessionId, rfid, chargePointId));
            put(new SessionStarted(sessionId, Instant.now()));
            chargepointClient.start(chargePointId, sessionId);
        }
    }

    public void authStop(SessionId sessionId, Rfid rfid) {
        LOG.debug("Auth stop request received: session: {}; rfid: {}", sessionId, rfid);
        Optional<Session> mSession = get(sessionId);
        mSession.ifPresent(session ->
            session.rfid()
                    .filter(r -> r.equals(rfid))
                    .ifPresent(good ->
                            session.chargePointId().ifPresent(cpid ->
                                    chargepointClient.stop(cpid, sessionId))));
    }

    private Session fromEvents(SessionId sessionId, List<SessionEvent> events) {
        Optional<ChargePointId> chargePointId = empty();
        Optional<Rfid> rfid = empty();
        Optional<Instant> startTime = empty();
        Optional<Instant> stopTime = empty();
        Optional<Long> energy = empty();

        for (SessionEvent event : events) {
            if (event instanceof SessionCreated e) {
                chargePointId = Optional.of(e.chargePointId());
                rfid = Optional.of(e.rfid());
            } else if (event instanceof SessionStarted e) {
                startTime = Optional.of(e.timestamp());
            } else if (event instanceof SessionStopped e) {
                stopTime = Optional.of(e.timestamp());
            } else if (event instanceof EnergyDelivered e) {
                energy = Optional.of(energy.orElse(0L) + e.qty());
            } else {
                throw new IllegalStateException("Unknown event type: " + event.getClass());
            }
        }

        return new Session(sessionId, chargePointId, rfid, startTime, stopTime, energy);
    }

    private List<SessionId> getSessions(Predicate<Session> filter) {
        return store.entrySet().stream()
                .map(e -> fromEvents(e.getKey(), e.getValue()))
                .filter(filter)
                .map(Session::sessionId)
                .collect(Collectors.toList());
    }

    public List<SessionId> getAllOngoing() {
        return getSessions(s -> s.stopTime().isEmpty());
    }

    public List<SessionId> getSessions(ChargePointId chargePointId) {
        return getSessions(s -> s.chargePointId().filter(cp -> cp.equals(chargePointId)).isPresent());
    }

    public List<SessionId> getSessions(Rfid rfid) {
        return getSessions(s -> s.rfid().filter(r -> r.equals(rfid)).isPresent());
    }

    public Optional<Session> getDetailed(SessionId sessionId) {
        return Optional.ofNullable(store.get(sessionId)).map(s -> fromEvents(sessionId, s));
    }
}
