package luoma.test_cms.Service;


import luoma.test_cms.Entity.ClassInfo;

import java.util.List;

public interface ClassInfoService {

    List<ClassInfo> getclassInfos();

    List<ClassInfo> getClassByStu(int id);

    int addClass(int id, String className, String place, int date, int time);

}
