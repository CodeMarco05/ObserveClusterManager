package org.observe.controller;

import com.observe.openapi.api.GeneralApi;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.observe.auth.ApiKeySecured;
import org.observe.service.GeneralService;

@ApplicationScoped
@ApiKeySecured
public class GeneralController implements GeneralApi {

    @Inject
    GeneralService generalService;

    @Override
    public Integer uptimeInSeconds() {
        return generalService.getUptimeInSeconds();
    }
}
