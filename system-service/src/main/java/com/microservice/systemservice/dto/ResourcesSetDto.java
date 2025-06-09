package com.microservice.systemservice.dto;
import com.microservice.systemservice.models.Resources;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.OneToMany;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResourcesSetDto implements Comparable<Resources>{
    private Long resourcesId;
    private Long parentId;
    private String resourceName;
    private String resourceValue;
    private short resourceGrade;
    private String iconUrl;
    private long resourceSort;
    private Long realSubCount;

    @OneToMany
    private Set<Resources> realSubResoSet;

    private Long roleId;

    public Long getResourceSort() {
        return resourceSort;
    }
    public int compareTo(Resources resources) {
        return Integer.parseInt(String.valueOf((getResourceSort()-resources.getResourceSort())));
    }
}
