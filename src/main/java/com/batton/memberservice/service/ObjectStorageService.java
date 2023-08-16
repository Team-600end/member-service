package com.batton.memberservice.service;

import com.batton.memberservice.common.BaseException;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.batton.memberservice.common.BaseResponseStatus.IMAGE_UPLOAD_ERROR;

@RequiredArgsConstructor
@Service
public class ObjectStorageService {
    @Value("${spring.cloud.kakao.access_key}")
    private String access_key;
    @Value("${spring.cloud.kakao.secret_key}")
    private String secret_key;
    private final String API_TOKEN_URL = "https://iam.kakaoicloud-kr-gov.com/identity/v3/auth/tokens";
    private final String STORAGE_URL = "https://objectstorage.kr-gov-central-1.kakaoicloud-kr-gov.com/v1/72a1e5bc92824b1e85f86463b972eb74/batton/IMAGE";
    private final RestTemplateService restTemplateService;

    public String uploadFile(MultipartFile multipartFile) {
        String fileURL = getFileURL(multipartFile, STORAGE_URL);
        HttpHeaders headers = getApiTokenHeader();
        HttpEntity<String> response;
        response = restTemplateService.uploadFile(fileURL, headers, multipartFile, String.class);

        if(response == null) {
            throw new BaseException(IMAGE_UPLOAD_ERROR);
        }

        return fileURL;
    }

    private String getFileURL(MultipartFile multipartFile, String storageURL) {
        StringBuilder sb = new StringBuilder(storageURL);
        String fileName = UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();
        String fileURL = sb.append("/" + fileName).toString();

        return fileURL;
    }

    private JSONObject getApiTokenBodyObject() {
        JSONObject bodyObject = new JSONObject();
        JSONObject authObject = new JSONObject();
        JSONObject identityObject = new JSONObject();
        List<String> methodList = new ArrayList<>();
        JSONObject credentialObject = new JSONObject();

        methodList.add("application_credential");
        identityObject.put("methods", methodList);
        credentialObject.put("id", access_key);
        credentialObject.put("secret", secret_key);
        identityObject.put("application_credential", credentialObject);
        authObject.put("identity", identityObject);
        bodyObject.put("auth", authObject);

        return bodyObject;
    }

    // API 인증 토큰 발급받기
    private HttpHeaders getApiTokenHeader() {
        JSONObject bodyObject = getApiTokenBodyObject();
        HttpEntity<String> response = restTemplateService.post(API_TOKEN_URL, HttpHeaders.EMPTY, bodyObject, String.class);
        HttpHeaders responseHeaders = response.getHeaders();
        String api_token = responseHeaders.getFirst("X-Subject-Token");
        HttpHeaders apiTokenHeader = new HttpHeaders();

        apiTokenHeader.add("X-Auth-Token", api_token);

        return apiTokenHeader;
    }
}

