package com.luyouxiao.mianshixing.esdao;

import com.luyouxiao.mianshixing.model.dto.question.QuestionEsDTO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * @author 鹿又笑
 * @create 2024/9/22-22:25
 * @description 题目ES操作
 */
public interface QuestionEsDao extends ElasticsearchRepository<QuestionEsDTO, Long> {

    /**
     * 根据用户 id 查询
     * @param userId
     * @return
     */
    List<QuestionEsDTO> findByUserId(Long userId);


}
