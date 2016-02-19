package org.fer.security.back.service;

import org.fer.security.back.dto.UserProfileDTO;
import org.fer.security.back.model.UserGroup;
import org.fer.security.back.model.UserProfile;

import java.util.List;
import java.util.Map;

public interface UserProfileService {



    public UserProfile create(UserProfileDTO created);
    public UserProfile delete(Long personId) throws DataNotFoundException;  // throws not found exception ...
    public List<UserProfile> findAll();
    public UserProfile findById(Long id);
    public UserProfile findByUsername(String username);
    public UserProfile update(UserProfileDTO updated) throws DataNotFoundException;  // throws not found exception ...

    public List<UserGroup> findAssociatedToGroup(String app, String type, String object);
    public List<UserGroup> findAssociatedToUserAsAdmin(String username);

    public void updateGroupUsers(List<UserGroup> userGroups, Map<String, List<String>> roleUsers);

}
