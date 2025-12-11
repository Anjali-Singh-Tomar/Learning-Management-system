package org.employdemy.library.lms.service;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OtpStore {

    private static final int EXPIRY_MINUTES = 5;

    private final ConcurrentHashMap<String, OtpData> store = new ConcurrentHashMap<>();

    public void saveOtp(String identifier, String otp) {
        store.put(identifier, new OtpData(otp, LocalDateTime.now().plusMinutes(EXPIRY_MINUTES)));
    }

    public String getOtp(String identifier) {
        OtpData data = store.get(identifier);

        if (data == null) return null;

        if (LocalDateTime.now().isAfter(data.expiry)) {
            store.remove(identifier);
            return null; // expired
        }

        return data.otp;
    }

    public void clearOtp(String identifier) {
        store.remove(identifier);
    }

    public boolean verifyOtp(String identifier, String otp) {
        String stored = getOtp(identifier);
        if (stored != null && stored.equals(otp)) {
            clearOtp(identifier);
            return true;
        }
        return false;
    }


    private record OtpData(String otp, LocalDateTime expiry) {}
}
