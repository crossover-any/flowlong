/* 
 * Copyright 2023-2025 Licensed under the AGPL License
 */
package com.flowlong.bpm.engine.core.enums;

import java.util.Arrays;
/**
 * 流程状态
 *
 * <p>
 * 尊重知识产权，不允许非法使用，后果自负
 * </p>
 *
 * @author 江涛
 * @since 1.0
 */
public enum InstanceState {
    /**
     * 审批中
     */
    active(0),
    /**
     * 审批通过
     */
    complete(1),
    /**
     * 审批拒绝
     */
    reject(2),
    /**
     * 撤销审批
     */
    revoke(3),
    /**
     * 超时结束
     */
    timeout(4),
    /**
     * 强制终止
     */
    termination(5);

    private final int value;

    InstanceState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static InstanceState get(int value) {
        return Arrays.stream(InstanceState.values()).filter(s -> s.getValue() == value).findFirst().orElseGet(null);
    }

}
