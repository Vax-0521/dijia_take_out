package com.xyy.dijia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xyy.dijia.entity.Category;


public interface CategoryService extends IService<Category> {
    public void removeById1(Long id);
}
