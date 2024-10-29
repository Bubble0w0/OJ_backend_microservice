package com.bubble.bubbleojbackendjudgeservice.judge.strategy;


import com.bubble.bubblebackendmodel.model.codesandbox.JudgeInfo;
import com.bubble.bubblebackendmodel.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * 判题管理(简化调用)
 */
@Service
public class JudgeManager {
    /**
     * 判题
     * @param judgeContext
     * @return
     */
    public JudgeInfo doJudge(JudgeContext judgeContext){
         QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
         String language = questionSubmit.getLanguage();
         JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
         if ("java".equals(language)){
             judgeStrategy = new JavaLanguageJudgeStrategy();
         }
         return judgeStrategy.doJudge(judgeContext);
     }
}
