package com.colibrihub.wordpress.service;

import java.util.Map;

public interface EspoCrmService {

    Map<String, Object> createLead(String firstName, String lastName,
                                   String email, String description);
}
