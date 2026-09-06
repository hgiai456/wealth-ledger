package com.giaidev.security;

import com.giaidev.core.exception.AppException;
import com.giaidev.core.exception.CommonErrorCode;
import com.giaidev.core.security.CurrentUserProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class JwtCurrentUserProvider implements CurrentUserProvider { //get user_id from Security Context Holder , it don't have to declare
    @Override
    public String getUserId(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof JwtAuthenticationToken jwtToken)
                || !authentication.isAuthenticated()) {
            throw new AppException(CommonErrorCode.UNAUTHENTICATED);
        }

        String userId = jwtToken
                .getToken()
                .getClaimAsString("user_id");

        if(userId == null || userId.isBlank()){
            throw new AppException(CommonErrorCode.UNAUTHENTICATED);

        }

        return userId;
    }


}
