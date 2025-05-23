package org.ex.back.domain.owner.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ex.back.domain.owner.dto.*;
import org.ex.back.domain.owner.service.OwnerService;
import org.ex.back.global.error.CustomException;
import org.ex.back.global.error.ErrorCode;
import org.ex.back.global.jwt.TokenResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;


@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/auth/owner")
public class OwnerController {

    @Value("${openApi.serviceKey}")
    private String openApiKey;

    @Value("${portone.api.key}")
    private String portoneApiKey;

    @Value("${portone.api.secret}")
    private String portoneApiSecret;

    private final OwnerService ownerService;

    // 사업자 번호 조회
    @PostMapping("/brn")
    public ResponseEntity<CheckBRNResponseDto> checkBRN(@RequestBody CheckBRNRequestDto request) throws Exception {

        String BRN = request.getBrn().replace("-", "");
        log.info(BRN);

        String url = "https://api.odcloud.kr/api/nts-businessman/v1/status?" +
                "serviceKey=" + openApiKey +
                "&returnType=" + "json";

        // JsonObject 생성
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(BRN);
        jsonObject.add("b_no", jsonArray);

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // HttpEntity 생성
        HttpEntity<String> entity = new HttpEntity<>(jsonObject.toString(), headers);

        // POST 요청
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, entity, String.class);

        if(!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new Exception(responseEntity.getStatusCode() + ": 사업자등록번호를 조회할 수 없습니다."); //TODO noh 예외처리
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseEntity.getBody()).get("data").get(0);
        CheckBRNResponseDto dto = validateBRN(jsonNode);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    // 사업자 번호 유효성 검사
    private CheckBRNResponseDto validateBRN(JsonNode jsonNode) {
        String tax_type = jsonNode.get("tax_type").asText();
        String b_stt = jsonNode.get("b_stt").asText();

        log.info("tax_type: " + tax_type + ", b_stmt: " + b_stt);

        boolean isValid = false;
        String info;

        if(tax_type.equals("국세청에 등록되지 않은 사업자등록번호입니다.")) {
            info = "국세청에 등록되지 않은 사업자등록번호입니다.";
        } else {
            if(b_stt.equals("계속사업자")){
                isValid = true;
                info = "유효한 사업자등록번호입니다.";
            } else {
                info = "사업자등록번호가 휴업 및 폐업 상태입니다.";
            }
        }

        return CheckBRNResponseDto.builder().isValid(isValid).info(info).build();
    }

    // 계좌번호 본인 확인
    @PostMapping("/bank")
    public ResponseEntity<BankHolderResponseDto> checkBank(@RequestBody BankHolderRequestDto request) throws Exception {

        /*
            1. access token 받아오기
        */

        String accessTokenUrl = "https://api.iamport.kr/users/getToken";

        // JsonObject 생성
        JsonObject jsonObject1 = new JsonObject();
        jsonObject1.addProperty("imp_key", portoneApiKey);
        jsonObject1.addProperty("imp_secret", portoneApiSecret);

        // 헤더 설정
        HttpHeaders headers1 = new HttpHeaders();
        headers1.setContentType(MediaType.APPLICATION_JSON);

        // HttpEntity 생성
        HttpEntity<String> entity1 = new HttpEntity<>(jsonObject1.toString(), headers1);

        // POST 요청
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity1 = restTemplate.postForEntity(accessTokenUrl, entity1, String.class);

        if(!responseEntity1.getStatusCode().is2xxSuccessful()) {
            log.error("계좌번호 조회 API - access token을 발급받지 못했습니다.");
            throw new CustomException(ErrorCode.BANK_NOT_FOUND);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode1 = objectMapper.readTree(responseEntity1.getBody()).get("response");
        String accessToken = jsonNode1.get("access_token").asText();

        /*
            2. 계좌 정보 받아오기
        */

        String bankInfoUrl = "https://api.iamport.kr/vbanks/holder"
                + "?bank_code=" + request.getBank_code()
                + "&bank_num=" + request.getBank_num();

        // header 설정
        HttpHeaders headers2 = new HttpHeaders();
        headers2.setContentType(MediaType.APPLICATION_JSON);
        headers2.set("Authorization", "Bearer " + accessToken);

        // HttpEntity 생성
        HttpEntity<String> entity2 = new HttpEntity<>(headers2);

        // GET 요청
        ResponseEntity<String> responseEntity2;

        try {
            responseEntity2 = restTemplate.exchange(bankInfoUrl, HttpMethod.GET, entity2, String.class);
        } catch (Exception e) {
            log.error("계좌번호 조회 API - 계좌 정보를 조회하지 못했습니다.");
            throw new CustomException(ErrorCode.BANK_NOT_FOUND);
        }

        JsonNode jsonNode = objectMapper.readTree(responseEntity2.getBody());
        String holderName = jsonNode.get("response").get("bank_holder").asText();

        return new ResponseEntity<>(BankHolderResponseDto.builder().name(holderName).build(), HttpStatus.OK);
    }

    // 회원가입
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody OwnerSignUpRequestDto request) {
        ownerService.signUp(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody OwnerLoginRequestDto request
    ) throws Exception
    {
        TokenResponseDto tokenDto = ownerService.login(request);
        log.info(tokenDto.getAccessToken());
        log.info(tokenDto.getRefreshToken());

        // 헤더에 token 정보 추가
        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", createCookie("accessToken", tokenDto.getAccessToken()).toString());
        headers.add("Set-Cookie", createCookie("refreshToken", tokenDto.getRefreshToken()).toString());

        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    private ResponseCookie createCookie(String key, String value) {
        return ResponseCookie.from(key, value)
                .sameSite("")
                .path("/")
                .build();
    }

    // 토큰 재발급
    @PostMapping("/reissue")
    public ResponseEntity<?> reissueToken(HttpServletRequest request) throws Exception {

        // Header에 Token 있는지 검사
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION); //Bearer 2fsd5...
        String refreshTokenHeader = request.getHeader("Refresh-Token"); //2fsd5...
        if (Objects.isNull(authorizationHeader) || Objects.isNull(refreshTokenHeader)) {
            throw new CustomException(ErrorCode.TOKEN_NOT_FOUND);
        }

        // Token 검증 후 새로운 토큰 발급
        String accessToken;
        try {
            accessToken = authorizationHeader.substring(7);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.TOKEN_NOT_FOUND);
        }

        TokenResponseDto tokenDto = ownerService.reissueToken(accessToken, refreshTokenHeader);

        // 헤더에 생성한 token 정보 추가
        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", createCookie("accessToken", tokenDto.getAccessToken()).toString());
        headers.add("Set-Cookie", createCookie("refreshToken", tokenDto.getRefreshToken()).toString());

        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}
