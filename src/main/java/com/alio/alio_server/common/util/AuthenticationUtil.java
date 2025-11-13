package com.alio.alio_server.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthenticationUtil {
    public static Long getUserId() {
        var anonymous = String.valueOf(isAnonymous());
        log.warn("known : "+anonymous);
        if (isAnonymous()) {
            return null;
        }
        return (Long) getAuthentication().getPrincipal();
    }

    public static boolean isAnonymous() {
        Authentication authentication = getAuthentication();
        return authentication == null || authentication.getPrincipal().equals("anonymousUser");
    }

    private static Authentication getAuthentication() {
        var context = SecurityContextHolder.getContext();
        log.info("Context :"+ context);
        var result = context.getAuthentication();
        log.info("result : " + result);
        return result;    }
}
