package com.keji.green.lit.engine.service;

import com.keji.green.lit.engine.exception.BusinessException;
import com.keji.green.lit.engine.model.trade.PayWay;

import java.util.List;

public interface PayWayService {
    /**
     * 保存
     */
    void saveData(PayWay rpPayWay);

    /**
     * 更新
     */
    void updateData(PayWay rpPayWay);

    /**
     * 根据id获取数据
     *
     * @param id
     * @return
     */
    PayWay getDataById(String id);

    /**
     * 根据支付方式、渠道编码获取数据
     * @param rpTypeCode
     * @return
     */
    PayWay getByPayWayTypeCode(String payProductCode, String payWayCode, String rpTypeCode);


    /**
     * 获取分页数据
     *
     * @param pageParam
     * @return
     */
//    PageBean listPage(PageParam pageParam, RpPayWay rpPayWay);

    /**
     * 绑定支付费率
     * @param payWayCode
     * @param payTypeCode
     * @param payRate
     */
    void createPayWay(String payProductCode, String payWayCode, String payTypeCode, Double payRate)  throws BusinessException;

    /**
     * 根据支付产品获取支付方式
     * @param payProductCode
     */
//    List<RpPayWay> listByProductCode(String payProductCode);

    /**
     * 获取所有支付方式
     */
//    List<RpPayWay> listAll();
}
