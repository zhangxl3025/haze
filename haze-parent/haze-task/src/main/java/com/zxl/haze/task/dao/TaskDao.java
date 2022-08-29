package com.zxl.haze.task.dao;

import com.zxl.haze.task.domain.TaskInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class TaskDao {

    Map<String, TaskInfo> db = new HashMap<>();


    public TaskInfo init(TaskInfo taskInfo) {
        String id = UUID.randomUUID().toString().replaceAll("-", "");
        taskInfo.setId(id);
        db.put(id, taskInfo);
        return taskInfo;
    }

    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    public TaskInfo update(TaskInfo taskInfo) {
        TaskInfo target = db.get(taskInfo.getId());


        //单元测试莫名中断？？
        try {
            BeanUtils.copyProperties(taskInfo,target,getNullPropertyNames(taskInfo));
        }catch (Throwable e){
            e.printStackTrace();
        }finally {
            System.out.println(2);
        }



        db.put(taskInfo.getId(), target);
        return target;
    }

    public TaskInfo selectById(String taskId) {
        return db.get(taskId);
    }
}
