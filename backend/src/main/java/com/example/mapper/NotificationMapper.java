package com.example.mapper;

import com.example.entity.Notification;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface NotificationMapper {
    @Insert("INSERT INTO notification (user_id, description, is_read, send_time, deleted) " +
            "VALUES (#{userId}, #{description}, #{isRead}, #{sendTime}, #{deleted})")
    void insert(Notification notification);

    @Update("UPDATE notification SET deleted = 1 WHERE id = #{id}")
    void deleteById(Integer id);

    @Update("UPDATE notification SET deleted = 1 WHERE id IN " + "(#{ids})")
    void deleteBatch(List<Integer> ids);

    void updateById(Notification notification);

    @Select("SELECT * FROM notification WHERE id = #{id}")
    Notification selectById(Integer id);

    List<Notification> selectAll(Notification notification);

    @Delete("DELETE FROM notification WHERE id = #{id}")
    void deleteReal(Integer id);

    @Select("SELECT COUNT(*) FROM notification WHERE user_id = #{userId} AND is_read = 0")
    Integer selectUnreadCount(Integer userId);
}