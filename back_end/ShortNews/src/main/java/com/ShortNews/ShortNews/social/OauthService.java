package com.ShortNews.ShortNews.social;

import com.ShortNews.ShortNews.Token.JwtTokenProvider;
import com.ShortNews.ShortNews.Token.TokenService;
import com.ShortNews.ShortNews.dto.SessionDto;
import com.ShortNews.ShortNews.entity.Member;
import com.ShortNews.ShortNews.entity.Member_tts;
import com.ShortNews.ShortNews.repository.MemberRepository;
import com.ShortNews.ShortNews.repository.Member_ttsRepository;
import com.ShortNews.ShortNews.service.LoginService;
import com.ShortNews.ShortNews.service.MemberService;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class OauthService {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private Member_ttsRepository member_ttsRepository;
    @Autowired
    private MemberService memberService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private TokenService tokenService;

    @Value("${kakao.client.id}")
    private String KAKAO_CLIENT_ID;
    @Value("${kakao.redirect.uri}")
    private String KAKAO_REDIRECT_URI;

    public String getKakaoAccessToken (String code) {
        String access_Token = "";
        String refresh_Token = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=" + KAKAO_CLIENT_ID); // TODO REST_API_KEY 입력
            sb.append("&redirect_uri=" + KAKAO_REDIRECT_URI); // TODO 인가코드 받은 redirect_uri 입력
            sb.append("&code=" + code);
            bw.write(sb.toString());
            bw.flush();

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }

            //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

            br.close();
            bw.close();

            return access_Token;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, Object> createKakaoUser(String token) {
        String reqURL = "https://kapi.kakao.com/v2/user/me";
        Map<String, Object> map = new HashMap<>();
        //access_token을 이용하여 사용자 정보 조회
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + token); //전송할 header 작성, access_token전송

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }

            //Gson 라이브러리로 JSON파싱
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            String id = "K" + element.getAsJsonObject().get("id").toString();
            boolean hasEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();
            String email = "";

            if(hasEmail){
                email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
                List<Member> email_list = memberRepository.findByPhone(email + "K");
                String nick = email.substring(0, email.indexOf('@')) + "K";

                if (email_list.isEmpty()) {
                    map.put("id", id);
                    map.put("phone", email);
                    map.put("nickname", nick);
                    map.put("message", false);  // false면 카테고리 선택
                    return map;
                } else {
                    SessionDto sessionDto = loginService.loginCheck(id, "");
                    String access_token = jwtTokenProvider.createToken(id);
                    String refresh_token = jwtTokenProvider.createRefreshToken(id);
                    tokenService.saveRefreshToken(id, refresh_token);
                    map.put("dto", sessionDto);
                    map.put("access_token", access_token);
                    map.put("refresh_token", refresh_token);
                    map.put("message", true);  // true면 로그인
                    return map;
                }
            }

            br.close();

        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        map.put("message", "에러");
        return map;
    }

///////////////////////////////////////////////////////////////////////////

    @Value("${naver.client.id}")
    private String NAVER_CLIENT_ID;
    @Value("${naver.client.secret}")
    private String NAVER_CLIENT_SECRET;
    @Value("${naver.redirect.uri}")
    private String NAVER_REDIRECT_URL;
    @Value("${naver.auth.uri}")
    private String NAVER_AUTH_URI;
    @Value("${naver.api.uri}")
    private String NAVER_API_URI;

    public String getNaverLogin() {
        return NAVER_AUTH_URI + "/oauth2.0/authorize"
                + "?client_id=" + NAVER_CLIENT_ID
                + "&redirect_uri=" + NAVER_REDIRECT_URL
                + "&response_type=code";
    }

    public String getNaverInfo(String code) throws Exception {
        if (code == null) throw new Exception("Failed get authorization code");

        String accessToken = "";
        String refreshToken = "";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-type", "application/x-www-form-urlencoded");

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type"   , "authorization_code");
            params.add("client_id"    , NAVER_CLIENT_ID);
            params.add("client_secret", NAVER_CLIENT_SECRET);
            params.add("code"         , code);
            params.add("redirect_uri" , NAVER_REDIRECT_URL);
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    NAVER_AUTH_URI + "/oauth2.0/token",
                    HttpMethod.POST,
                    httpEntity,
                    String.class
            );
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObj = (JSONObject) jsonParser.parse(response.getBody());

            accessToken  = (String) jsonObj.get("access_token");
            refreshToken = (String) jsonObj.get("refresh_token");
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("API call failed");
        }
        return accessToken;
    }

    public Map<String, Object> getUserInfoWithToken(String accessToken) throws Exception {
        //HttpHeader 생성
        Map<String, Object> map = new HashMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //HttpHeader 담기
        RestTemplate rt = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = rt.exchange(
                NAVER_API_URI + "/v1/nid/me",
                HttpMethod.POST,
                httpEntity,
                String.class
        );
        //Response 데이터 파싱
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObj    = (JSONObject) jsonParser.parse(response.getBody());
        JSONObject account = (JSONObject) jsonObj.get("response");

        String id = "N" + account.get("id");
        String email = String.valueOf(account.get("email"));
        String nick = email.substring(0, email.indexOf('@')) + "N";
        List<Member> email_list = memberRepository.findByPhone(email + "N");
        if (email_list.isEmpty()) {
            map.put("id", id);
            map.put("phone", email);
            map.put("nickname", nick);
            map.put("message", false);  // false면 카테고리 선택
            return map;
        } else {
            SessionDto sessionDto = loginService.loginCheck(id, "");
            String access_token = jwtTokenProvider.createToken(id);
            String refresh_token = jwtTokenProvider.createRefreshToken(id);
            tokenService.saveRefreshToken(id, refresh_token);
            map.put("dto", sessionDto);
            map.put("access_token", access_token);
            map.put("refresh_token", refresh_token);
            map.put("message", true);  // true면 로그인
            return map;
        }
    }

    public Map<String, Object> oauthAppLogin(String id, String email, String platform) throws NoSuchAlgorithmException {
        List<Member> email_list = memberRepository.findByPhone(email);
        String nick = email.substring(0, email.indexOf('@')) + platform;
        Map<String, Object> map = new HashMap<>();
        if (email_list.isEmpty()) {
            map.put("id", id);
            map.put("phone", email);
            map.put("nickname", nick);
            map.put("message", false);
        } else {
            SessionDto sessionDto = loginService.loginCheck(id, "");
            String access_token = jwtTokenProvider.createToken(id);
            String refresh_token = jwtTokenProvider.createRefreshToken(id);
            map.put("dto", sessionDto);
            map.put("access_token", access_token);
            map.put("refresh_token", refresh_token);
            map.put("message", true);  // true면 로그인
        }
        return map;
    }

////////////////////////////////////////////////////////////
    @Value("${google.client.id}")
    private String GOOGLE_CLIENT_ID;
    @Value("${google.client.pw}")
    private String GOOGLE_CLIENT_PW;
    @Value("${google.redirect.uri}")
    private String GOOGLE_REDIRECT_URI;
    public String getGoogleAccessToken(String authCode) {
        RestTemplate restTemplate = new RestTemplate();
        GoogleRequest googleOAuthRequestParam = GoogleRequest
                .builder()
                .clientId(GOOGLE_CLIENT_ID)
                .clientSecret(GOOGLE_CLIENT_PW)
                .code(authCode)
                .redirectUri(GOOGLE_REDIRECT_URI)
                .grantType("authorization_code").build();

        ResponseEntity<GoogleResponse> resultEntity = restTemplate.postForEntity("https://oauth2.googleapis.com/token",
                googleOAuthRequestParam, GoogleResponse.class);

        return Objects.requireNonNull(resultEntity.getBody()).getId_token();
    }
    public Map<String, Object> googleInfo(String token) throws NoSuchAlgorithmException {
        Map<String, String> tmp = new HashMap<>();
        RestTemplate restTemplate = new RestTemplate();
        tmp.put("id_token",token);
        ResponseEntity<GoogleInfResponse> resultEntity2 = restTemplate.postForEntity("https://oauth2.googleapis.com/tokeninfo",
                tmp, GoogleInfResponse.class);

        String email= Objects.requireNonNull(resultEntity2.getBody()).getEmail();
        String id = "G" + resultEntity2.getBody().getSub();
        String nickname = email.substring(0, email.indexOf("@")) + "G";

        Map<String, Object> map = new HashMap<>();
        List<Member> email_list = memberRepository.findByPhone(email + "G");
        if (email_list.isEmpty()) {
            map.put("id", id);
            map.put("phone", email);
            map.put("nickname", nickname);
            map.put("message", false);  // false면 카테고리 선택
            return map;
        } else {
            SessionDto sessionDto = loginService.loginCheck(id, "");
            String access_token = jwtTokenProvider.createToken(id);
            String refresh_token = jwtTokenProvider.createRefreshToken(id);

            map.put("dto", sessionDto);
            map.put("access_token", access_token);
            map.put("refresh_token", refresh_token);
            map.put("message", true);  // true면 로그인
            return map;
        }
    }

    public String googleLoginUrl() {
        return "https://accounts.google.com/o/oauth2/v2/auth?" +
                "client_id=" + GOOGLE_CLIENT_ID +
                "&redirect_uri=" + GOOGLE_REDIRECT_URI +
                "&response_type=code&scope=email%20profile%20openid&access_type=offline";
    }
}
