package gg.demo.wso2conn.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.parser.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.util.Map;

@Controller
@RequestMapping("/dev")
public class MainController {

    @Autowired
    private OAuth2AuthorizedClientManager auth2AuthorizedClientManager;

    private static final Logger log = LoggerFactory.getLogger(MainController.class);
    @GetMapping
    public String getIndex(OAuth2AuthenticationToken authentication, Model model,
                           HttpServletRequest servletRequest,
                           HttpServletResponse servletResponse){
        log.info("getAuthorizedClientRegistrationId: "+authentication.getAuthorizedClientRegistrationId());
        log.info("authentication.getName: "+authentication.getName());

        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId("wso2")
                .principal(authentication)
                .attributes(attrs -> {
                    attrs.put(HttpServletRequest.class.getName(), servletRequest);
                    attrs.put(HttpServletResponse.class.getName(), servletResponse);
                })
                .build();

        OAuth2AuthorizedClient client = auth2AuthorizedClientManager.authorize(authorizeRequest);

        String userInfoEndpointUri = client.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUri();

        if(!StringUtils.isEmpty(userInfoEndpointUri)) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            log.info("email: "+auth.getName());
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + client.getAccessToken().getTokenValue());
            HttpEntity<String> entity = new HttpEntity<String>("", headers);

            ResponseEntity<Map> response = restTemplate.exchange(userInfoEndpointUri, HttpMethod.GET, entity, Map.class);
            Map userAttributes = response.getBody();

            for (Object key : userAttributes.keySet()){
                log.info("K: "+key.toString()+" V: "+userAttributes.get(key).toString());
            }

            model.addAttribute("name", userAttributes.get("email"));
        }

        return "index";
    }
}
