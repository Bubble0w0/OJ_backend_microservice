package com.bubble.bubbleojbackendjudgeservice.judge.codesandbox.impl;

import com.bubble.bubblebackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.bubble.bubblebackendmodel.model.codesandbox.ExecuteCodeResponse;
import com.bubble.bubbleojbackendjudgeservice.judge.codesandbox.CodeSandbox;


/**
 * 第三方代码沙箱
 */
public class ThirdPartyCodeSandbox implements CodeSandbox {
    /**
     * 执行代码
     *
     * @param executeCodeRequest
     * @return
     */
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方代码沙箱");
        return null;
    }
}
