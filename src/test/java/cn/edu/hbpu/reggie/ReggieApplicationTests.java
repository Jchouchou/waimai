package cn.edu.hbpu.reggie;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

@SpringBootTest
class ReggieApplicationTests {

    @Test
    void contextLoads() {
        String input = "123456";
        String md5 = DigestUtils.md5DigestAsHex(input.getBytes());
        System.out.println(md5);
    }

    @Test
    void aaa(){
        String fileName = "abc.jpg";
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        System.out.println(suffix);
    }

}
