package com.order.model;

/**
 * Created with IntelliJ IDEA.
 * User: daisong
 * Date: 15-11-22
 * Time: 下午8:52
 * To change this template use File | Settings | File Templates.
 */
public enum UserRole {

    ADMIN("管理员"),COMMON("普通用户");

    private String text;

    UserRole(String text){
        this.text = text;
    }

    public String getText(){
        return this.text;
    }

    public static UserRole getUserRoleByText(String roleText){
        UserRole[] roles = values();
        for(UserRole role : roles){
            if(role.text.equals(roleText)){
                return role;
            }
        }

        return null;
    }
}
