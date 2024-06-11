package com.example.${module}.service;

import com.example.common.resp.PageResp;
import com.example.${module}.req.${Domain}QueryReq;
import com.example.${module}.req.${Domain}SaveReq;
import com.example.${module}.resp.${Domain}QueryResp;

public interface ${Domain}Service {

    void save(${Domain}SaveReq req);

    PageResp<${Domain}QueryResp> queryList(${Domain}QueryReq req);

    void delete(Long id);

}