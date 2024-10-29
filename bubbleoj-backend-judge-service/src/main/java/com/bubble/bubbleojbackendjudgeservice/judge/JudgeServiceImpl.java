package com.bubble.bubbleojbackendjudgeservice.judge;

import cn.hutool.json.JSONUtil;

import com.bubble.bubblebackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.bubble.bubblebackendmodel.model.codesandbox.ExecuteCodeResponse;
import com.bubble.bubblebackendmodel.model.codesandbox.JudgeInfo;
import com.bubble.bubblebackendmodel.model.dto.question.JudgeCase;
import com.bubble.bubblebackendmodel.model.entity.Question;
import com.bubble.bubblebackendmodel.model.entity.QuestionSubmit;
import com.bubble.bubblebackendmodel.model.enums.QuestionSubmitStatusEnum;
import com.bubble.bubbleojbackendcommon.common.ErrorCode;
import com.bubble.bubbleojbackendcommon.exception.BusinessException;
import com.bubble.bubbleojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.bubble.bubbleojbackendjudgeservice.judge.codesandbox.CodeSandboxFactory;
import com.bubble.bubbleojbackendjudgeservice.judge.codesandbox.CodeSandboxProxy;
import com.bubble.bubbleojbackendjudgeservice.judge.strategy.JudgeContext;
import com.bubble.bubbleojbackendjudgeservice.judge.strategy.JudgeManager;
import com.bubble.bubbleojbackendserviceclient.service.QuestionFeignClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JudgeServiceImpl implements JudgeService {
    /**
     * 判题
     *
     * @param questionSubmitId
     * @return
     */
    @Resource
    private QuestionFeignClient questionFeignClient;

    @Resource
    private JudgeManager judgeManager;

    @Value("${codesandbox.type:example}")
    private String type;

    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        // 1）传入题目提交的id，获取到对应的题目，提交信息（包含代码，编程语言等）
        QuestionSubmit questionSubmit = questionFeignClient.getQuestionSubmitById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR ,"提交信息不存在");
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionFeignClient.getQuestionById(questionId);
        if(question == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR ,"题目信息不存在");
        }
        // 2) 如果不为等待状态，就不用重复执行
        if(!questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue())){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"题目已在判题");
        }
        // 3) 更改判题（题目提交）的状态为判题中，防止重复执行
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if(!update){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"题目更新状态错误");
        }
        // 4）调用代码沙箱，获取到代码执行结果
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        //获取输入用例
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        List<String> outputList = executeCodeResponse.getOutputList();
        // 5）根据沙箱的执行结果，设置题目的判题状态和信息
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(outputList);
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestion(question);
        judgeContext.setQuestionSubmit(questionSubmit);
        JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);
        //6) 修改数据库中的判题结果
        questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if(!update){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"题目更新状态错误");
        }
        QuestionSubmit questionSubmitResult = questionFeignClient.getQuestionSubmitById(questionId);
        return questionSubmitResult;
    }
}
