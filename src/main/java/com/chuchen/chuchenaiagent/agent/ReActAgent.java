package com.chuchen.chuchenaiagent.agent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * @author chuchen
 * @date 2025/5/28 14:57
 * @description ReAct (Reasoning and Acting) 模式的代理抽象类。实现了 思考-行动 的循环模式
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public abstract class ReActAgent extends BaseAgent{

    /**
     * 处理当前状态并且执行下一步行动
     * @return 是否需要执行下一步行动
     */
    public abstract boolean think();

    /**
     * 执行上一步思考中确定的行动
     * @return 本次行动的结果
     */
    public abstract String act();

    /**
     * 执行单个步骤，思考和行动
     * @return 本次步骤的结果
     */
    @Override
    public String step() {
        try {
            // 思考
            boolean shouldAct = think();
            if(!shouldAct) {
                return "无需行动";
            }
            // 行动
            return act();
        }catch (Exception e) {
            // 记录一场日志
            // e.printStackTrace();
            log.error("步骤执行失败: {}", e.getMessage());
            return "步骤执行失败：" + e.getMessage();
        }
    }
}
