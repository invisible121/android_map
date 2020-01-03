package luoma.test_cms.response.Impl;

import lombok.Data;
import luoma.test_cms.Entity.ClassInfo;
import luoma.test_cms.response.Response;

import java.util.List;

public class ClassResponse {

    @Data
    public static class getClass implements Response{
        int state;
        List<ClassInfo> classInfos;

        public getClass(int state, List<ClassInfo> classInfos) {
            this.state = state;
            this.classInfos = classInfos;
        }
    }
}
