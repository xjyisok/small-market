package cn.bugstack.domain.auth.service.impl;

import cn.bugstack.domain.auth.adapter.port.ILoginPort;
import cn.bugstack.domain.auth.service.ILoginService;
import com.google.common.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jms.JmsProperties;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

@Service
@Slf4j
public class WeixinLoginService implements ILoginService {
    @Resource
    private ILoginPort loginport;
    @Resource
    private Cache<String,String>openidToken;
    @Override
    public String createQrCodeTicket() throws Exception {
        return loginport.createQrCodeTicket();
    }

    @Override
    public void saveLoginState(String ticket, String openid) throws IOException {
        openidToken.put(ticket, openid);
        loginport.sendLoginTemplate(openid);
    }

    @Override
    public String checkLogin(String ticket) {
        return openidToken.getIfPresent(ticket);
    }
}
