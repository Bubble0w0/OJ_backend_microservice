package com.bubble.bubbleojbackendquestionservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bubble.bubblebackendmodel.model.dto.questionSubmit.QuestionSubmitAddRequest;
import com.bubble.bubblebackendmodel.model.dto.questionSubmit.QuestionSubmitQueryRequest;
import com.bubble.bubblebackendmodel.model.entity.Question;
import com.bubble.bubblebackendmodel.model.entity.QuestionSubmit;
import com.bubble.bubblebackendmodel.model.entity.User;
import com.bubble.bubblebackendmodel.model.enums.QuestionSubmitLanguageEnum;
import com.bubble.bubblebackendmodel.model.enums.QuestionSubmitStatusEnum;
import com.bubble.bubblebackendmodel.model.vo.QuestionSubmitVO;
import com.bubble.bubbleojbackendcommon.common.ErrorCode;
import com.bubble.bubbleojbackendcommon.constant.CommonConstant;
import com.bubble.bubbleojbackendcommon.exception.BusinessException;
import com.bubble.bubbleojbackendcommon.utils.SqlUtils;
import com.bubble.bubbleojbackendquestionservice.mapper.QuestionSubmitMapper;
import com.bubble.bubbleojbackendquestionservice.rabbitmq.MyMessageProducer;
import com.bubble.bubbleojbackendquestionservice.service.QuestionService;
import com.bubble.bubbleojbackendquestionservice.service.QuestionSubmitService;
import com.bubble.bubbleojbackendserviceclient.service.JudgeFeignClient;
import com.bubble.bubbleojbackendserviceclient.service.UserFeignClient;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
* @author Bubble
* @description 针对表【question_submit(题目提交)】的数据库操作Service实现
* @createDate 2024-10-08 14:21:26
*/
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
    implements QuestionSubmitService {
    
    @Resource
    private QuestionService questionService;
    @Resource
    private UserFeignClient userFeignClient;
    @Resource
    @Lazy
    private JudgeFeignClient judgeFeignClient;
    @Resource
    private MyMessageProducer myMessageProducer;

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        // 校验编程语言是否合法
        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if(languageEnum == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"编程语言错误");
        }
        long questionId = questionSubmitAddRequest.getQuestionId();
        // 判断实体是否存在，根据类别获取实体
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已提交题目
        long userId = loginUser.getId();

        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setUserId(userId);
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setLanguage(language);
        // 设置初始状态
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionSubmit.setJudgeInfo("{}");
        boolean save = this.save(questionSubmit);
        if (!save){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"数据插入失败");
        }else {
            Long questionSubmitId = questionSubmit.getId();
            //发送消息
            myMessageProducer.sendMessage("code_exchange","my_routingKey",String.valueOf(questionSubmitId));
            // 执行判题服务
//            CompletableFuture.runAsync(()->{
//                judgeFeignClient.doJudge(questionSubmitId);
//            });
            return questionSubmitId;
        }

    }

    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitSubmitQueryRequest == null) {
            return queryWrapper;
        }

        Long questionId = questionSubmitSubmitQueryRequest.getQuestionId();
        Long userId = questionSubmitSubmitQueryRequest.getUserId();
        String language = questionSubmitSubmitQueryRequest.getLanguage();
        Integer status = questionSubmitSubmitQueryRequest.getStatus();
        String sortField = questionSubmitSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitSubmitQueryRequest.getSortOrder();


        // 拼接查询条件
        queryWrapper.eq(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(QuestionSubmitStatusEnum.getEnumByValue(status)!= null , "status", status);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        long userId = loginUser.getId();
        if(userId != questionSubmit.getUserId() && !userFeignClient.isAdmin(loginUser)){
            questionSubmitVO.setCode(null);
        }
        return questionSubmitVO;
    }

    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream()
                .map(questionSubmit -> getQuestionSubmitVO(questionSubmit, loginUser))
                .collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }




}




