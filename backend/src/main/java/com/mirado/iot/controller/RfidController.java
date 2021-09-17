package com.mirado.iot.controller;

import com.mirado.iot.model.Rfid;
import com.mirado.iot.service.AuthService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Collection;

import static io.micronaut.http.HttpResponse.ok;

@Tag(name = "rfid")
@Controller
public class RfidController {
    private final AuthService authService;

    public RfidController(AuthService authService) {
        this.authService = authService;
    }

    @Post("/rfid")
    public HttpResponse<String> addRfid(String rfid) {
        authService.put(new Rfid(rfid));
        return ok(rfid);
    }

    @Delete("/rfid/{rfid}")
    public HttpResponse<Void> deleteRfid(@PathVariable String rfid) {
        authService.delete(new Rfid(rfid));
        return ok();
    }

    @Get("/rfid/{rfid}")
    public HttpResponse<Rfid> get(@PathVariable String rfid) {
        return authService.get(new Rfid(rfid)).map(HttpResponse::ok).orElse(HttpResponse.notFound());
    }

    @Get("/rfids")
    public Collection<Rfid> getAll() {
        return authService.getAll();
    }
}
