package api.blog.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import api.blog.model.RequestParamDto;
import api.blog.service.BlogService;


@RestController
@RequestMapping("/api")
public class BlogController {

    private BlogService blogService;
    
    public BlogController(BlogService blogService){
        this.blogService = blogService;
    }

    @Value("${api.service.type}")
    private String apiServiceType;

    private static final Integer PAGE_MIN_VALUE =1;
    private static final Integer PAGE_MAX_VALUE = 50;
    private static final Integer SIZE_MIN_VALUE = 1;
    private static final Integer SIZE_MAX_VALUE = 50;

    private static final String INVALID_ARGUMENT = "InvalidArgument";
    private static final String MISSING_PARAMETER = "MissingParameter";
    private static final String QUERY_PARAMETER_REQUIRED = "query parameter required";
    private static final String PAGE_LESS_THAN_MIN = "page is less than min";
    private static final String PAGE_MORE_THAN_MAX = "page is more than max";
    private static final String SIZE_LESS_THAN_MIN = "size is less than min";
    private static final String SIZE_MORE_THAN_MAX = "size is more than max";
    
    @GetMapping("/blog")
    public ResponseEntity<Map<String, Object>> getBlog(@ModelAttribute("RequestParamDto") RequestParamDto requestParamDto){

        if(apiServiceType.equals("kakao")){

            Map<String, Object> errorDataMap = new HashMap<>();
            
            if(requestParamDto.getQuery() == null || requestParamDto.getQuery().equals("")){
                errorDataMap.put("errorType",MISSING_PARAMETER);
                errorDataMap.put("message",QUERY_PARAMETER_REQUIRED);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDataMap);
            }

            if(requestParamDto.getPage() != null && (requestParamDto.getPage() < PAGE_MIN_VALUE || requestParamDto.getPage() > PAGE_MAX_VALUE)){
                errorDataMap.put("errorType",INVALID_ARGUMENT);
                errorDataMap.put("message",requestParamDto.getPage() < PAGE_MIN_VALUE ? PAGE_LESS_THAN_MIN : PAGE_MORE_THAN_MAX);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDataMap);
            }

            if(requestParamDto.getSize() != null && (requestParamDto.getSize() < SIZE_MIN_VALUE || requestParamDto.getSize() > SIZE_MAX_VALUE)){
                errorDataMap.put("errorType",INVALID_ARGUMENT);
                errorDataMap.put("message",requestParamDto.getSize() < SIZE_MIN_VALUE ? SIZE_LESS_THAN_MIN : SIZE_MORE_THAN_MAX);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDataMap);
            }

            requestParamDto.setServiceType("kakao");

            ObjectMapper objectMapper = new ObjectMapper();
            
            Map<String, Object> queryDataMap = objectMapper.convertValue(requestParamDto, Map.class);

            return blogService.getBlogDataList(queryDataMap);

        }else if(apiServiceType.equals("naver")){
            
            requestParamDto.setServiceType("naver");
            
            Map<String, Object> errorDataMap = new HashMap<>();
            
            if(requestParamDto.getQuery() == null || requestParamDto.getQuery().equals("")){
                errorDataMap.put("errorType",MISSING_PARAMETER);
                errorDataMap.put("message",QUERY_PARAMETER_REQUIRED);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDataMap);
            }
            
            ObjectMapper objectMapper = new ObjectMapper();
            
            Map<String, Object> queryDataMap = objectMapper.convertValue(requestParamDto, Map.class);
            
            return blogService.fallbackGetBlogDataList(queryDataMap);

        }else{
            return null;
        }

    }

    
}
