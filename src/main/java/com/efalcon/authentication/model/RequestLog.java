package com.efalcon.authentication.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "JSON")
    private String request;

    @Column(columnDefinition = "JSON")
    private String response;

    @Column(columnDefinition = "JSON")
    private String headers;

    private String requestIp;

    private String requestUri;

    private String userAgent;

    private String params;

    private String requestId;

    private LocalDateTime creationDate;
}

