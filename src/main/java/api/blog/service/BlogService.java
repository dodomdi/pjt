package api.blog.service;

import java.util.Map;
import org.springframework.http.ResponseEntity;

import api.blog.model.BlogQueryDto;

public interface BlogService {

    ResponseEntity<Map<String, Object>> getBlogDataList(Map<String, Object> queryDataMap);

    ResponseEntity<Map<String, Object>> fallbackGetBlogDataList(Map<String, Object> queryDataMap);

    void updateKeywordSearchCount(Map<String, Object> queryDataMap); 

    void initializeKeywordSearchCount(String query);

    void increaseKeywordSearchCount(BlogQueryDto blogQuery);

}