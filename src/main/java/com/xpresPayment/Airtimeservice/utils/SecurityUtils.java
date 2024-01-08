package com.xpresPayment.Airtimeservice.utils;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    //Get Principal
    public static <T> T getAuthenticatedUser(Class<T> type){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && type.isAssignableFrom(authentication.getPrincipal().getClass())){
            return type.cast(authentication.getPrincipal());
        }
        throw new AccessDeniedException("Access denied");
    }

}
