package com.ohgoodteam.ohgoodpay.common.cursor;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class CursorUtil {
    private static final ObjectMapper om = new ObjectMapper()
    .registerModule(new JavaTimeModule())
    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public static String encode(Cursor c) {
        if (c == null) return null;
        try {
            String json = om.writeValueAsString(c);
            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalArgumentException("cursor encode error", e);
        }
    }

    public static Cursor decode(String s) {
        if (s == null || s.isBlank()) return new Cursor(null, null, null);
        try {
            byte[] bytes = Base64.getUrlDecoder().decode(s);
            return om.readValue(bytes, Cursor.class);
        } catch (Exception e) {
            
            return new Cursor(null, null, null);
        }
    }
}
