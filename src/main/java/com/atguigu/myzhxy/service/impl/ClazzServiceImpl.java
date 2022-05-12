package com.atguigu.myzhxy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.myzhxy.pojo.Clazz;
import com.atguigu.myzhxy.service.ClazzService;
import com.atguigu.myzhxy.mapper.ClazzMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 *
 */
@Service
@Transactional
public class ClazzServiceImpl extends ServiceImpl<ClazzMapper, Clazz>
    implements ClazzService{

    @Override
    public IPage<Clazz> getClazzByOpr(Page<Clazz> pageParam, Clazz clazz) {
        QueryWrapper queryWrapper = new QueryWrapper();
        String gradeName = clazz.getGradeName();
        String name = clazz.getName();
        if (!StringUtils.isEmpty(gradeName)) {
                queryWrapper.like("grade_name",gradeName);
        }
        if (!StringUtils.isEmpty(name)) {
            queryWrapper.like("name",name);
        }
        queryWrapper.orderByAsc("id");

        Page page = baseMapper.selectPage(pageParam, queryWrapper);

        return page;
    }
}




