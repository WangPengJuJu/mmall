package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import com.mmall.util.DateTimeUtil;
import com.mmall.vo.CategoryVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {
    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
    @Autowired
    private CategoryMapper categoryMapper;

    public ServerResponse delete_category(Integer[] categoryIds){
        int row = categoryMapper.deleteCategoryByIds(categoryIds);
        if(row > 0){
            return ServerResponse.createBySuccess("删除成功");
        }
        return ServerResponse.createByErrorMessage("删除失败");
    }
    public ServerResponse addCategory(String categoryName,Integer parentId,Integer status){
        if(parentId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        Category category = new Category();
        category.setParentId(parentId);
        category.setName(categoryName);
        category.setStatus(status);
        int resultCount = categoryMapper.insert(category);
        if(resultCount > 0){
            return ServerResponse.createBySuccess("添加品类成功!");
        }
        return ServerResponse.createByErrorMessage("添加品类失败！");
    }

    public ServerResponse updateCategoryName(Integer categoryId,String categoryName){
        if(categoryId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        int resultCount = categoryMapper.updateByPrimaryKeySelective(category);
        if(resultCount > 0){
            return ServerResponse.createBySuccess("更新品类成功！");
        }
        return ServerResponse.createByErrorMessage("更新品类失败！");
    }
    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId){
        List<Category> category = categoryMapper.selectChildrenByParentId(categoryId);
        if(CollectionUtils.isEmpty(category)){
            logger.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(category);
    }

    public ServerResponse<List<CategoryVo>> selectCategoryAndChildrenVoById(Integer categoryId){
        Set<Category> setCategory = Sets.newHashSet();
        findChildrenCategoryVo(setCategory,categoryId);
        List<CategoryVo> categoryListVo = Lists.newArrayList();
        if(categoryId != null){
            for(Category categoryItem : setCategory){
                CategoryVo categoryVo = new CategoryVo();
                categoryVo.setId(categoryItem.getId());
                categoryVo.setName(categoryItem.getName());
                categoryVo.setParentId(categoryItem.getParentId());
                categoryVo.setCreateTime(DateTimeUtil.dateToStr(categoryItem.getCreateTime()));
                categoryListVo.add(categoryVo);
            }
        }
        return ServerResponse.createBySuccess(categoryListVo);
    }

    private Set<Category> findChildrenCategoryVo(Set<Category> setCategory,Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category != null){
            setCategory.add(category);
        }
        List<Category> categoryList = categoryMapper.selectChildrenByParentId(categoryId);
        for(Category categoryItem : categoryList){
            findChildrenCategoryVo(setCategory,categoryItem.getId());
        }
        return setCategory;
    }

    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId){
        Set<Category> setCategory = Sets.newHashSet();
        findChildrenCategory(setCategory,categoryId);
        List<Integer> categoryIdList = Lists.newArrayList();
        if(categoryId != null){
            for(Category categoryItem : setCategory){
                categoryIdList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }

    private Set<Category> findChildrenCategory(Set<Category> setCategory,Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category != null){
            setCategory.add(category);
        }
        List<Category> categoryList = categoryMapper.selectChildrenByParentId(categoryId);
        for(Category categoryItem : categoryList){
            findChildrenCategory(setCategory,categoryItem.getId());
        }
        return setCategory;
    }
}
