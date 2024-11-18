package com.mayday9.splatoonbot.business.service;

import java.security.NoSuchAlgorithmException;

/**
 * Nintendo Switch Online
 *
 * @author Lianjiannan
 * @since 2024/11/8 16:34
 **/
public interface NSOService {

    /**
     * 生成认证URL
     *
     * @param wxid 微信号
     * @return String
     */
    String generateAuthUrl(String wxid) throws NoSuchAlgorithmException;

    /**
     * 登陆
     *
     * @param wxid 微信号
     * @return void
     */
    void login(String wxid, String text);

    /**
     * 获取个人总览信息
     *
     * @param wxid 微信号
     * @return String
     */
    String getMeInfo(String wxid);

    /**
     * 获取最新一场打工信息
     *
     * @param wxid 微信号
     * @return String
     */
    String getLastCoop(String wxid);

    /**
     * 获取最后一场（真格/涂地）比赛信息
     *
     * @param wxid 微信号
     * @return String
     */
    String getLastMatch(String wxid);
}
