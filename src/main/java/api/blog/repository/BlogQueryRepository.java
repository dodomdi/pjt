package api.blog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import api.blog.model.BlogQueryDto;

public interface BlogQueryRepository extends JpaRepository<BlogQueryDto, Long>{
    
    public Optional<BlogQueryDto> findByQuery(String query);

    public List<BlogQueryDto> findAllByOrderByCountDesc();
}
