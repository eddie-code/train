package com.example.member.controller.admin;

import com.example.common.resp.CommonResp;
import com.example.common.resp.PageResp;
import com.example.member.req.TicketQueryReq;
import com.example.member.resp.TicketQueryResp;
import com.example.member.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/ticket")
public class TicketAdminController {

    @Autowired
    private TicketService ticketService;

    @GetMapping("/query-list")
    public CommonResp<PageResp<TicketQueryResp>> queryList(@Valid TicketQueryReq req) {
        PageResp<TicketQueryResp> list = ticketService.queryList(req);
        return new CommonResp<>(list);
    }

}
