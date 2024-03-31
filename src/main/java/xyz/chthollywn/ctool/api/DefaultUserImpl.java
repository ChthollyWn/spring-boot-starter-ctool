package xyz.chthollywn.ctool.api;


import org.springframework.stereotype.Component;

@Component
public class DefaultUserImpl implements CToolUserInterface {
    @Override
    public boolean isLogin() {
        return false;
    }

    @Override
    public Object getUserId() {
        return null;
    }

    @Override
    public Object getUserInfo() {
        return null;
    }
}
