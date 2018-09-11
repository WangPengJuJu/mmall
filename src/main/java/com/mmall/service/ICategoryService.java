package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.vo.CategoryVo;

import java.util.List;

public interface ICategoryService {
    ServerResponse addCategory(String categoryName, Integer parentId,Integer status);
    ServerResponse updateCategoryName(Integer categoryId,String categoryName);
    ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);
    ServerResponse<List<CategoryVo>> selectCategoryAndChildrenVoById(Integer categoryId);
    ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);
    ServerResponse delete_category(Integer[] categoryIds);
}
