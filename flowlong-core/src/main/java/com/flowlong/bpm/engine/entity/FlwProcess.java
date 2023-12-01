/*
 * Copyright 2023-2025 Licensed under the AGPL License
 */
package com.flowlong.bpm.engine.entity;

import com.flowlong.bpm.engine.assist.Assert;
import com.flowlong.bpm.engine.core.Execution;
import com.flowlong.bpm.engine.core.FlowLongContext;
import com.flowlong.bpm.engine.core.enums.FlowState;
import com.flowlong.bpm.engine.exception.FlowLongException;
import com.flowlong.bpm.engine.handler.impl.EndProcessHandler;
import com.flowlong.bpm.engine.model.NodeModel;
import com.flowlong.bpm.engine.model.ProcessModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * 流程定义实体类
 *
 * <p>
 * 尊重知识产权，不允许非法使用，后果自负
 * </p>
 *
 * @author hubin
 * @since 1.0
 */
@Getter
@Setter
@ToString
public class FlwProcess extends FlowEntity {
    /**
     * 流程定义 key 唯一标识
     */
    protected String processKey;
    /**
     * 流程定义名称
     */
    protected String processName;
    /**
     * 流程图标地址
     */
    protected String processIcon;
    /**
     * 流程定义类型（预留字段）
     */
    protected String processType;
    /**
     * 流程版本
     */
    protected Integer processVersion;
    /**
     * 当前流程的实例url（一般为流程第一步的url）
     * 该字段可以直接打开流程申请的表单
     */
    protected String instanceUrl;
    /**
     * 备注说明
     */
    protected String remark;
    /**
     * 使用范围 0，全员 1，指定人员（业务关联） 2，均不可提交
     */
    protected Integer useScope;
    /**
     * 流程状态 0，不可用 1，可用
     */
    protected Integer processState;
    /**
     * 流程模型定义JSON内容
     */
    protected String modelContent;
    /**
     * 排序
     */
    protected Integer sort;

    public void setFlowState(FlowState flowState) {
        this.processState = flowState.getValue();
    }

    /**
     * 模型解析
     */
    public ProcessModel model() {
        if (null == this.modelContent) {
            return null;
        }
        return FlowLongContext.parseProcessModel(this.modelContent, this.id, false);
    }

    /**
     * 执行节点模型
     *
     * @param flowLongContext 流程引擎上下文
     * @param execution       流程执行对象
     * @param nodeName        节点名称
     */
    public void executeNodeModel(FlowLongContext flowLongContext, Execution execution, String nodeName) {
        this.processModelParser(processModel -> {
            NodeModel nodeModel = processModel.getNode(nodeName);
            Assert.isNull(nodeModel, "流程模型中未发现，流程节点" + nodeName);
            Optional<NodeModel> executeNodeOptional = nodeModel.nextNode();
            if (executeNodeOptional.isPresent()) {
                // 执行流程节点
                NodeModel executeNode = executeNodeOptional.get();
                executeNode.execute(flowLongContext, execution);
                /**
                 * 不存在子节点，不存在其它分支节点，当前执行节点为最后节点 并且当前节点不是审批节点
                 * 执行结束流程处理器
                 */
                if (null == executeNode.getChildNode() && null == executeNode.getConditionNodes()) {
                    if (!executeNode.nextNode().isPresent() && executeNode.getType() != 1) {
                        new EndProcessHandler().handle(flowLongContext, execution);
                    }
                }
            } else {
                /**
                 * 无执行节点流程结束
                 */
                new EndProcessHandler().handle(flowLongContext, execution);
            }
        });
    }

    /**
     * 执行开始模型
     *
     * @param flowLongContext 流程引擎上下文
     * @param execution       流程执行对象
     */
    public void executeStartModel(FlowLongContext flowLongContext, Execution execution) {
        this.processModelParser(processModel -> {
            NodeModel nodeModel = processModel.getNodeConfig();
            Assert.isNull(nodeModel, "流程定义[processName=" + this.processName + ", processVersion=" + this.processVersion + "]没有开始节点");
            // 创建首个审批任务
            nodeModel.createTask(flowLongContext, execution);
        });
    }

    /**
     * 流程模型解析
     *
     * @param consumer 解析模型消费者
     */
    private void processModelParser(Consumer<ProcessModel> consumer) {
        if (null != this.modelContent) {
            consumer.accept(this.model());
        }
    }

    /**
     * 流程状态验证
     */
    public FlwProcess checkState() {
        if (Objects.equals(0, this.processState)) {
            throw new FlowLongException("指定的流程定义[id=" + this.id + ",processVersion=" + this.processVersion + "]为非活动状态");
        }
        return this;
    }
}
