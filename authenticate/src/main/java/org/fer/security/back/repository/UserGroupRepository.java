package org.fer.security.back.repository;

import org.fer.security.back.model.UserGroup;
import org.fer.security.back.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {

    // TODO: Use a Set instead !
    public List<UserGroup> findByTypeAndApplicationAndRole(String type, String application, String role);
    public List<UserGroup> findByApplicationAndTypeAndObject(String application, String type, String object);

    @Query("SELECT userGroup FROM UserGroup userGroup WHERE userGroup.owner = :owner")
    public List<UserGroup> findGroupByOwner(@Param("owner") UserProfile owner);

    @Query("SELECT DISTINCT userGroup FROM UserGroup userGroup JOIN userGroup.users as user WHERE userGroup.owner=:owner OR (userGroup.role = 'Admin' AND user=:owner) ")
    public List<UserGroup> findGroupByAdminOrOwnership(@Param("owner") UserProfile owner);


}
