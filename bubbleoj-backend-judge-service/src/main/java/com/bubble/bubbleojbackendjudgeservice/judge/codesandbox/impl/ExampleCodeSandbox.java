package com.bubble.bubbleojbackendjudgeservice.judge.codesandbox.impl;


import com.bubble.bubblebackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.bubble.bubblebackendmodel.model.codesandbox.ExecuteCodeResponse;
import com.bubble.bubblebackendmodel.model.codesandbox.JudgeInfo;
import com.bubble.bubblebackendmodel.model.enums.JudgeInfoMessageEnum;
import com.bubble.bubblebackendmodel.model.enums.QuestionSubmitStatusEnum;
import com.bubble.bubbleojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 示例代码沙箱
 */
@Slf4j
public class ExampleCodeSandbox implements CodeSandbox {
    /**
     * 执行代码
     *
     * @param executeCodeRequest
     * @return
     */
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();

        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(inputList);
        executeCodeResponse.setMessage("测试执行成功");
        executeCodeResponse.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoMessageEnum.Accepted.getText());
        judgeInfo.setMemory(100L);
        judgeInfo.setTime(100L);
        executeCodeResponse.setJudgeInfo(judgeInfo);
        return executeCodeResponse;
    }
}
