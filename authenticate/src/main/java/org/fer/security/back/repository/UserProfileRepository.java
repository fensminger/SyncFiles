package org.fer.security.back.repository;

import org.fer.security.back.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    public UserProfile findByUsername(String username);
    public UserProfile findByEmail(String email);

    @Query("SELECT user FROM UserProfile user WHERE LOWER(user.email) = LOWER(:email)")
    public UserProfile findByEmailIgnoreCase(@Param("email") String email);

    @Query("SELECT user FROM UserProfile user JOIN user.groups AS g  WHERE g.application=:application AND g.type=:type AND  g.object=:object  ")
    public List<UserProfile> findUsersAssociatedToGroup(@Param("application")String application, @Param("type")String type, @Param("object") String object);



}
