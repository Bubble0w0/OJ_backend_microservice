package com.bubble.bubbleojbackendquestionservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bubble.bubblebackendmodel.model.dto.questionSubmit.QuestionSubmitAddRequest;
import com.bubble.bubblebackendmodel.model.dto.questionSubmit.QuestionSubmitQueryRequest;
import com.bubble.bubblebackendmodel.model.entity.QuestionSubmit;
import com.bubble.bubblebackendmodel.model.entity.User;
import com.bubble.bubblebackendmodel.model.vo.QuestionSubmitVO;

/**
* @author Bubble
* @description 针对表【question_submit(题目提交)】的数据库操作Service
* @createDate 2024-10-08 14:21:26
*/
public interface QuestionSubmitService extends IService<QuestionSubmit> {
    /**
     * 题目提交
     *
     * QuestionSubmitAddRequest 题目提交信息
     *
     * @param
     * @param loginUser
     * @return
     */
    long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);

    /**
     * 获取查询条件
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);


    /**
     * 获取题目封装
     *
     * @param questionSubmit
     * @param loginUser
     * @return
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser);

    /**
     * 分页获取题目封装
     *
     * @param questionSubmitPage
     * @param loginUser
     * @return
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser);

}
