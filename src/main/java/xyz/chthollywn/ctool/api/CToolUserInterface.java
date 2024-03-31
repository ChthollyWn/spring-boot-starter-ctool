package xyz.chthollywn.ctool.api;

public interface CToolUserInterface {
    /**
     * 检查是否登录
     */
    boolean isLogin();

    /**
     * 获取用户Id
     */
    Object getUserId();

    /**
     * 获取用户信息
     */
    Object getUserInfo();
}
