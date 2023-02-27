package com.twenty.cattlecommuntiy.controller;

import com.google.code.kaptcha.Producer;
import com.twenty.cattlecommuntiy.config.KaptchaConfig;
import com.twenty.cattlecommuntiy.entity.User;
import com.twenty.cattlecommuntiy.service.LoginTicketService;
import com.twenty.cattlecommuntiy.service.UserService;
import com.twenty.cattlecommuntiy.util.ActivateConstant;
import com.twenty.cattlecommuntiy.util.CommunityUtil;
import com.twenty.cattlecommuntiy.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController implements ActivateConstant {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private Producer kaptchaProducer;
    @Autowired
    private LoginTicketService loginTicketService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @GetMapping("/register")
    public String getRegisterPage() {
        return "/site/register";
    }
    @RequestMapping(path = "/login",method = RequestMethod.GET)
    public String getLoginPage() {
        return "/site/login";
    }

    /**
     * 反馈注册提示信息
     * @param model 通过键值对的方式向前端返回数据
     * @param user
     * @return
     */
    @PostMapping("/register")
    public String register(Model model, User user) {
        Map<String, Object> map = userService.register(user);
        if (map == null || map.isEmpty()) {//注册成功
            model.addAttribute("msg", "注册成功，请去邮箱文件中激活账号");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        } else {//注册失败
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";
        }
    }

    /**
     * 账户激活
     * @param model
     * @param userId
     * @param code
     * @return
     */
    @GetMapping(path = "/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code){
        int result = userService.activation(userId, code);
        if(result==ACTIVATE_SUCCESS){
            model.addAttribute("msg", "激活成功");
            model.addAttribute("target", "/login");
        }else if(result==ACTIVATE_REPLAY){
            model.addAttribute("msg", "该账户已经激活过了");
            model.addAttribute("target", "/index");
        }else{
            model.addAttribute("msg", "激活失败");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }

    /**
     * 验证码
     * @param response
     * @param session
     */
    @GetMapping("/kaptcha")
    public void getKaptch(HttpServletResponse response, HttpSession session){
        //生成验证码
        String text = kaptchaProducer.createText();
        //生成验证码图片
        BufferedImage image = kaptchaProducer.createImage(text);

        //将验证吗存入session
        //session.setAttribute("kaptcha",text);
        //使用redis存储验证码
        //1.验证码归属
        String katchaOwner = CommunityUtil.genetateUUID();
        Cookie cookie = new Cookie("katchaOwner",katchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);
        //2.将验证码存入redis
        String kaptchaKey = RedisKeyUtil.getKaptchaKey(katchaOwner);
        redisTemplate.opsForValue().set(kaptchaKey, text, 60, TimeUnit.SECONDS);
        //将图片输入给浏览器
        response.setContentType("image/png");
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            ImageIO.write(image,"png",outputStream);
        } catch (IOException e) {
            logger.error("验证码相应失败"+e.getMessage());
        }

    }

    /**
     * 用户登入
     * @param username
     * @param password
     * @param code
     * @param remember
     * @param model
     * @param response
     * @param katchaOwner
     * @return
     */
    @RequestMapping (path = "/login", method = RequestMethod.POST)
    public String Login(String username, String password, String code, boolean remember,
                        Model model, /*HttpSession session,*/HttpServletResponse response,
                        @CookieValue("katchaOwner") String katchaOwner){
        //检查验证码
        //String kaptcha = (String)session.getAttribute("kaptcha");
        String kaptcha = null;
        if(StringUtils.isNotBlank(katchaOwner)){
            String kaptchaKey = RedisKeyUtil.getKaptchaKey(katchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(kaptchaKey);
        }
        if(StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg","验证码错误");
            return "/site/login";
        }
        //检查账号，密码
        int expiredSecond = remember ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = loginTicketService.login(username, password, expiredSecond);
        if(map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSecond);
            response.addCookie(cookie);
            return "redirect:/index";
        }else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/login";
        }
    }

    /**
     * 退出
     * @param ticket
     * @return
     */
    @GetMapping("/layout")
    public String layout(@CookieValue("ticket") String ticket){
        loginTicketService.loginout(ticket);
        return "redirect:/login";
    }
}
