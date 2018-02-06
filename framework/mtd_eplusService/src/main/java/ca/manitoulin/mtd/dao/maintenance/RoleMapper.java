package ca.manitoulin.mtd.dao.maintenance;

import ca.manitoulin.mtd.dto.security.Role;
import ca.manitoulin.mtd.dto.security.RoleAccessRight;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoleMapper {
    List<Role> selectRole(@Param("language") String language);

    Role selectRoleById(Integer roleId);

    void insertRole(Role role);

    void deleteRole(@Param("id") Integer roleId);

    void updateRole(Role role);

    List<Role> selectRolesByUserID(@Param("id") Integer id);
    
    List<RoleAccessRight> selectRoleAccessRightsByUserId(String userId);

    List<RoleAccessRight> selectRoleAccessRight(@Param("id") Integer id);

    void insertRoleAccessRight(RoleAccessRight roleAccessRight);

    void deleteRoleAccessRight(@Param("id") Integer id);

    void deleteRoleAccessRightByRoleId(@Param("roleId") Integer roleId);
    
    List<Role> selectOnlineUserCount(@Param("depId")Integer departmentId, @Param("timeDiff")Integer timeDiffInSeconds);
}
