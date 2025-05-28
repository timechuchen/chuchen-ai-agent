package com.chuchen.chuchenaiagent.agent.model;

/**
 * @author chuchen
 * @date 2025/5/28 14:53
 * @description 代理执行状态枚举类
 */
public enum AgentState {

    /**
     * 空闲状态
     */
    IDLE,

    /**
     * 运行中状态
     */
    RUNNING,

    /**
     * 已完成状态
     */
    FINISHED,

    /**
     * 错误状态
     */
    ERROR
}
