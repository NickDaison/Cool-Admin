package com.order.admin.dao;

import com.order.model.Expend;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: daisong
 * Date: 2015/12/6
 * Time: 20:34
 * To change this template use File | Settings | File Templates.
 */
public interface IExpendDao {
    List<Expend> findAll();
}