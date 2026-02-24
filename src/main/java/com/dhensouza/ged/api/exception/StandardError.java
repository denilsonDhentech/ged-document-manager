package com.dhensouza.ged.api.exception;

import java.time.Instant;

public record StandardError(
        Integer status,
        String message,
        Instant timestamp,
        String path
) {}