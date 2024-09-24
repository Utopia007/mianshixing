package com.luyouxiao.mianshixing.es;

import com.luyouxiao.mianshixing.esdao.QuestionEsDao;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author 鹿又笑
 * @create 2024/9/22-23:30
 * @description
 */
@SpringBootTest
class QuestionEsDaoTest {

    @Resource
    private QuestionEsDao questionEsDao;

    @Test
    void findByUserId() {
        questionEsDao.findByUserId(1L);
    }
}
