package com.zxl.haze.core.executor;

public interface TaskContext<T> {

    T getContext();

    void setContext(T context);

    void remove();

}
