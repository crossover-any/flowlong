/* Copyright 2023-2025 jobob@qq.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.flowlong.bpm.engine.scheduling;

/**
 * 调度器接口，与具体的定时调度框架无关
 *
 * <p>
 * 尊重知识产权，CV 请保留版权，爱组搭 http://aizuda.com 出品
 * </p>
 *
 * @author hubin
 * @since 1.0
 */
public interface FlowLongScheduler {
    String CONFIG_REPEAT = "scheduler.repeat";
    String CONFIG_USECALENDAR = "scheduler.useCalendar";
    String CONFIG_HOLIDAYS = "scheduler.holidays";
    String CONFIG_WEEKS = "scheduler.weeks";
    String CONFIG_WORKTIME = "scheduler.workTime";

    String CALENDAR_NAME = "snakerCalendar";

    String KEY = "id";
    String MODEL = "model";
    String GROUP = "snaker";

    String TYPE_EXECUTOR = "executor.";
    String TYPE_REMINDER = "reminder.";

    /**
     * 调度执行方法
     *
     * @param jobEntity 调度DTO
     */
    void schedule(JobEntity jobEntity);

    /**
     * 停止调度
     *
     * @param key job主键
     */
    void delete(String key);
}
