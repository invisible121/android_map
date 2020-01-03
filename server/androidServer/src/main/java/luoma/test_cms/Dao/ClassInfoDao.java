package luoma.test_cms.Dao;

import luoma.test_cms.Entity.ClassInfo;

import java.util.List;

public interface ClassInfoDao {
    List<ClassInfo> getclassInfos();

    List<ClassInfo> getClassByStu(int id);

    int addClass(int id, String className, String place, int date, int time);
}
