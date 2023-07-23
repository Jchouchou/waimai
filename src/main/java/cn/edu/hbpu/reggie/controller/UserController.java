package cn.edu.hbpu.reggie.controller;

import cn.edu.hbpu.reggie.common.R;
import cn.edu.hbpu.reggie.entity.User;
import cn.edu.hbpu.reggie.service.UserService;
import cn.edu.hbpu.reggie.utils.SMSUtils;
import cn.edu.hbpu.reggie.utils.ValidateCodeUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 发送手机验证码短信
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();

        if(StringUtils.isNotEmpty(phone)){
            //生成随机4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();

            log.info("code={}",code);

            //调用Api来完成发送短信
            SMSUtils.sendMessage("瑞吉外卖","",phone,code);

            //将生成的验证码存入session中
//            session.setAttribute(phone,code);

            //将生成的验证码存入redis缓存中
            redisTemplate.opsForValue().set(phone,code,3, TimeUnit.MINUTES);

            return R.success("验证码发送成功！");
        }
        return R.error("验证码发送失败！");
    }

    /**
     * 移动端用户登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        log.info(map.toString());
        //获取手机号
        String phone = map.get("phone").toString();

        //获取验证码
        String code = map.get("code").toString();

        //比对验证码
//        Object codeInSession = session.getAttribute(phone);

        //用缓存中的code来比对验证码
        Object codeInSession = redisTemplate.opsForValue().get(phone);

        if(code != null&&code.equals(codeInSession)){
            //判断手机号是否存在
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getPhone,phone);
            User user = userService.getOne(wrapper);
            if(user==null){
                //若不存在则注册新用户
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            //将用户信息存入session中
            session.setAttribute("user",user.getId());

            //用户登录之后,缓存要自动删除验证码
            redisTemplate.delete(phone);

            return R.success(user);
        }
        return R.error("登陆失败！");
    }
}
