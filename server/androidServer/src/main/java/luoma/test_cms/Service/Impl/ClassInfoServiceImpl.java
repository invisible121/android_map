package luoma.test_cms.Service.Impl;

import luoma.test_cms.Dao.ClassInfoDao;
import luoma.test_cms.Entity.ClassInfo;
import luoma.test_cms.Service.ClassInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("ClassInfoService")
public class ClassInfoServiceImpl implements ClassInfoService {

    @Resource
    ClassInfoDao classInfoDao;

    @Override
    public List<ClassInfo> getclassInfos() {
        return classInfoDao.getclassInfos();
    }

    @Override
    public int addClass(int id, String className, String place, int date, int time) {
        return classInfoDao.addClass(id, className, place, date, time);
    }

    @Override
    public List<ClassInfo> getClassByStu(int id) {
        return classInfoDao.getClassByStu(id);
    }
}
