package com.bubble.bubbleojbackendserviceclient.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bubble.bubblebackendmodel.model.dto.question.QuestionQueryRequest;
import com.bubble.bubblebackendmodel.model.entity.Question;
import com.bubble.bubblebackendmodel.model.entity.QuestionSubmit;
import com.bubble.bubblebackendmodel.model.vo.QuestionVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
* @author Bubble
* @description 针对表【question(题目)】的数据库操作Service
* @createDate 2024-10-08 14:20:05
*/
@FeignClient(name = "bubbleoj-backend-question-service",path = "/api/question/inner")
public interface QuestionFeignClient {

    @GetMapping("/get/id")
    Question getQuestionById(@RequestParam("questionId") long questionId);

    @GetMapping("/quesion_submit/get/id")
    QuestionSubmit getQuestionSubmitById(@RequestParam("questionId") long questionSubmitId);

    @PostMapping("/quesion_submit/update")
    boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmit);

}
