package me.yangbajing.user.db.mapper;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.yangbajing.user.db.domain.UserDO;

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-03 17:28:56
 */
@TableName(value = "user", schema = "user")
public interface UserMapper extends BaseMapper<UserDO> {
}
