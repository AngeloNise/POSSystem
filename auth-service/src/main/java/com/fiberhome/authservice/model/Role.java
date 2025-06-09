package com.fiberhome.authservice.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Table(name = "role")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROLE_ID")
    private Long roleId;
    @Column(name = "NAME_")
    private String roleName;
    @Column(name = "POWER")
    private String rolePower;
    @Column(name = "DESCRIBES")
    private String roleDescribe;
    @Column(name = "STATE")
    private Boolean state;
    @Column(name = "CREATE_DATE")
    private Date createdDate;
    @Column(name = "GRADE_ID")
    private Short gradeId;
    @Column(name = "NAME_CN")
    private String roleNameCn;
    @Column(name = "PARENT_ID")
    private Long roleParentId;
}
