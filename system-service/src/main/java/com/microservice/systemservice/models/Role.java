package com.microservice.systemservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "role")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;
    private String roleName;
    private String rolePower;
    private String roleDescribe;
    private Boolean state;
    private Date createdDate;
    private Short gradeId;
    private String roleNameCn;
    private Long roleParentId;
}
