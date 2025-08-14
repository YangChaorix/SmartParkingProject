package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.entity.Notice;
import com.example.mapper.NoticeMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 系统公告业务处理类，负责系统公告的增删改查等操作。
 **/
@Service
public class NoticeService {

    @Resource
    private NoticeMapper noticeMapper; // 注入NoticeMapper用于数据库操作

    /**
     * 添加公告。
     * @param notice 公告实体
     */
    public void add(Notice notice) {
        notice.setTime(DateUtil.now()); // 设置当前时间为公告时间
        noticeMapper.insert(notice); // 插入数据库
    }

    /**
     * 根据ID删除公告。
     * @param id 公告ID
     */
    public void deleteById(Integer id) {
        noticeMapper.deleteById(id); // 删除操作
    }

    /**
     * 批量删除公告。
     * @param ids 公告ID列表
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            noticeMapper.deleteById(id); // 批量删除操作
        }
    }

    /**
     * 更新公告信息。
     * @param notice 公告实体
     */
    public void updateById(Notice notice) {
        notice.setTime(DateUtil.now()); // 更新时间为当前时间
        noticeMapper.updateById(notice); // 更新操作
    }

    /**
     * 根据ID查询公告。
     * @param id 公告ID
     * @return 公告实体
     */
    public Notice selectById(Integer id) {
        return noticeMapper.selectById(id); // 查询操作
    }

    /**
     * 查询所有公告。
     * @param notice 公告实体（可选条件）
     * @return 公告列表
     */
    public List<Notice> selectAll(Notice notice) {
        return noticeMapper.selectAll(notice); // 查询所有操作
    }

    /**
     * 分页查询公告。
     * @param notice 公告实体（可选条件）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    public PageInfo<Notice> selectPage(Notice notice, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize); // 开始分页
        List<Notice> list = this.selectAll(notice); // 查询所有操作
        return PageInfo.of(list); // 返回分页结果
    }

}