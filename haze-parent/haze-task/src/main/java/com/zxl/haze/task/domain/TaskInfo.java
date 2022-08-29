package com.zxl.haze.task.domain;

import lombok.Data;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
@Data
public class TaskInfo {

    private String id;
    private String name;
    private Integer taskStatus;
    private Integer taskType;
    private Date beginExecuteTime;
    private Date endExecuteTime;
    private String progress;
    private String inputParams;
    private Integer isDeleted;
    private String failureReason;
    private String response;
    private Integer createUser;
    private Date createTime;





    public enum TaskType {
        FILE_UPLOAD(1, "文件上传"),
        FILE_DOWNLOAD(2, "文件打包下载"),
        FILE_EXPORT(3, "文件导出"),
        ;
        private final int type;

        private final String desc;

        TaskType(int type, String desc) {
            this.type = type;
            this.desc = desc;
        }

        public int getType() {
            return type;
        }

        public String getDesc() {
            return desc;
        }
    }


    public enum TaskStatus {

        CREATED(0, "已创建/等待中"),

        EXECUTING(1, "进行中"),

        SUCCESS(2, "执行成功"),

        FAILED(3, "执行失败"),

        ;

        public static final Set<Integer> EXECUTING_STATUS = new HashSet<Integer>(){
            {
                add(TaskInfo.TaskStatus.CREATED.getType());
                add(TaskInfo.TaskStatus.EXECUTING.getType());
            }
        };

        public static final Set<Integer> USER_QUERY_STATUS = new HashSet<Integer>(){
            {
                add(TaskInfo.TaskStatus.CREATED.getType());
                add(TaskInfo.TaskStatus.EXECUTING.getType());
                add(TaskInfo.TaskStatus.SUCCESS.getType());
                add(TaskInfo.TaskStatus.FAILED.getType());
            }
        };

        private final int type;

        private final String desc;

        TaskStatus(int type, String desc) {
            this.type = type;
            this.desc = desc;
        }

        public int getType() {
            return type;
        }

        public String getDesc() {
            return desc;
        }
    }
}
