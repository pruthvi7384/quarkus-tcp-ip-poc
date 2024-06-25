package com.tcpip.dto;

import lombok.Data;

@Data
public class ErrorRes {
    private String errorCode;
    private String errorDescription;
}
