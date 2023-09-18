package com.yakusa.reggie.common;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/*
    自定义元数据对象处理器,用于公共字段填充,spring的面向切面编程
 */

@Component
@Slf4j
public class MyMetaObjecthandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
    log.info("开始插入填充字段");
    metaObject.setValue("createTime", LocalDateTime.now());
    metaObject.setValue("updateTime", LocalDateTime.now());
    metaObject.setValue("createUser", BaseContext.getUserId());
    metaObject.setValue("updateUser", BaseContext.getUserId());


    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("开始插入填充字段");
        long id = Thread.currentThread().getId();
        log.info("当前线程id:{}",id);
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser", BaseContext.getUserId());
        metaObject.setValue("updateUser", BaseContext.getUserId());


    }
}
