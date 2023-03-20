package api.blog.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestParamDto {    

    private String query;
    
    private String sort;

    private Integer page;

    private Integer size;

    private Integer display;

    private Integer start;

    private String serviceType;

}
