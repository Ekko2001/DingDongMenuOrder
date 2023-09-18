package com.yakusa.reggie.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yakusa.reggie.common.R;
import com.yakusa.reggie.entity.User;
import com.yakusa.reggie.service.UserService;
import com.yakusa.reggie.utils.SMSUtils;
import com.yakusa.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;


    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //1.获取手机号
        String phone = user.getPhone();
        //2.判断手机号是否为空
        if (StringUtils.isNotEmpty(phone)){
        //3.生成随机验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("发送的验证码为:{}",code);
        //4.调用阿里云提供的短信服务API完成发送短信

          //  SMSUtils.sendMessage("阿里云短信测试","SMS_154950909","18775083685",code);


        //5.将验证码存入session
        //session.setAttribute("phone",code);

        //6.将验证码存入redis
        redisTemplate.opsForValue().set(phone,code,5,java.util.concurrent.TimeUnit.MINUTES);


        return R.success("手机验证码发送成功");
        }
        return R.error("短信发送失败");
    }


@PostMapping("/login")
    public R<User> login(@RequestBody Map map,HttpSession session){
        //获取手机号
        String phone = (String) map.get("phone");//用户输入的手机号
        //获取验证码
        String code = (String) map.get("code");//用户输入的验证码

        //获取session中的验证码,并判断(页面提交的验证码和Session中保存的验证码是否一致)
        //String sessionCode = (String) session.getAttribute("phone");//Session中保存的验证码

        //获取redis中的验证码,并判断(页面提交的验证码和redis中保存的验证码是否一致)
        String sessionCode =  (String)  redisTemplate.opsForValue().get(phone);


        if (StringUtils.isEmpty(sessionCode) && !sessionCode.equals(code)  ){
            return R.error("验证码错误");
        }
        //select * from user where phone = #{phone}
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone,phone);
        User user = userService.getOne(wrapper);
        if (user == null){
         //判断当前手机号是否为新用户,如果是新用户,则将手机号保存到数据库中
            User newUser = new User();
            newUser.setPhone(phone);
            newUser.setStatus(1);
            userService.save(newUser);
        }
        session.setAttribute("user",user.getId());//将用户id保存到session中

        redisTemplate.delete(phone);//登录成功后,删除redis中的验证码
        return R.success(user);
    }

    //用户登出
    @PostMapping("/loginout")
    public R<String> loginout(HttpServletRequest request){
        //清理Session中保存的当前用户登录的id
        request.getSession().removeAttribute("user");
        return R.success("退出成功");
    }




}
