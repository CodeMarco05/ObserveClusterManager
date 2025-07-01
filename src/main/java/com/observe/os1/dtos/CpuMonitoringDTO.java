package com.observe.os1.dtos;

public record CpuMonitoringDTO(Boolean enabled, String cpuUsageFile, String interval) {}