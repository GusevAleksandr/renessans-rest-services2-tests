package com.otr.ru.renessans;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import sun.misc.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;

/**
 * @author gusev.aleksandr
 * @since 22.07.19
 */
@Controller
public class MainController {

    public static final String AGENT_LOGIN = "210069";
    public static final String AGENT_PASSWORD = "l12345";

    public static final String PARTNER_LOGIN = "9189778523";
    public static final String PARTNER_PASSWORD = "l12345";

    private RestTemplate restTemplate = new RestTemplate();

    @Value(value = "${ufos.url}")
    private String ufosUrl;

    @GetMapping(value = "/")
    public ResponseEntity<String> test2() throws IOException {
        InputStream is = getClass().getResourceAsStream("/templates/page.html");
        return new ResponseEntity<>(new String(IOUtils.readNBytes(is, is.available())), HttpStatus.OK);
    }

    @GetMapping(value = "/method1/{documentGuid}")
    public ResponseEntity<String> method1(@PathVariable("documentGuid") String documentGuid) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getToken(AGENT_LOGIN, AGENT_PASSWORD));
        HttpEntity entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> result;
        try {
            result = restTemplate.exchange(ufosUrl + "/services/func/rest-api/print/" + documentGuid, HttpMethod.GET, entity, byte[].class);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            return new ResponseEntity<>(new String(ex.getResponseBodyAsByteArray()), ex.getStatusCode());
        }
        if (result.getStatusCode() == HttpStatus.OK) {
            return new ResponseEntity<>(Base64.getEncoder().encodeToString(result.getBody()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new String(result.getBody()), result.getStatusCode());
        }
    }

    @GetMapping(value = "/method2/{documentGuid}")
    public ResponseEntity<String> method2(@PathVariable("documentGuid") String documentGuid) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getToken(AGENT_LOGIN, AGENT_PASSWORD));
        HttpEntity entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> result;
        try {
            result = restTemplate.exchange(ufosUrl + "/services/func/rest-api/export/" + documentGuid, HttpMethod.GET, entity, byte[].class);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            return new ResponseEntity<>(new String(ex.getResponseBodyAsByteArray()), ex.getStatusCode());
        }
        if (result.getStatusCode() == HttpStatus.OK) {
            return new ResponseEntity<>(Base64.getEncoder().encodeToString(result.getBody()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new String(result.getBody()), result.getStatusCode());
        }
    }

    @PostMapping(value = "/method4",consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> method4(@RequestParam Map<String, String> name) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getToken(PARTNER_LOGIN, PARTNER_PASSWORD));
        HttpEntity entity = new HttpEntity<>(name, headers);
        ResponseEntity<String> result;
        try {
            result = restTemplate.exchange(ufosUrl + "/services/func/rest-api/sign/new/attach/", HttpMethod.PUT, entity, String.class);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            return new ResponseEntity<>(new String(ex.getResponseBodyAsByteArray()), ex.getStatusCode());
        }
        if (result.getStatusCode() == HttpStatus.OK) {
            return new ResponseEntity<>(result.getBody(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(result.getBody(), result.getStatusCode());
        }
    }

    @GetMapping(value = "/method5/{documentGuid}")
    public ResponseEntity<String> method5(@PathVariable("documentGuid") String documentGuid) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getToken(PARTNER_LOGIN, PARTNER_PASSWORD));
        HttpEntity entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> result;
        try {
            result = restTemplate.exchange(ufosUrl + "/services/func/rest-api/sign/new/start/" + documentGuid, HttpMethod.POST, entity, byte[].class);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            return new ResponseEntity<>(new String(ex.getResponseBodyAsByteArray()), ex.getStatusCode());
        }
        if (result.getStatusCode() == HttpStatus.OK) {
            return new ResponseEntity<>(Base64.getEncoder().encodeToString(result.getBody()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new String(result.getBody()), result.getStatusCode());
        }
    }

    @GetMapping(value = "/method6/{documentGuid}/{technicalMode}")
    public ResponseEntity<String> method6(@PathVariable("documentGuid") String documentGuid, @PathVariable("technicalMode") String technicalMode) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getToken(PARTNER_LOGIN, PARTNER_PASSWORD));
        HttpEntity entity = new HttpEntity<>(headers);
        ResponseEntity<String> result;
        try {
            result = restTemplate.exchange(ufosUrl + "/services/func/rest-api/sign/new/main/" + documentGuid +
                    (Boolean.parseBoolean(technicalMode) ? "/neo" : Strings.EMPTY), HttpMethod.POST, entity, String.class);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            return new ResponseEntity<>(new String(ex.getResponseBodyAsByteArray()), ex.getStatusCode());
        }
        if (result.getStatusCode() == HttpStatus.OK) {
            return new ResponseEntity<>(result.getBody(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(result.getBody(), result.getStatusCode());
        }
    }

    @GetMapping(value = "/method7/{documentGuid}/{smsCode}")
    public ResponseEntity<String> method7(@PathVariable("documentGuid") String documentGuid, @PathVariable("smsCode") String smsCode) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getToken(PARTNER_LOGIN, PARTNER_PASSWORD));
        HttpEntity entity = new HttpEntity<>(Collections.singletonMap("userCode", smsCode), headers);
        ResponseEntity<String> result;
        try {
            result = restTemplate.exchange(ufosUrl + "/services/func/rest-api/sign/new/approve/" + documentGuid, HttpMethod.POST, entity, String.class);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            return new ResponseEntity<>(new String(ex.getResponseBodyAsByteArray()), ex.getStatusCode());
        }
        if (result.getStatusCode() == HttpStatus.OK) {
            return new ResponseEntity<>(result.getBody(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(result.getBody(), result.getStatusCode());
        }
    }

    @GetMapping(value = "/method8/{documentGuid}")
    public ResponseEntity<String> method8(@PathVariable("documentGuid") String documentGuid) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getToken(PARTNER_LOGIN, PARTNER_PASSWORD));
        HttpEntity entity = new HttpEntity<>(headers);
        ResponseEntity<String> result;
        try {
            result = restTemplate.exchange(ufosUrl + "/services/func/rest-api/sign/new/reject/" + documentGuid, HttpMethod.POST, entity, String.class);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            return new ResponseEntity<>(new String(ex.getResponseBodyAsByteArray()), ex.getStatusCode());
        }
        if (result.getStatusCode() == HttpStatus.OK) {
            return new ResponseEntity<>(result.getBody(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(result.getBody(), result.getStatusCode());
        }
    }

    @GetMapping(value = "/method9_1/{documentGuid}/{reason}")
    public ResponseEntity<String> method9_1(
            @PathVariable("documentGuid") String documentGuid,
            @PathVariable("reason") String reason
    ) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getToken(PARTNER_LOGIN, PARTNER_PASSWORD));
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.setAccept(Collections.emptyList());
        headers.setAcceptCharset(Collections.emptyList());
        HttpEntity entity = new HttpEntity<>(
                ("{\n" +
                        "\t\"documentGuids\" : [\"" + documentGuid + "\"],\n" +
                        "\t\"lifeCycleParams\" : [\n" +
                        "\t\t{\n" +
                        "\t\t\t\"name\" : \"nonAgreeText\",\n" +
                        "\t\t\t\"value\" : \"" + reason + "\"\n" +
                        "\t\t}"+
                        "\t]\n" +
                        "}\n").getBytes(), headers);
        ResponseEntity<String> result;
        try {
            result = restTemplate.exchange(ufosUrl + "/services/rest-api/core/v1/lifeCycle/ToRefuseCoordination", HttpMethod.POST, entity, String.class);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            return new ResponseEntity<>(new String(ex.getResponseBodyAsByteArray()), ex.getStatusCode());
        }
        if (result.getStatusCode() == HttpStatus.OK) {
            return new ResponseEntity<>(result.getBody(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(result.getBody(), result.getStatusCode());
        }
    }

    @GetMapping(value = "/method9_2/{documentGuid}/{errorText}/{errorType}")
    public ResponseEntity<String> method9_2(
            @PathVariable("documentGuid") String documentGuid,
            @PathVariable("errorText") String errorText,
            @PathVariable("errorType") String errorType
    ) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getToken(AGENT_LOGIN, AGENT_PASSWORD));
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.setAccept(Collections.emptyList());
        headers.setAcceptCharset(Collections.emptyList());
        HttpEntity entity = new HttpEntity<>(
                ("{\n" +
                        "\t\"documentGuids\" : [\"" + documentGuid + "\"],\n" +
                        "\t\"lifeCycleParams\" : [\n" +
                        "\t\t{\n" +
                        "\t\t\t\"name\" : \"errorText\",\n" +
                        "\t\t\t\"value\" : \"" + errorText + "\"\n" +
                        "\t\t},"+
                        "\t\t{\n" +
                        "\t\t\t\"name\" : \"errorType\",\n" +
                        "\t\t\t\"value\" : \"" + errorType + "\"\n" +
                        "\t\t}"+
                        "\t]\n" +
                        "}\n").getBytes(), headers);
        ResponseEntity<String> result;
        try {
            result = restTemplate.exchange(ufosUrl + "/services/rest-api/core/v1/lifeCycle/to_Contract_ErrorDocs", HttpMethod.POST, entity, String.class);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            return new ResponseEntity<>(new String(ex.getResponseBodyAsByteArray()), ex.getStatusCode());
        }
        if (result.getStatusCode() == HttpStatus.OK) {
            return new ResponseEntity<>(result.getBody(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(result.getBody(), result.getStatusCode());
        }
    }

    @GetMapping(value = "/method10/{documentGuid}")
    public ResponseEntity<String> method10(
            @PathVariable("documentGuid") String documentGuid
    ) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getToken(AGENT_LOGIN, AGENT_PASSWORD));
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.setAccept(Collections.<MediaType>emptyList());
        headers.setAcceptCharset(Collections.<Charset>emptyList());
        HttpEntity entity = new HttpEntity<>(
                ("{\n" +
                        "\t\"documentGuids\" : [\"" + documentGuid + "\"],\n" +
                        "\t\"lifeCycleParams\" : [\n" +
                        "\t]\n" +
                        "}\n").getBytes(), headers);
        ResponseEntity<String> result;
        try {
            result = restTemplate.exchange(ufosUrl + "/services/rest-api/core/v1/lifeCycle/DocumentsSubmitted", HttpMethod.POST, entity, String.class);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            return new ResponseEntity<>(new String(ex.getResponseBodyAsByteArray()), ex.getStatusCode());
        }
        if (result.getStatusCode() == HttpStatus.OK) {
            return new ResponseEntity<>(result.getBody(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(result.getBody(), result.getStatusCode());
        }
    }

    @GetMapping(value = "/method11/{documentGuid}")
    public ResponseEntity<String> method11(@PathVariable("documentGuid") String documentGuid) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getToken(AGENT_LOGIN, AGENT_PASSWORD));
        HttpEntity entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> result;
        try {
            result = restTemplate.exchange(ufosUrl + "/services/func/rest-api/sign/reward/start/" + documentGuid, HttpMethod.POST, entity, byte[].class);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            return new ResponseEntity<>(new String(ex.getResponseBodyAsByteArray()), ex.getStatusCode());
        }
        if (result.getStatusCode() == HttpStatus.OK) {
            return new ResponseEntity<>(Base64.getEncoder().encodeToString(result.getBody()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new String(result.getBody()), result.getStatusCode());
        }
    }

    @GetMapping(value = "/method12/{documentGuid}/{login}/{password}")
    public ResponseEntity<String> method12(
            @PathVariable("documentGuid") String documentGuid,
            @PathVariable("login") String login,
            @PathVariable("password") String password
    ) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        try {
            headers.set("Authorization", "Bearer " + getToken(login, password));
        } catch (Throwable thr) {
            return new ResponseEntity<>(thr.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        HttpEntity entity = new HttpEntity<>(headers);
        ResponseEntity<String> result;
        try {
            result = restTemplate.exchange(ufosUrl + "/services/func/rest-api/sign/reward/end/" + documentGuid, HttpMethod.POST, entity, String.class);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            return new ResponseEntity<>(new String(ex.getResponseBodyAsByteArray()), ex.getStatusCode());
        }
        if (result.getStatusCode() == HttpStatus.OK) {
            return new ResponseEntity<>(result.getBody(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(result.getBody(), result.getStatusCode());
        }
    }

    private String getToken(String login, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity<>(getAuthBodyFor(login, password), headers);
        return restTemplate.postForObject(ufosUrl + "/services/rest-api/auth/authorization", entity, String.class);
    }

    private String getAuthBodyFor(String login, String password) {
        return "{ \"login\" : \"" + login + "\", \"password\" : \"" + password + "\" }";
    }
}
