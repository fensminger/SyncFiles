package org.fer.security.back.controller;

import org.fer.security.back.dto.*;
import org.fer.security.back.model.UserGroup;
import org.fer.security.back.model.UserProfile;
import org.fer.security.back.security.CookieService;
import org.fer.security.back.security.SecurityService;
import org.fer.security.back.service.DataNotFoundException;
import org.fer.security.back.service.MailService;
import org.fer.security.back.service.ReqAppsService;
import org.fer.security.back.service.UserProfileService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import javax.validation.Valid;


@Controller
public class UserController {

    public static Logger LOGGER = Logger.getLogger(UserController.class);

    @Autowired
    //@Autowired
    private UserProfileService userProfileService;

    @Autowired
    @Qualifier("myUserDetailsService")
    private UserDetailsService userDetailService;

    @Autowired
    private ShaPasswordEncoder passwordEncoder;

    @Autowired
    private SaltSource saltSource;

    @Autowired
    private CookieService cookieService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private AuthenticationManager  authenticationManager;

    @Autowired
    private MailService mailService;


    @RequestMapping(value="/register", method=RequestMethod.POST)
	public @ResponseBody
    UserProfileDTO register(@Valid @RequestBody UserProfileDTO userProfileDTO, HttpServletRequest request, HttpServletResponse response) {

        response.addHeader("Access-Control-Allow-Origin","*");
        response.addHeader("Access-Control-Allow-Methods","POST, GET, OPTIONS");
        response.addHeader("Access-Control-Allow-Headers","Content-Type");

        UserProfile up = null;
        UserProfileDTO result = null;

        // password Encoder
        // TODO: set password encoders in a service
        UserDetails tmp = new User(userProfileDTO.getUsername(), userProfileDTO.getPassword(), new HashSet<GrantedAuthority>());
        String salt = (String) saltSource.getSalt(tmp);
        String encodePwd = passwordEncoder.encodePassword(userProfileDTO.getPassword(), salt);
        String origPassword = userProfileDTO.getPassword();
        userProfileDTO.setPassword(encodePwd);

        try {
            up = userProfileService.create(userProfileDTO);
            up.setPassword("****");
            result = constructUSerProfileObject(up);

            String ip = request.getRemoteAddr();
            response.addCookie(cookieService.createCookie(userProfileDTO.getUsername(), origPassword, ip));

        } catch (Exception e) {
            result = new UserProfileDTO();
            result.setStatus("ERR");
            result.setMessage("UserAlreadyExists");
        }

        return result;

	}

    @RequestMapping(value="/lostPassword", method=RequestMethod.POST)
    public @ResponseBody
    void lostPassword(@Valid @RequestBody UserLostPwdDTO userDetailsDTO, HttpServletRequest request, HttpServletResponse response) {

        UserProfile userProfile = userProfileService.findByUsername(userDetailsDTO.getUsername());

        Long validityTime = new Date().getTime() + 3600 * 1000;

        //TODO: set the password change date and build a token with this and he username
        //TODO: find a clean way to get full web address ...
        String ip = request.getRemoteAddr();
        String url = "http://localhost:9000/#/lostPassword/"
                + securityService.buildLostPasswordToken(userProfile.getUsername(), ip, userProfile.getPassword(), validityTime, userProfile.getCredentialChangeTime().getTime());

        LOGGER.debug("Generating url: " + url);
        mailService.sendLostPasswordMail(userProfile, url);

    }

    @RequestMapping(value="/lostPassword/change", method=RequestMethod.POST)
    public @ResponseBody
    void lostPasswordChange(@Valid @RequestBody UserLostPwdDTO userDetailsDT, HttpServletRequest request, HttpServletResponse response) {

        UserDetailsDTO ud = null;

        boolean firstKeyAuth = false;
        String firstKeyErrMsg = null;
        boolean authenticated = false;
        String username = null;
        Long timestamp = null;
        String ip = request.getRemoteAddr();
        String[] tokenInfos = null;

        if (null != userDetailsDT.getToken()) {

            tokenInfos = userDetailsDT.getToken().split(":");
            if (null != tokenInfos && tokenInfos.length == 4) {
                username = tokenInfos[0];
                String timestampStr = tokenInfos[1];
                String token1 = tokenInfos[2];

                try {
                   timestamp = Long.parseLong(timestampStr);
                }
                catch (NumberFormatException e) {
                    firstKeyErrMsg = "Key corrupted .";
                }

                if (null != timestamp)  {
                    long currentTime = new Date().getTime();
                    if (currentTime > timestamp) {
                        firstKeyErrMsg = "Key expired";
                    }

                    else {

                        // TODO: check if a token with timestamp is better
                        String expectedToken1 = securityService.buildToken1(username, ip);
                        if (expectedToken1.equals(token1)) {
                            firstKeyAuth = true;
                        }
                        else {
                            firstKeyErrMsg = "Key corrupted ..";
                        }
                    }
                }
            } else firstKeyErrMsg = "Key corrupted ...";
        }  else firstKeyErrMsg = "Key corrupted ....";

        // Only if the first token check is OK !
        if (firstKeyAuth) {

            // Assume no check because firstKeyAuth ... care in acase of refacto
            username = tokenInfos[0];
            String timestampStr = tokenInfos[1];
            String token3 = tokenInfos[3];

            UserProfile userProfile = userProfileService.findByUsername(userDetailsDT.getUsername());
            String expectedToken3 = securityService.buildToken3(userProfile.getUsername(), ip, userProfile.getPassword(), timestamp, userProfile.getCredentialChangeTime().getTime());

            if (expectedToken3.equals(token3)) {

//                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetailsDT.getUsername(), userProfile.getPassword());
//                Authentication authenticate = authenticationManager.authenticate(token);
//
                  UserDetails userDetails = userDetailService.loadUserByUsername(userDetailsDT.getUsername());
//                // This should trigger Auth Success Handler
//                if (authenticate.isAuthenticated()) {

                    UserDetails tmp = new User(userDetailsDT.getUsername(), userDetailsDT.getPassword(), new HashSet<GrantedAuthority>());
                    String salt = (String) saltSource.getSalt(tmp);
                    String encodePwd = passwordEncoder.encodePassword(userDetailsDT.getPassword(), salt);

                    UserProfileDTO userProfileDTO = constructUSerProfileObject(userProfile);
                    userProfileDTO.setPassword(encodePwd);

                    try {

                        userProfile = userProfileService.update(userProfileDTO);
                        response.addCookie(cookieService.createCookie(userDetailsDT.getUsername(), encodePwd, ip));
                        ud = new UserDetailsDTO(userDetailsDT.getUsername(), "****", userDetails.getAuthorities());
                        mailService.sendChangedPasswordMail(userProfile);


                    } catch (DataNotFoundException e) {


                        ud =  new UserDetailsDTO(userDetailsDT.getUsername(), "****", null);
                        ud.setStatus("ERR");
                        ud.setMessage("ErrWhileChangingPwd");
                        LOGGER.info("ErrWhileChangingPwd ......;");
                    }
//                }
            }
        }

        //

    }


    @RequestMapping(value="/secure/currentUserDetails", method=RequestMethod.GET)
    public @ResponseBody
    UserDetailsDTO checkLogin(HttpServletResponse response) {

        User details = (User) SecurityContextHolder.getContext().getAuthentication().getDetails(); //.getPrincipal();
        UserDetailsDTO ud = new UserDetailsDTO(details.getUsername(), "****", details.getAuthorities());
        return ud;
    }

    @RequestMapping(value="/login", method=RequestMethod.POST)
    public @ResponseBody UserDetailsDTO login(@RequestBody UserLoginDTO userLogin,  HttpServletRequest request, HttpServletResponse response) {

        //TODO: register a success handler !
        //TODO: make auth apart from app classes ;-)

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userLogin.getLogin(), userLogin.getPassword());
        Authentication authenticate = authenticationManager.authenticate(token);

        // TODO: check this !
        UserDetails details = (User) authenticate.getPrincipal();

       String ip = request.getRemoteAddr();
        // This should trigger Auth Success Handler
       if (authenticate.isAuthenticated()) {
           response.addCookie(cookieService.createCookie(userLogin.getLogin(), userLogin.getPassword(), ip));
       }

       UserDetailsDTO ud = new UserDetailsDTO(details.getUsername(), "****", details.getAuthorities());

       return ud;
    }

    @RequestMapping(value="/secure/changePassword", method=RequestMethod.POST)
    public @ResponseBody UserDetailsDTO changePwd(@RequestBody UserChangePwdDTO userLogin,  HttpServletRequest request, HttpServletResponse response) {

        UserProfile userProfile = userProfileService.findByUsername(userLogin.getLogin());

        UserDetailsDTO ud = null;

        // Recheck OldPassword In case of cookie usurpation ?
        // Here: Try to Log-In with OLD Password !
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userLogin.getLogin(), userLogin.getOldPassword());
        Authentication authenticate = authenticationManager.authenticate(token);

        // This should trigger Auth Success Handler
        if (authenticate.isAuthenticated()) {


            String ip = request.getRemoteAddr();


            // Create AUTH Tokens for new AUTH
            UserDetails tmp = new User(userLogin.getLogin(), userLogin.getPassword(), new HashSet<GrantedAuthority>());
            String salt = (String) saltSource.getSalt(tmp);
            String encodePwd = passwordEncoder.encodePassword(userLogin.getPassword(), salt);

            UserProfileDTO userProfileDTO = constructUSerProfileObject(userProfile);
            userProfileDTO.setPassword(encodePwd);

            try {

                userProfile = userProfileService.update(userProfileDTO);
                response.addCookie(cookieService.createCookie(userLogin.getLogin(), encodePwd, ip));

                ud = new UserDetailsDTO(userLogin.getLogin(), "****", authenticate.getAuthorities());


            } catch (DataNotFoundException e) {


                ud =  new UserDetailsDTO(userLogin.getLogin(), "****", authenticate.getAuthorities());
                ud.setStatus("ERR");
                ud.setMessage("ErrWhileChangingPwd");
                LOGGER.info("ErrWhileChangingPwd ......;");
            }
        }

        return ud;
    }

    @RequestMapping(value="/admin/users", method=RequestMethod.POST)
    public @ResponseBody List<UserProfileDTO> adminList(@RequestBody UserProfile userProfile,  HttpServletResponse response) {

        response.addHeader("Access-Control-Allow-Origin","*");
        response.addHeader("Access-Control-Allow-Methods","POST, GET, OPTIONS");
        response.addHeader("Access-Control-Allow-Headers","Content-Type");

        List<UserProfile> userProfiles = userProfileService.findAll();
        List<UserProfileDTO> userProfileDTOs = new ArrayList<UserProfileDTO>();
        for ( UserProfile up: userProfiles) {
            userProfileDTOs.add(constructUSerProfileObject(up));
        }

        return userProfileDTOs;
    }

//    @RequestMapping(value="/test/all", method=RequestMethod.GET)
//    public @ResponseBody List<UserShareDTO> testList(HttpServletResponse response) {
//
//        User details = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();
//
//
//        // Todo: take into account ownership !
//        List<UserShareDTO> userShareDTOs = new ArrayList<UserShareDTO>();
//        // List<UserGroup> userGroups = userProfileService.findAssociatedToGroup("ReqApps", "Project", "wilfried/test1");
//        List<UserGroup> userGroups = userProfileService.findAssociatedToUserAsAdmin(details.getUsername());
//
//
//        Map<String, UserShareDTO> userShareDTOMap = new HashMap<String, UserShareDTO>();
//
//        for ( UserGroup ug: userGroups) {
//
//            UserShareDTO userShareDTO = userShareDTOMap.get(ug.getType() + ":" + ug.getObject());
//            if (null == userShareDTO) {
//                userShareDTO = new UserShareDTO();
//                userShareDTO.setName(ug.getObject());
//                userShareDTO.setType(ug.getType());
//                userShareDTO.setOwnerName(ug.getOwner().getUsername());
//                userShareDTO.setRoles(new HashSet<String>());
//                userShareDTO.setUserRoles(new ArrayList<UserRoleDTO>());
//                userShareDTOMap.put(ug.getType() + ":" + ug.getObject(), userShareDTO);
//            }
//
//            userShareDTO.getRoles().add(ug.getRole());
//
//            for (UserProfile userProfile: ug.getUsers()) {
//                //userProfileDTOs.add(constructUSerProfileObject(userProfile));
//
//                UserRoleDTO ur = new UserRoleDTO();
//                ur.setUsername(userProfile.getUsername());
//                ur.setRole(ug.getRole());
//                userShareDTO.getUserRoles().add(ur);
//            }
//
//
//        }
//
//        userShareDTOs.addAll(userShareDTOMap.values());
//        return userShareDTOs;
//    }

    @RequestMapping(value="/secure/shares", method=RequestMethod.POST)
    public @ResponseBody List<UserShareDTO> shares(@RequestBody UserShareDTO puserShareDTO, HttpServletResponse response) {

        // TODO: use UserShareDTO as a search form

        User details = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();

        // get the list of all the groups the current user is owner or admin
        List<UserGroup> userGroups = userProfileService.findAssociatedToUserAsAdmin(details.getUsername());

        Set<String> roles = new HashSet<String>();
        roles.add("User");
        roles.add("Reader");
        roles.add("Admin");

        // Use a map for perfs
        Map<String, UserShareDTO> userShareDTOMap = new HashMap<String, UserShareDTO>();

        for ( UserGroup ug: userGroups) {
            UserShareDTO userShareDTO = userShareDTOMap.get(ug.getType() + ":" + ug.getObject());
            if (null == userShareDTO) {
                userShareDTO = new UserShareDTO();
                userShareDTO.setName(ug.getObject());
                userShareDTO.setType(ug.getType());
                userShareDTO.setOwnerName(ug.getOwner().getUsername());
                userShareDTO.setRoles(roles);
                userShareDTO.setUserRoles(new ArrayList<UserRoleDTO>());
                userShareDTOMap.put(ug.getType() + ":" + ug.getObject(), userShareDTO);
            }

            //userShareDTO.getRoles().add(ug.getRole());

            for (UserProfile userProfile: ug.getUsers()) {
                //userProfileDTOs.add(constructUSerProfileObject(userProfile));

                UserRoleDTO ur = new UserRoleDTO();
                ur.setUsername(userProfile.getUsername());
                ur.setRole(ug.getRole());
                userShareDTO.getUserRoles().add(ur);
            }


        }

        List<UserShareDTO> userShareDTOs = new ArrayList<UserShareDTO>();
        userShareDTOs.addAll(userShareDTOMap.values());
        return userShareDTOs;

    }

    @RequestMapping(value="/secure/share", method=RequestMethod.POST)
    public @ResponseBody UserShareDTO share(@RequestBody UserShareDTO userShareDTO, HttpServletResponse response) {


        User details = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();

        String app = ReqAppsService.AppName;
        String type = userShareDTO.getType();
        String object = userShareDTO.getName();

        List<UserGroup> userGroups = userProfileService.findAssociatedToGroup("ReqApps", type, object);

        Map<String, List<String>> roleUsers = new HashMap<String, List<String>>();
        for (UserGroup userGroup: userGroups) {
            roleUsers.put(userGroup.getRole(), new ArrayList<String>());
        }

        for (UserRoleDTO ur: userShareDTO.getUserRoles()) {
            List<String> listUserNames =  roleUsers.get(ur.getRole());
            if (null != listUserNames) {
                listUserNames.add(ur.getUsername());
            }
            else {
                LOGGER.warn("Try to associate a user to an unknown group ...");
            }
        }

        userProfileService.updateGroupUsers(userGroups, roleUsers);



        userGroups = userProfileService.findAssociatedToGroup("ReqApps", type, object);
        UserShareDTO result = new UserShareDTO();
        result.setName(object);
        result.setType(type);

        result.setRoles(new HashSet<String>());
        result.setUserRoles(new ArrayList<UserRoleDTO>());

        for ( UserGroup ug: userGroups) {
            result.getRoles().add(ug.getRole());
            result.setOwnerName(ug.getOwner().getUsername());

            for (UserProfile userProfile: ug.getUsers()) {
                //userProfileDTOs.add(constructUSerProfileObject(userProfile));

                UserRoleDTO ur = new UserRoleDTO();
                ur.setUsername(userProfile.getUsername());
                ur.setRole(ug.getRole());
                result.getUserRoles().add(ur);
            }
        }

        return result;
    }


    private UserShareDTO mockUserShareDTO(String name, String ownerName, String type) {
        UserShareDTO userShareDTO = new UserShareDTO();

        userShareDTO.setName(name);
        userShareDTO.setOwnerName(ownerName);
        userShareDTO.setType(type);
        userShareDTO.setCurrentUserRole("OWNER");

        Set<String> roles = new HashSet<String>();
        roles.add("USER");
        roles.add("READER");
        roles.add("ADMIN");
        userShareDTO.setRoles(roles);


        List<UserRoleDTO> userRoles = new ArrayList<UserRoleDTO>();
        for (int i=1; i <= 10; i++) {
            userRoles.add(new UserRoleDTO("user" + i, "USER"));
        }
        userRoles.add(new UserRoleDTO("reader" + 1, "READER"));
        userRoles.add(new UserRoleDTO("me", "ADMIN"));
        userShareDTO.setUserRoles(userRoles);


        return userShareDTO;

    }



    private UserProfileDTO constructUSerProfileObject(UserProfile up) {
        UserProfileDTO formObject = new UserProfileDTO();

        formObject.setId(up.getId());
        formObject.setUsername(up.getUsername());
        formObject.setEmail(up.getEmail());
        formObject.setPassword(up.getPassword());

        return formObject;
    }

//    private UserDetailsDTO constructUserDetailsObject(UserProfile up) {
//        UserDetailsDTO formObject = new UserDetailsDTO();
//
//        formObject.setEmail(up.getEmail());
//        formObject.setLastName("TODO-LastNAme");
//        formObject.setFirstname("TODO-Firstname");
//        formObject.setUserName("TODO-USrname");
//        formObject.setRole("TODO-userRole");
//        //formObject.setPassword(up.getPassword());
//
//        return formObject;
//    }


    public void setUserProfileService(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    public void setUserDetailService(UserDetailsService userDetailService) {
        this.userDetailService = userDetailService;
    }

    public void setCookieService(CookieService cookieService) {
        this.cookieService = cookieService;
    }

}

