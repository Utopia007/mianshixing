package com.luyouxiao.mianshixing.satoken;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.luyouxiao.mianshixing.model.entity.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.luyouxiao.mianshixing.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author 鹿又笑
 * @create 2024/10/8-17:50
 * @description
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    /**
     * 返回一个账号所有的权限码集合
     * @param loginId
     * @param s
     * @return
     */
    @Override
    public List<String> getPermissionList(Object loginId, String s) {
        return new ArrayList<>();
    }

    /**
     * 返回一个账号所有角色标识集合（角色可以和权限分开校验）
     * @param loginId
     * @param s
     * @return
     */
    @Override
    public List<String> getRoleList(Object loginId, String s) {
        // 从当前登录的用户信息中获取角色标识
        User user = (User) StpUtil.getSessionByLoginId(loginId).get(USER_LOGIN_STATE);
        return Collections.singletonList(user.getUserRole());
    }
}
