package com.mirado.iot.controller;

import com.mirado.iot.model.ChargePointId;
import com.mirado.iot.model.Rfid;
import com.mirado.iot.model.Session;
import com.mirado.iot.model.SessionId;
import com.mirado.iot.service.SessionService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

@Tag(name = "session")
@Controller
public class SessionController {
    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Get("/chargepoint/{cpid}/sessions")
    public List<SessionId> getOngoingSessions(@PathVariable ChargePointId cpid) {
        return sessionService.getSessions(cpid);
    }

    @Get("/rfid/{rfid}/sessions")
    public List<SessionId> getOngoingSessions(@PathVariable String rfid) {
        return sessionService.getSessions(new Rfid(rfid));
    }

    @Get("/sessions/ongoing")
    public List<SessionId> getOngoingSessions() {
        return sessionService.getAllOngoing();
    }

    @Get("/session/{sessionId}")
    public HttpResponse<Session> getSession(@PathVariable UUID sessionId) {
        return sessionService.getDetailed(new SessionId(sessionId))
                .map(HttpResponse::ok)
                .orElse(HttpResponse.notFound());
    }

    @Post("/session")
    public HttpResponse<SessionId> remoteSession(@Body NewSessionRequest req) {
        return sessionService.remoteSession(
                new ChargePointId(UUID.fromString(req.chargePointId())), new Rfid(req.rfid()))
                .map(HttpResponse::ok)
                .orElse(HttpResponse.unauthorized());
    }

    @Post("/session/{sessionId}/stop")
    public HttpResponse<Void> remoteStop(@PathVariable UUID sessionId) {
        return sessionService.remoteStop(new SessionId(sessionId)) ? HttpResponse.accepted() : HttpResponse.notFound();
    }
}
