package api.blog.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import api.blog.model.BlogQueryDto;
import api.blog.repository.BlogQueryRepository;

@Service
public class BlogServiceImpl implements BlogService{

    private static final Integer INITIAL_COUNT = 1;

    public BlogServiceImpl(BlogQueryRepository blogQueryRepository){
        this.blogQueryRepository = blogQueryRepository;
    }
    
    @Value("${api.key}")
    private String apiKey;
    
    @Value("${api.service.url}")
    private String apiServiceUrl;
    
    private BlogQueryRepository blogQueryRepository;

    @Override
    public ResponseEntity<Map<String, Object>> getBlogDataList(Map<String, Object> queryDataMap) {

        updateKeywordSearchCount(queryDataMap);

        try {

            RestTemplate restTemplate = new RestTemplate();

            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(apiServiceUrl)
                .queryParam("query",queryDataMap.get("query"))
                .queryParam("sort",queryDataMap.get("sort"))
                .queryParam("page",queryDataMap.get("page"))
                .queryParam("size",queryDataMap.get("size"));

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", "KakaoAK "+apiKey);
            HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

            ResponseEntity<String> response = restTemplate.exchange(
                uriComponentsBuilder.toUriString(),
                HttpMethod.GET,
                httpEntity,
                String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readValue(
                response.getBody(),
                JsonNode.class);

            Map<String, Object> resultData = new HashMap<>();
            resultData.put("meta", jsonNode.get("meta"));
            resultData.put("documents", jsonNode.get("documents"));
            return ResponseEntity.ok().body(resultData);

        } catch (JsonMappingException e) {

            e.printStackTrace();
            return ResponseEntity.internalServerError().build();

        } catch (JsonProcessingException e) {

            e.printStackTrace();
            return ResponseEntity.internalServerError().build();

        } catch (HttpServerErrorException e){

            // 서버 장애 발생 시, NAVER API 호출
            return fallbackGetBlogDataList(queryDataMap);

        }
        
    }

    @Override
    @Transactional
    public void updateKeywordSearchCount(Map<String, Object> queryDataMap) {
        
        String query = queryDataMap.get("query").toString();
        Optional<BlogQueryDto> blogQuery= blogQueryRepository.findByQuery(query);

        if(!blogQuery.isPresent()){
            initializeKeywordSearchCount(query);
        }else{
            increaseKeywordSearchCount(blogQuery.get());
        }
        
    }

    @Override
    public void initializeKeywordSearchCount(String query) {
        blogQueryRepository.save(BlogQueryDto.builder().query(query).version(1L).count(INITIAL_COUNT).build());   
    }

    @Override
    public void increaseKeywordSearchCount(BlogQueryDto blogQuery) {
        int new_cnt = blogQuery.getCount()+1;
        blogQuery.setCount(new_cnt);
        blogQueryRepository.save(blogQuery);
    }

    @Override
    public ResponseEntity<Map<String, Object>> fallbackGetBlogDataList(Map<String, Object> queryDataMap) {
        
        try {

            RestTemplate restTemplate = new RestTemplate();

            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString("https://openapi.naver.com/v1/search/blog.json")
                .queryParam("query",queryDataMap.get("query"));

                if(queryDataMap.get("display") != null)
                    uriComponentsBuilder.queryParam("display",queryDataMap.get("display"));

                if(queryDataMap.get("start") != null)
                    uriComponentsBuilder.queryParam("start",queryDataMap.get("start"));

                if(queryDataMap.get("sort") != null)
                    uriComponentsBuilder.queryParam("sort",queryDataMap.get("sort"));


            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("X-Naver-Client-Id", "");
            httpHeaders.add("X-Naver-Client-Secret", "");
            HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

            ResponseEntity<String> response = restTemplate.exchange(
                uriComponentsBuilder.toUriString(),
                HttpMethod.GET,
                httpEntity,
                String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readValue(
                response.getBody(),
                JsonNode.class);

            Map<String, Object> resultData = new HashMap<>();
            resultData.put("lastBuildDate", jsonNode.get("lastBuildDate"));
            resultData.put("total", jsonNode.get("total"));
            resultData.put("start", jsonNode.get("start"));
            resultData.put("display", jsonNode.get("display"));
            resultData.put("items", jsonNode.get("items"));
            return ResponseEntity.ok().body(resultData);

        } catch (Exception e) {

            e.printStackTrace();
            return ResponseEntity.internalServerError().build();

        }
    }

}

