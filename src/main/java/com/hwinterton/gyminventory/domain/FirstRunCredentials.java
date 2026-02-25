package com.hwinterton.gyminventory.domain;

public record FirstRunCredentials(
        String ownerUsername,
        String ownerPassword,
        String backupUsername,
        String backupPassword
) {}