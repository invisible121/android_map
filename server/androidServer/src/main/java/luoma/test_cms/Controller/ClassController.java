package luoma.test_cms.Controller;

import luoma.test_cms.Entity.ClassInfo;
import luoma.test_cms.Entity.Login;
import luoma.test_cms.Service.ClassInfoService;
import luoma.test_cms.Service.LoginService;
import luoma.test_cms.response.Impl.ClassResponse;
import luoma.test_cms.response.Impl.UserResponse;
import luoma.test_cms.response.Response;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sun.rmi.runtime.Log;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/class")
public class ClassController {

    @Resource
    ClassInfoService classInfoService;

    @Resource
    LoginService loginService;

    @RequestMapping("/addClass")
    public Response addClass(
            @RequestParam("token") String token,
            @RequestParam("name") String name,
            @RequestParam("place") String place,
            @RequestParam("date") int date,
            @RequestParam("time") int time

    ) {
        List<Login> logins = loginService.selectLoginByToken(token);

        if (logins.size() == 0) {
            return new UserResponse.Register(-1);
        } else {
            List<ClassInfo> classInfos = classInfoService.getClassByStu(logins.get(0).id);

            for (ClassInfo classInfo : classInfos) {
                if (classInfo.date == date && classInfo.time == time) {
                    return new UserResponse.Register(1);
                }
            }


            classInfoService.addClass(logins.get(0).id, name, place, date, time);

            return new UserResponse.Register(0);
        }
    }

    @RequestMapping("/selectClassById")
    public Response selectById(
            @RequestParam("token") String token
    ) {
        List<Login> logins = loginService.selectLoginByToken(token);

        if (logins.size() == 0) {
            return new ClassResponse.getClass(-1, null);
        } else {
            return new ClassResponse.getClass(0,
                    classInfoService.getClassByStu(logins.get(0).id));
        }
    }

    @RequestMapping("/freeClass")
    public String freeClass(
            @RequestParam("class") String className
    ) {
        switch (className) {
            case "sx":
                return "sx201\nsx303\nsx502";

            case "sy":
                return "sy301\nsy302\nsy506";

            case "yf":
                return "yf312\nyf411\nyf508";
            default:
                return "";
        }
    }
}
