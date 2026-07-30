package com.example.demo.security;

import com.example.demo.auth.service.AuthAccountService;
import com.example.demo.user.entity.User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OAuth2UserService extends DefaultOAuth2UserService {
    
    private final AuthAccountService authAccountService;

    public OAuth2UserService(AuthAccountService authAccountService){
        this.authAccountService = authAccountService;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String provider = userRequest.getClientRegistration().getRegistrationId();
        
        // Different providers use different fields for their unique ID
        String providerUserId = provider.equals("google") ? oAuth2User.getAttribute("sub") : oAuth2User.getAttribute("id").toString();

        User user = authAccountService.processOAuthPostLogin(email, name, provider, providerUserId);

        return new UserDetailsImpl(user, oAuth2User.getAttributes());
    }
}
