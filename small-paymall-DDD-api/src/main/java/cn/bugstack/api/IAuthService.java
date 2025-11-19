package cn.bugstack.api;

import cn.bugstack.api.response.Response;

public interface IAuthService {
    public Response<String> weixinQrCodeTicket();
    public Response<String> checkLogin(String ticket);
}
