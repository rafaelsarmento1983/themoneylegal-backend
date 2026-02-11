package com.moneylegal.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;

public class JwtKeyGenerator {
    public static void main(String[] args) {
        var key = Jwts.SIG.HS512.key().build(); // 512 bits garantido
        System.out.println("Encoders: " + Encoders.BASE64.encode(key.getEncoded()));
    }
}