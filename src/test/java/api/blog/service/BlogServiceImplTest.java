package api.blog.service;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import api.blog.repository.BlogQueryRepository;

@SpringBootTest
public class BlogServiceImplTest {

    @Mock
    BlogQueryRepository blogQueryRepository;
    
    @Test
    @DisplayName("updateKeywordSearchCountTest")
    void updateKeywordSearchCountTest() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("query","테스트");
        BlogServiceImpl blogServiceImpl = new BlogServiceImpl(blogQueryRepository);
        blogServiceImpl.updateKeywordSearchCount(map);
    }

}
