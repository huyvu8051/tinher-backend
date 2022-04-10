package com.bobvu.tinherbackend.auth.google;

import lombok.Data;

@Data
public class GoogleAuthenticationRequest {
    private String idToken;

    private double lat;
    private double lon;


}