package com.example.idpbackend.service.jenkins;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JenkinsJobBuildInfo {
    private String status; // e.g., "SUCCESS", "FAILURE", "IN_PROGRESS", "NOT_BUILT", "UNKNOWN"
    private Integer buildNumber;
    private boolean building;

    public JenkinsJobBuildInfo(String status, Integer buildNumber, boolean building) {
        this.status = status;
        this.buildNumber = buildNumber;
        this.building = building;
    }

    // Можно добавить статические фабричные методы для удобства
    public static JenkinsJobBuildInfo building(Integer buildNumber) {
        return new JenkinsJobBuildInfo("IN_PROGRESS", buildNumber, true);
    }

    public static JenkinsJobBuildInfo completed(String result, Integer buildNumber) {
        return new JenkinsJobBuildInfo(result != null ? result : "UNKNOWN", buildNumber, false);
    }

    public static JenkinsJobBuildInfo notBuilt() {
        return new JenkinsJobBuildInfo("NOT_BUILT", null, false);
    }
     public static JenkinsJobBuildInfo errorFetching() {
        return new JenkinsJobBuildInfo("ERROR_FETCHING_STATUS", null, false);
    }
} 