package com.bubble.bubbleojbackendjudgeservice.judge.codesandbox;


import com.bubble.bubblebackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.bubble.bubblebackendmodel.model.codesandbox.ExecuteCodeResponse;

/**
 * 代码沙箱接口定义
 */
public interface CodeSandbox {
    /**
     * 执行代码
     *
     *
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
