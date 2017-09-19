package cn.com.sit.tokenservice.config;


import cn.com.sit.tokenservice.identity.TokenUser;
import cn.com.sit.tokenservice.identity.TokenUtil;
import cn.com.sit.tokenservice.model.response.OperationResponse;
import cn.com.sit.tokenservice.model.session.SessionItem;
import cn.com.sit.tokenservice.model.session.SessionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.oracle.javafx.jmx.json.JSONException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;



/* This filter maps to /session and tries to validate the username and password */
@Slf4j
public class GenerateTokenForUserFilter extends AbstractAuthenticationProcessingFilter {

    private TokenUtil tokenUtil;



    protected GenerateTokenForUserFilter(String urlMapping, AuthenticationManager authenticationManager, TokenUtil tokenUtil) {
        super(new AntPathRequestMatcher(urlMapping));
        setAuthenticationManager(authenticationManager);
        this.tokenUtil = tokenUtil;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException, JSONException {
        try{


            log.info("/session invoked" );

            String jsonString = IOUtils.toString(request.getInputStream(), "UTF-8");
            /* using org.json */
            JSONObject userJSON = new JSONObject(jsonString);
            String username = userJSON.getString("username");
            String password = userJSON.getString("password");
            log.info("username:{} and paswword:{} \n", username, password);
            //final UsernamePasswordAuthenticationToken loginToken = new UsernamePasswordAuthenticationToken("demo", "demo");
            final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
            return getAuthenticationManager().authenticate(authToken); // This will take to successfulAuthentication or faliureAuthentication function
        }
        catch( JSONException | AuthenticationException e){
            throw new AuthenticationServiceException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication (HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication authToken) throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(authToken);
        /*
        JSONObject jsonResp = new JSONObject();
        TokenUser tokenUser = (TokenUser)authToken.getPrincipal();
        String newToken = this.tokenUtil.createTokenForUser(tokenUser);

        jsonResp.put("token",newToken);
        jsonResp.put("firstName",tokenUser.getUser().getFirstName());
        jsonResp.put("lastName",tokenUser.getUser().getLastName());
        jsonResp.put("email",tokenUser.getUser().getEmail());
        jsonResp.put("role",tokenUser.getRole());
        */
        //System.out.println(authToken.getName());

        TokenUser tokenUser = (TokenUser)authToken.getPrincipal();
        SessionResponse resp = new SessionResponse();
        SessionItem respItem = new SessionItem();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String tokenString = this.tokenUtil.createTokenForUser(tokenUser);

        respItem.setFirstName(tokenUser.getUser().getFirstName());
        respItem.setLastName(tokenUser.getUser().getLastName());
        respItem.setUserId(tokenUser.getUser().getUserId());
        respItem.setEmail(tokenUser.getUser().getEmail());
        respItem.setToken(tokenString);

        resp.setOperationStatus(OperationResponse.ResponseStatusEnum.SUCCESS);
        resp.setOperationMessage("Login Success");
        resp.setItem(respItem);
        String jsonRespString = ow.writeValueAsString(resp);

        res.setStatus(HttpServletResponse.SC_OK);
        res.getWriter().write(jsonRespString);
        //res.getWriter().write(jsonResp.toString());
        res.getWriter().flush();
        res.getWriter().close();

        // DONT call supper as it contine the filter chain super.successfulAuthentication(req, res, chain, authResult);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse res, AuthenticationException failed) throws IOException, ServletException {

        OperationResponse resp = new OperationResponse();
        resp.setOperationStatus(OperationResponse.ResponseStatusEnum.ERROR);
        resp.setOperationMessage("Login Faild : "+failed.getMessage());
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String jsonRespString = ow.writeValueAsString(resp);
        res.setStatus(HttpServletResponse.SC_OK);
        res.getWriter().write(jsonRespString);
        //res.getWriter().write(jsonResp.toString());
        res.getWriter().flush();
        res.getWriter().close();


    }
}
