package com.twenty.cattlecommuntiy.controller;

import com.twenty.cattlecommuntiy.annotation.LoginRequrie;
import com.twenty.cattlecommuntiy.entity.User;
import com.twenty.cattlecommuntiy.service.FollowService;
import com.twenty.cattlecommuntiy.service.LikeService;
import com.twenty.cattlecommuntiy.service.UserService;
import com.twenty.cattlecommuntiy.util.ActivateConstant;
import com.twenty.cattlecommuntiy.util.CommunityUtil;
import com.twenty.cattlecommuntiy.util.HostHonder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.jws.WebParam;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
@RequestMapping("/user")
public class UserController implements ActivateConstant {

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.domain}")
    private String domain;
    @Value("${community.path.load}")
    private String uploadPath;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Autowired
    private UserService userService;
    @Autowired
    private HostHonder hostHonder;
    @Autowired
    private LikeService likeService;
    @Autowired
    private FollowService followService;
    @LoginRequrie
    @GetMapping("/setting")
    public String getSettingPage(){
        return "/site/setting";
    }

    /**
     *
     * @param headImage
     * @param model
     * @return
     */
    @PostMapping("/upload")
    public String upload(MultipartFile headImage, Model model){
        if(headImage==null){
            model.addAttribute("error","????????????????????????");
            return "/site/setting";
        }
        String imageName = headImage.getOriginalFilename();
        String suffix = imageName.substring(imageName.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("erroe","????????????????????????????????????");
            return "/site/setting";
        }
        //?????????????????????
        imageName = CommunityUtil.genetateUUID()+suffix;
        //????????????????????????
        File file = new File(uploadPath + "/" + imageName);
        try {
            //????????????
            headImage.transferTo(file);
        } catch (IOException e) {
            logger.error("??????????????????"+e.getMessage());
            throw new RuntimeException("??????????????????",e);
        }
        //???????????????????????????(web??????)
        String headerUrl = domain + contextPath + "user/header/" + imageName;
        User user = hostHonder.getUser();
        userService.updateHeaderUrl(user.getId(),headerUrl);

        return "redirect:/index";
    }

    @LoginRequrie
    @GetMapping("/header/{fileName}")
    public void getHeader(@PathVariable("fileName") String filename, HttpServletResponse response){
        //?????????????????????
        filename = uploadPath + "/" + filename;
        String suffix = filename.substring(filename.lastIndexOf("."));
        //????????????
        response.setContentType("/image"+suffix);
        try(
                FileInputStream inputStream = new FileInputStream(filename);
                OutputStream outputStream = response.getOutputStream();
                ){
            byte[] buffer = new byte[1024];
            int b = 0;
            while((b = inputStream.read(buffer)) != -1){
                outputStream.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("error","??????????????????"+e.getMessage());
        }
    }

    @PostMapping("/updatePassword")
    public String updatePassword(Model model, String passwordBefore,String passwordAfter){
        if(passwordBefore==null){
            model.addAttribute("passwordBefore","???????????????");
            return "/site/setting";
        }
        if(passwordAfter==null){
            model.addAttribute("passwordAfter","?????????????????????");
            return "/site/setting";
        }
        //??????????????????
        User user = hostHonder.getUser();
        //????????????????????????????????????
        if(user.getPassword().equals(CommunityUtil.MD5(passwordBefore+user.getSalt()))){//????????????
            //????????????
            String salt = CommunityUtil.genetateUUID().substring(0,4);
            userService.updateSalt(user.getId(),salt);
            userService.updatePassword(user.getId(),CommunityUtil.MD5(passwordAfter+salt));
        }else{
            model.addAttribute("passwordBefore","?????????????????????????????????");
            return "/site/setting";
        }
        return "redirect:/index";
    }
    @GetMapping("/profile/{userId}")
    public String getProfilePage(@PathVariable("userId") int userId, Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("???????????????");
        }
        //??????
        model.addAttribute("user",user);
        //????????????
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",likeCount);

        //????????????
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_USER);
        model.addAttribute("followeeCount",followeeCount);
        //????????????
        long followerCount = followService.findFollowerCount(ENTITY_USER, userId);
        model.addAttribute("followerCount",followerCount);
        //???????????????
        boolean hasFollow = false;
        if(hostHonder.getUser() != null){
            hasFollow = followService.hasFollow(hostHonder.getUser().getId(), ENTITY_USER, userId);
        }
        model.addAttribute("hasFollow",hasFollow);
        return "/site/profile";
    }


}
