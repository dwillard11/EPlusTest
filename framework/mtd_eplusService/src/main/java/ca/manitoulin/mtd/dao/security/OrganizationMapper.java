package ca.manitoulin.mtd.dao.security;

import ca.manitoulin.mtd.dto.security.Organization;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrganizationMapper {
    List<Organization> selectDepartment(@Param("language") String language);

    Organization selectDepartmentById(Integer departmentId);

    void insertDepartment(Organization organization);

    void deleteDepartment(@Param("id") Integer departmentId);

    void updateDepartment(Organization organization);
    
    List<Organization> selectDepartmentsByUser(Integer userId);
}
