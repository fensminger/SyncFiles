package org.fer.security.back.service;

import org.fer.security.back.dto.UserProfileDTO;
import org.fer.security.back.model.UserGroup;
import org.fer.security.back.model.UserProfile;
import org.fer.security.back.repository.UserGroupRepository;
import org.fer.security.back.repository.UserProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service(value = "userProfileService")
public class UserProfileServiceImpl implements UserProfileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserProfileServiceImpl.class);

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Transactional
    @Override
    public UserProfile create(UserProfileDTO created) {
        LOGGER.debug("Creating a new person with information: " + created);

        UserProfile userProfile = UserProfile.getBuilder(created.getUsername(), created.getEmail(), created.getPassword()).build();
        UserProfile result =  userProfileRepository.save(userProfile);


        List<UserGroup> userGroups = userGroupRepository.findByTypeAndApplicationAndRole("Application", ReqAppsService.AppName, "User");
        UserGroup userGroup = null;

        if (userGroups == null || userGroups.size() == 0) {
            userGroup = userGroupRepository.save(UserGroup.getBuilder(ReqAppsService.AppName, "Application", "User", null, null).build());
            userGroups = new ArrayList<UserGroup>();
            userGroups.add(userGroup);

            List<UserProfile> users =  userGroup.getUsers();
            if (null == users) {
                userGroup.setUsers(new ArrayList<UserProfile>());
            }
            userGroup.getUsers().add(result);
            userGroupRepository.save(userGroup);
        }

        // TODO Check case of multiple results, or manage better unicity on groups ;-)
        else  {
            userGroup = userGroups.iterator().next();
            userGroup.getUsers().add(result);
            userGroups = new ArrayList<UserGroup>();
            userGroups.add(userGroup);
            userGroupRepository.save(userGroup);
        }
        //UserRole userRole = UserRole.getBuilder("USER", userGroup).build();

        result.setGroups(userGroups);

        return result;

    }

    @Transactional(rollbackFor = DataNotFoundException.class)
    @Override
    public UserProfile delete(Long personId) throws DataNotFoundException {
        LOGGER.debug("Deleting person with id: " + personId);

        UserProfile deleted = userProfileRepository.findOne(personId);

        if (deleted == null) {
            LOGGER.debug("No person found with id: " + personId);
            throw new DataNotFoundException();
        }

        userProfileRepository.delete(deleted);
        return deleted;
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserProfile> findAll() {
        LOGGER.debug("Finding all persons");
        return userProfileRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public UserProfile findById(Long id) {
        LOGGER.debug("Finding userProfile by id: " + id);
        return userProfileRepository.findOne(id);
    }

    @Transactional(readOnly = true)
    @Override
    public UserProfile findByUsername(String username) {
        LOGGER.debug("Finding userProfile by username: " + username);
        return userProfileRepository.findByUsername(username);
    }

    @Transactional(rollbackFor = DataNotFoundException.class)
    @Override
    public UserProfile update(UserProfileDTO updated) throws DataNotFoundException {
        LOGGER.debug("Updating person with information: " + updated);

        UserProfile person = userProfileRepository.findOne(updated.getId());

        if (person == null) {
            LOGGER.debug("No person found with id: " + updated.getId());
            throw new DataNotFoundException();
        }
        if (! person.getPassword().equals(updated.getPassword())) {
            Date now = new Date();
            person.setCredentialChangeTime(now);
        }

        person.update(updated.getUsername(), updated.getEmail(), updated.getPassword());
        userProfileRepository.save(person);

        return person;
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserGroup> findAssociatedToGroup(String app, String type, String object) {

        List<UserGroup> groups = userGroupRepository.findByApplicationAndTypeAndObject(app, type, object);
        // Lazy Load users
        for (UserGroup group: groups) {
            List<UserProfile> userProfiles = group.getUsers();
            for (UserProfile userProfile: userProfiles) {
                System.out.println("User Profile: " + userProfile.getId());
            }
        }
        return groups;
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserGroup> findAssociatedToUserAsAdmin(String username) {

        UserProfile up =  userProfileRepository.findByUsername(username);
//        List<UserGroup> userGroups = up.getGroups();
//        List<UserGroup> adminGroups = new ArrayList<UserGroup>();
//
//        for (UserGroup userGroup: userGroups) {
//            if ("Admin".equals(userGroup.getRole())) {
//                adminGroups.add(userGroup);
//            }
//        }

        // TODO: join with user as admin
        List<UserGroup> groups = userGroupRepository.findGroupByAdminOrOwnership(up);
        // Lazy Load users
        for (UserGroup group: groups) {
            List<UserProfile> userProfiles = group.getUsers();
            for (UserProfile userProfile: userProfiles) {
                System.out.println("User Profile: " + userProfile.getId());
            }
        }
        return groups;
    }

    @Transactional(readOnly = true)
    @Override
    public void updateGroupUsers(List<UserGroup> userGroups, Map<String, List<String>> roleUsers) {


        Map<String, UserGroup> userGroupMapByRole = new HashMap<String, UserGroup>();

        Map<String, UserProfile> knownUsers = new HashMap<String, UserProfile>();
        for (UserGroup ug: userGroups) {
            userGroupMapByRole.put(ug.getRole(), ug);
            for (UserProfile up: ug.getUsers()) {
                knownUsers.put(up.getUsername(), up);
            }
        }


        // Check missing users
        List<String> usersToSearch = new ArrayList<String>();
        for (List<String> userNameList: roleUsers.values()) {
            for (String username: userNameList) {
                 if (! knownUsers.containsKey(username)) {
                     usersToSearch.add(username);
                 }
            }
        }

        // TODO: search users from a list for PERF
        for (String username: usersToSearch) {
            knownUsers.put(username, findByUsername(username));
        }



        for (Map.Entry<String, List<String>> entry: roleUsers.entrySet()) {

            String groupRole = entry.getKey();
            List<String> userNameList = entry.getValue();

            UserGroup ug = userGroupMapByRole.get(groupRole);
            ug.setUsers(new ArrayList());


            for (String username: userNameList) {
                UserProfile up = knownUsers.get(username);
                ug.getUsers().add(up);
                //up.getGroups().add(ug);
            }
        }

        userGroupRepository.save(userGroups);
        userGroupRepository.flush();

    }

    protected void setUserProfileRepository(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    protected void setUserGroupRepository(UserGroupRepository userGroupRepository) {
        this.userGroupRepository = userGroupRepository;
    }
}
