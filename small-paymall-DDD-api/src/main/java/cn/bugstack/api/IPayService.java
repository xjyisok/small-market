package cn.bugstack.api;

import cn.bugstack.api.dto.CreatePayRequestDTO;
import cn.bugstack.api.dto.NotifyRequestDTO;
import cn.bugstack.api.response.Response;

public interface IPayService {
    public Response<String> createPayOrder(CreatePayRequestDTO createPayRequestDTO);
    String groupBuyNotify(NotifyRequestDTO notifyRequestDTO);
}
