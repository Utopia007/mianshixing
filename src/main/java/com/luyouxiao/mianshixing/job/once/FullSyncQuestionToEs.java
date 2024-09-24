package com.luyouxiao.mianshixing.job.once;

import cn.hutool.core.collection.CollUtil;
import com.luyouxiao.mianshixing.esdao.QuestionEsDao;
import com.luyouxiao.mianshixing.model.dto.question.QuestionEsDTO;
import com.luyouxiao.mianshixing.model.entity.Question;
import com.luyouxiao.mianshixing.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 鹿又笑
 * @create 2024/9/22-23:36
 * @description 全量同步题目到 es  (CommandLineRunner 接口是 Spring Boot 提供的一个接口，
 *                                                 用于在应用程序启动后执行一些特定的命令行操作。
 *                                                 这个接口非常适合用来执行一些初始化任务，
 *                                                 比如数据初始化、定时任务的设置、日志记录等。)
 */
@Slf4j
@Component
public class FullSyncQuestionToEs implements CommandLineRunner {

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionEsDao questionEsDao;

    @Override
    public void run(String... args) throws Exception {
        // 全量获取题目
        List<Question> questionList = questionService.list();
        if (CollUtil.isEmpty(questionList)) {
            return;
        }
        // 转 es 为实体类
        List<QuestionEsDTO> questionEsDTOList = questionList.stream()
                .map(QuestionEsDTO::objToDto)
                .collect(Collectors.toList());
        // 分页批量插入到 es
        final int pageSize = 500;
        int total = questionEsDTOList.size();
        log.info("FullSyncQuestionToEs start, total {}", total);
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            questionEsDao.saveAll(questionEsDTOList.subList(i, end));
        }
        log.info("FullSyncQuestionToEs end, total {}", total);
    }

}

















